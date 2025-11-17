package org.robbie.yaha

import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.robbie.yaha.registry.YahaEntities

class Yaha : ModInitializer {

    override fun onInitialize() {
        YahaEntities.register()
    }

    companion object {
        const val MOD_ID: String = "yaha"
        @JvmStatic fun id(string: String) = Identifier(MOD_ID, string)
    }
}
