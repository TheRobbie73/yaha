package org.robbie.yaha

import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import net.minecraft.util.math.random.Random
import org.robbie.yaha.registry.YahaActions
import org.robbie.yaha.registry.YahaCriteria
import org.robbie.yaha.registry.YahaEntities
import org.robbie.yaha.registry.YahaItems

class Yaha : ModInitializer {

    override fun onInitialize() {
        YahaActions.register()
        YahaCriteria.register()
        YahaEntities.register()
        YahaItems.register()
    }

    companion object {
        const val MOD_ID: String = "yaha"
        val RANDOM: Random = Random.create() // if world.random cannot be used
        fun id(string: String) = Identifier(MOD_ID, string)
    }
}
