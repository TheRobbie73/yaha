package org.robbie.yaha.features.spells

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.projectile.thrown.PotionEntity
import net.minecraft.entity.projectile.thrown.ThrownItemEntity
import org.robbie.yaha.features.time_bomb.TimeBombCastEnv
import org.robbie.yaha.features.time_bomb.TimeBombEntity

object OpPotionToItem : SpellAction {
    override val argc = 1

    override fun execute(
        args: List<Iota>,
        env: CastingEnvironment
    ): SpellAction.Result {
        val entity = args.getEntity(0, argc)

        // a time bomb can disarm itself and only itself
        if (
            entity !is PotionEntity &&
            entity !is TimeBombEntity ||
            env is TimeBombCastEnv &&
            entity != env.getBomb()
        ) throw MishapBadEntity.of(entity, "yaha:potion")

        env.assertEntityInRange(entity)

        return SpellAction.Result(
            Spell(entity),
            MediaConstants.SHARD_UNIT,
            listOf(ParticleSpray.cloud(entity.pos, 1.0))
        )
    }

    /**
     * Easter Egg Specification:
     * Normally, this spell takes a PotionEntity and replaces it with an ItemEntity of the same potion.
     * However, if a Time Bomb were to cast this spell on itself (it must be cast by itself and on itself),
     * it should drop itself as an item AND end it's running hex (like Janus from Overevaluate).
     */
    override fun operate(
        env: CastingEnvironment,
        image: CastingImage,
        continuation: SpellContinuation
    ): OperationResult {
        // jank levels spiking!!
        val stackTop = image.stack.lastOrNull()
        val isBomb = (
                env is TimeBombCastEnv &&
                stackTop is EntityIota &&
                stackTop.entity is TimeBombEntity &&
                env.getBomb() == stackTop.entity
                )

        val opResult = super.operate(env, image, continuation)

        return if (!isBomb) opResult else opResult.copy(newContinuation = SpellContinuation.Done)
    }

    private data class Spell(val potion: ThrownItemEntity) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            val pos = potion.pos
            val vel = potion.velocity
            val item = potion.stack
            if (potion !is TimeBombEntity) potion.discard()
            val itemEntity = ItemEntity(
                env.world,
                pos.x, pos.y, pos.z,
                item,
                vel.x, vel.y, vel.z
            )
            itemEntity.setToDefaultPickupDelay()
            env.world.spawnEntity(itemEntity)
        }
    }
}