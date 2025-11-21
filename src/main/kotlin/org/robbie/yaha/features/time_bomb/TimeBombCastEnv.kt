package org.robbie.yaha.features.time_bomb

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.MishapEnvironment
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedMishapEnv
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.GameMode
import java.util.function.Predicate

class TimeBombCastEnv(world: ServerWorld?, private val bomb: TimeBombEntity) : CastingEnvironment(world) {
    private val AMBIT = 4.0
    private val player = bomb.owner as LivingEntity?

    override fun getCastingEntity() = player

    override fun getMishapEnvironment() = if (player is ServerPlayerEntity) PlayerBasedMishapEnv(player) else null

    override fun mishapSprayPos(): Vec3d = bomb.pos

    override fun extractMediaEnvironment(cost: Long, simulate: Boolean): Long {
        val remainder = cost - bomb.getMedia()
        if (!simulate) bomb.setMedia(-remainder)
        return remainder
    }

    override fun isVecInRangeEnvironment(vec: Vec3d) =
        vec.squaredDistanceTo(bomb.pos) <= AMBIT * AMBIT + 0.00000000001

    override fun hasEditPermissionsAtEnvironment(pos: BlockPos?): Boolean {
        if (player !is ServerPlayerEntity) return false
        return player.interactionManager.gameMode != GameMode.ADVENTURE && world.canPlayerModifyAt(player, pos)
    }

    override fun getCastingHand() = Hand.MAIN_HAND

    override fun getUsableStacks(mode: StackDiscoveryMode?): List<ItemStack?>? {
        if (player !is ServerPlayerEntity) return mutableListOf()
        return getUsableStacksForPlayer(mode, null, player)
    }

    override fun getPrimaryStacks(): List<HeldItemInfo?>? {
        if (player !is ServerPlayerEntity) return mutableListOf()
        return getPrimaryStacksForPlayer(castingHand, player)
    }

    override fun replaceItem(
        stackOk: Predicate<ItemStack?>?,
        replaceWith: ItemStack?,
        hand: Hand?
    ): Boolean {
        if (player !is ServerPlayerEntity) return false
        return  replaceItemForPlayer(stackOk, replaceWith, hand, player)
    }

    override fun getPigment(): FrozenPigment = bomb.pigment

    override fun setPigment(pigment: FrozenPigment?) = null

    override fun produceParticles(
        particles: ParticleSpray?,
        colorizer: FrozenPigment?
    ) {
        particles?.sprayParticles(world, pigment)
    }

    override fun printMessage(message: Text?) {
        if (player is ServerPlayerEntity) player.sendMessage(message)
    }
}