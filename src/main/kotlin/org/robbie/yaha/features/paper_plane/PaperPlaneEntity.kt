package org.robbie.yaha.features.paper_plane

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityStatuses
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World
import org.robbie.yaha.registry.YahaEntities
import java.util.UUID

const val ACCELERATION = 0.01
const val MAX_VELOCITY = 0.05

class PaperPlaneEntity(
    entityType: EntityType<out PaperPlaneEntity>,
    world: World
) : PersistentProjectileEntity(entityType, world) {
    private var target: Entity? = null
    private var targetUUID: UUID? = null

    override fun tick() {
        super.tick()
        val accelDirection = getTarget()?.pos?.subtract(pos) ?: rotationVector
        velocity = velocity.add(accelDirection.normalize().multiply(ACCELERATION))
        if (velocity.length() > MAX_VELOCITY) velocity.normalize().multiply(MAX_VELOCITY) // why no clamp.., where clamp..

        if (!world.isClient && (inGround || age > 200)) shatter()
    }

    fun getTarget(): Entity? {
        return target ?: if (targetUUID != null && world is ServerWorld) {
            target = (world as ServerWorld).getEntity(targetUUID)
            return target
        } else null
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