package org.robbie.yaha

import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.robbie.yaha.registry.YahaActions
import org.robbie.yaha.registry.YahaEntities
import org.robbie.yaha.registry.YahaItems

class Yaha : ModInitializer {

    override fun onInitialize() {
        YahaActions.register()
        YahaEntities.register()
        YahaItems.register()
    }

    companion object {
        const val MOD_ID: String = "yaha"
        @JvmStatic fun id(string: String) = Identifier(MOD_ID, string)
    }
}
