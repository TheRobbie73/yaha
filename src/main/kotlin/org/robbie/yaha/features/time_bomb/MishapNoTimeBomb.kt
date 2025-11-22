package org.robbie.yaha.features.time_bomb

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import net.minecraft.util.DyeColor
import net.minecraft.world.World

class MishapNoTimeBomb : Mishap() {
    override fun accentColor(
        ctx: CastingEnvironment,
        errorCtx: Context
    ): FrozenPigment = dyeColor(DyeColor.LIGHT_BLUE)

    override fun errorMessage(
        ctx: CastingEnvironment,
        errorCtx: Context
    ) = error("yaha:no_time_bomb")

    override fun execute(
        env: CastingEnvironment,
        errorCtx: Context,
        stack: MutableList<Iota>
    ) {
        val pos = env.castingEntity?.pos ?: return
        env.world.createExplosion(null, pos.x, pos.y, pos.z, 0.25f, World.ExplosionSourceType.NONE)
    }
}