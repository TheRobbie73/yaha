package org.robbie.yaha.features.paper_plane

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityStatuses
import net.minecraft.entity.EntityType
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.entity.projectile.ProjectileUtil
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.ItemStackParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.robbie.yaha.registry.YahaEntities
import java.util.UUID

const val ACCELERATION = 0.1
const val DRAG = 0.9
const val MAX_AGE = 200

class PaperPlaneEntity(
    entityType: EntityType<out PaperPlaneEntity>,
    world: World
) : ProjectileEntity(entityType, world) {
    constructor(
        world: World,
        owner: Entity?,
        target: Entity?,
        pos: Vec3d
    ) : this(YahaEntities.PAPER_PLANE_ENTITY, world) {
        this.owner = owner
        setTarget(target)
        setPosition(pos)
    }

    private var target: Entity? = null
    private var targetUUID: UUID? = null

    override fun tick() {
        super.tick()

        if (!world.isClient && age > MAX_AGE) shatter()

        // check collision
        val hitResult = ProjectileUtil.getCollision(this, ::canHit)
        if (hitResult.type != HitResult.Type.MISS) onCollision(hitResult)

        // update position, rotation, and velocity
        setRotationFromVelocity() // fixed version of ProjectileUtil.setRotationFromVelocity()

        val oldVelocity = velocity
        val accelDirection = getTarget()?.eyePos?.subtract(pos) ?: rotationVector
        velocity = velocity
            .add(accelDirection.normalize().multiply(ACCELERATION))
            .multiply(DRAG)

        setPosition(pos.add(oldVelocity))

        checkBlockCollision() // other minecraft projectiles seem to call both onCollision AND checkBlockCollision
    }

    private fun setRotationFromVelocity() {
        if (velocity.lengthSquared() == 0.0) return
        yaw = (MathHelper.atan2(-velocity.x, velocity.z) * 180f / Math.PI).toFloat()
        pitch = (MathHelper.atan2(-velocity.y, velocity.horizontalLength()) * 180f / Math.PI).toFloat()
    }

    override fun canHit(entity: Entity) = super.canHit(entity) && (entity !is PaperPlaneEntity || target != entity)

    override fun canHit() = true

    override fun damage(source: DamageSource?, amount: Float): Boolean {
        if (isInvulnerableTo(source)) return false
        val entity = source?.attacker

        if (entity == null) return false

        if (!world.isClient) {
            velocity = entity.rotationVector
            owner = entity
            setTarget(null)
        }

        return true
    }

    fun getTarget(): Entity? {
        // also updates the target accordingly if it is null or removed

        target?.let {
            if (it.isRemoved) setTarget(null)
        } ?: if (targetUUID != null && world is ServerWorld) {
            setTarget((world as ServerWorld).getEntity(targetUUID))
        } else setTarget(null)

        return target
    }

    fun setTarget(entity: Entity?) {
        target = entity
        targetUUID = entity?.uuid
    }

    override fun onBlockHit(blockHitResult: BlockHitResult?) {
        shatter()
    }

    override fun onEntityHit(entityHitResult: EntityHitResult?) {
        entityHitResult?.entity?.damage(world.damageSources.create(DamageTypes.ARROW, this, owner), 2f)
        shatter()
    }

    private fun shatter() {
        playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, 0.25f, 1.5f) // sounds seem to work in here but not in handleStatus()
        world.sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES)
        discard()
    }

    override fun handleStatus(status: Byte) {
        if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
            // particles seem to work in here but not in shatter()
            val particleParam = ItemStackParticleEffect(ParticleTypes.ITEM, ItemStack(Items.AMETHYST_BLOCK, 1))
            for (i in 0..7) world.addParticle(particleParam, x, y, z, 0.0, 0.0, 0.0)
        }
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound?) {
        super.writeCustomDataToNbt(nbt)
        targetUUID?.let { nbt?.putUuid("Target", it) }
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound?) {
        super.readCustomDataFromNbt(nbt)
        if (nbt?.containsUuid("Target") ?: false) {
            targetUUID = nbt.getUuid("Target")
            target = null
        }
    }

    override fun hasNoGravity() = true
    override fun initDataTracker() {}
}