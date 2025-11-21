package org.robbie.yaha.features.paper_plane

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d

object OpPaperPlane : SpellAction {
    override val argc: Int = 2

    override fun execute(
        args: List<Iota>,
        env: CastingEnvironment
    ): SpellAction.Result {
        val pos = args.getVec3(0, argc)
        env.assertVecInRange(pos)
        val entity = args.getEntity(1, argc)
        env.assertEntityInRange(entity)
        return SpellAction.Result(
            Spell(pos, entity),
            MediaConstants.DUST_UNIT * 2,
            listOf(ParticleSpray.cloud(pos, 1.0))
        )
    }

    private data class Spell(val pos: Vec3d, val entity: Entity) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {}
        override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage {
            val plane = PaperPlaneEntity(
                env.world,
                env.castingEntity,
                entity,
                pos
            )
            env.world.spawnEntity(plane)
            return image.copy(stack = image.stack.toList().plus(EntityIota(plane)))
        }
    }
}