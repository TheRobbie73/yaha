package org.robbie.yaha.features.anvil

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityPose
import net.minecraft.entity.EntityStatuses
import net.minecraft.entity.EntityType
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.entity.projectile.ProjectileUtil
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.particle.ItemStackParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.robbie.yaha.Yaha
import org.robbie.yaha.registry.YahaDamageTypes
import org.robbie.yaha.registry.YahaEntities
import kotlin.math.pow

const val MAX_AGE = 600
const val GRAVITY = -0.04
const val DRAG = 0.98

class AnvilEntity(
    entityType: EntityType<out AnvilEntity>,
    world: World
) : ProjectileEntity(entityType, world) {
    constructor(
        world: World,
        owner: Entity?,
        pos: Vec3d
    ) : this(YahaEntities.ANVIL_ENTITY, world) {
        this.owner = owner
        setPosition(pos)
    }

    private var cooldown = 0

    override fun tick() {
        super.tick()

        if (!world.isClient && age > MAX_AGE) shatter()
        if (cooldown != 0) cooldown--

        val hitResult = ProjectileUtil.getCollision(this, ::canHit)
        if (hitResult.type != HitResult.Type.MISS) onCollision(hitResult)

        velocity = velocity.add(0.0, GRAVITY, 0.0)
        setPosition(pos.add(velocity))
        velocity = velocity.multiply(DRAG)

        checkBlockCollision()
    }

    override fun canHit(entity: Entity) = super.canHit(entity) && entity !is AnvilEntity
    override fun canHit() = true

    override fun onBlockHit(blockHitResult: BlockHitResult) {
        shatter()
    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        val entity = entityHitResult.entity
        if (entity is AnvilEntity || cooldown != 0) return

        val damage = 10 - 10 * (velocity.lengthSquared() / 5 + 1).pow(-2)
        entity.damage(world.damageSources.create(
            YahaDamageTypes.ANVIL,
            this,
            owner
        ), damage.toFloat())
        val entityVelocity = entity.velocity
        entity.velocity = velocity
        velocity = entityVelocity
        cooldown = 2

        playSound(SoundEvents.BLOCK_ANVIL_LAND, 0.2f, 1f)
        playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, 0.5f, 1f)
    }

    private fun shatter() {
        playSound(SoundEvents.BLOCK_ANVIL_LAND, 0.5f, 0.5f)
        playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, 1f, 0.5f)
        world.sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES)
        discard()
    }

    override fun handleStatus(status: Byte) {
        if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
            val particleParam = ItemStackParticleEffect(ParticleTypes.ITEM, ItemStack(Items.AMETHYST_BLOCK, 1))
            repeat(16) { world.addParticle(
                particleParam,
                x + Yaha.RANDOM.nextDouble() - 0.5,
                y + Yaha.RANDOM.nextDouble() - 0.5,
                z + Yaha.RANDOM.nextDouble() - 0.5,
                velocity.x,
                velocity.y,
                velocity.z
            ) }
        }
    }

    override fun getEyeHeight(pose: EntityPose, dimensions: EntityDimensions) = height / 2
    override fun initDataTracker() {}
}