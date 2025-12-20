package org.robbie.yaha.registry

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.lib.hex.HexActions
import net.minecraft.registry.Registry
import org.robbie.yaha.Yaha
import org.robbie.yaha.features.anvil.OpAnvil
import org.robbie.yaha.features.paper_plane.OpPaperPlane
import org.robbie.yaha.features.paper_plane.OpPaperPlaneTarget
import org.robbie.yaha.features.spells.OpPotionToItem
import org.robbie.yaha.features.spells.OpSussifyBlock
import org.robbie.yaha.features.time_bomb.OpTimeBomb
import org.robbie.yaha.features.time_bomb.OpTimeBombPos

object YahaActions {
    fun register() {
        register("paper_plane", HexDir.NORTH_WEST, "wwqaqwwdw", OpPaperPlane)
        register("paper_plane_target", HexDir.NORTH_WEST, "wwqaqwwdedde", OpPaperPlaneTarget)
        register("time_bomb", HexDir.NORTH_WEST, "eewaqawee", OpTimeBomb)
        register("time_bomb_pos", HexDir.NORTH_WEST, "eewaqaweedd", OpTimeBombPos)
        register("anvil", HexDir.WEST, "dqdwdqdqaa", OpAnvil)

        register("sussify_block", HexDir.EAST, "eqqqeawqwqwqwqwqw", OpSussifyBlock)
        register("potion_to_item", HexDir.EAST, "dqqqqqedwda", OpPotionToItem)
    }

    private fun register(name: String, startDir: HexDir, sig: String, action: Action) =
        Registry.register(HexActions.REGISTRY, Yaha.id(name), ActionRegistryEntry(HexPattern.fromAngles(sig, startDir), action))
}