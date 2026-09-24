package org.robbie.yaha

import at.petrak.hexcasting.interop.HexInterop
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.util.Identifier
import net.minecraft.util.math.random.Random
import org.robbie.yaha.registry.YahaActions
import org.robbie.yaha.registry.YahaCriteria
import org.robbie.yaha.registry.YahaDamageTypes
import org.robbie.yaha.registry.YahaEntities
import org.robbie.yaha.registry.YahaItems
import org.robbie.yaha.registry.YahaSounds
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import vazkii.patchouli.api.PatchouliAPI

class Yaha : ModInitializer {

    override fun onInitialize() {
        YahaActions.register()
        YahaCriteria.register()
        YahaDamageTypes.register()
        YahaEntities.register()
        YahaItems.register()
        YahaSounds.register()

        // ough
        if (FabricLoader.getInstance().isModLoaded("spectrum")) {
            PatchouliAPI.get().setConfigFlag(HexInterop.PATCHOULI_ANY_INTEROP_FLAG, true)
        }
    }

    companion object {
        const val MOD_ID: String = "yaha"
        val RANDOM: Random = Random.create() // if world.random cannot be used
        val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)
        fun id(string: String) = Identifier(MOD_ID, string)
    }
}
