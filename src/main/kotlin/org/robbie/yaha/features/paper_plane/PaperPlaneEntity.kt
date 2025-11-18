package org.robbie.yaha.features.paper_plane

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityStatuses
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.ItemStackParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World
import org.robbie.yaha.registry.YahaEntities
import java.util.UUID

const val ACCELERATION = 0.05
const val MAX_VELOCITY = 0.1

class PaperPlaneEntity(
    entityType: EntityType<out PaperPlaneEntity>,
    world: World
) : PersistentProjectileEntity(entityType, world) {
    constructor(world: World) : this(YahaEntities.PAPER_PLANE_ENTITY, world)

    private var target: Entity? = null
    private var targetUUID: UUID? = null

    override fun tick() {
        val accelDirection = getTarget()?.eyePos?.subtract(pos) ?: rotationVector
        velocity = velocity.add(accelDirection.normalize().multiply(ACCELERATION))
        if (velocity.length() > MAX_VELOCITY) velocity.normalize().multiply(MAX_VELOCITY) // why no clamp.., where clamp..

        if (!world.isClient && (inGround || age > 200)) shatter()
        super.tick()
    }

    fun getTarget(): Entity? {
        return target ?: if (targetUUID != null && world is ServerWorld) {
            target = (world as ServerWorld).getEntity(targetUUID)
            return target
        } else null
    }

    fun setTarget(entity: Entity) {
        target = entity
        targetUUID = entity.uuid
    }

    override fun onBlockHit(blockHitResult: BlockHitResult?) {
        shatter()
    }

    override fun onEntityHit(entityHitResult: EntityHitResult?) {
        shatter()
    }

    private fun shatter() {
        world.sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES)
        discard()
    }

    override fun handleStatus(status: Byte) {
        if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
            // TODO - fix sound not playing???
            world.playSound(null, blockPos, SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, SoundCategory.NEUTRAL, 0.25f, 1.5f)

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

    override fun asItemStack() = null
    override fun hasNoGravity() = true
    override fun tryPickup(player: PlayerEntity?) = false
    override fun getHitSound(): SoundEvent = SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK
}