package org.robbie.yaha.features.paper_plane

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity

object OpPaperPlaneTarget : ConstMediaAction {
    override val argc = 1

    override fun execute(
        args: List<Iota>,
        env: CastingEnvironment
    ): List<Iota> {
        val entity = args.getEntity(0, argc)
        val plane = entity as? PaperPlaneEntity
            ?: throw MishapBadEntity.of(entity, "yaha:paper_plane")
        env.assertEntityInRange(plane)

        return plane.getTarget().asActionResult
    }
}