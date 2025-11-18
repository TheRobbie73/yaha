package org.robbie.yaha.registry

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.lib.hex.HexActions
import net.minecraft.registry.Registry
import org.robbie.yaha.Yaha
import org.robbie.yaha.features.paper_plane.OpPaperPlane

object YahaActions {
    fun register() {
        register("paper_plane", HexDir.NORTH_WEST, "wwqaqwwdw", OpPaperPlane)
    }

    private fun register(name: String, startDir: HexDir, sig: String, action: Action) =
        Registry.register(HexActions.REGISTRY, Yaha.id(name), ActionRegistryEntry(HexPattern.fromAngles(sig, startDir), action))
}