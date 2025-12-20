package org.robbie.yaha.registry

import dev.onyxstudios.cca.api.v3.component.ComponentKey
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy
import org.robbie.yaha.Yaha
import org.robbie.yaha.features.bundles.CCBundleSelect

class YahaCardinalComponents : EntityComponentInitializer {
    override fun registerEntityComponentFactories(registry: EntityComponentFactoryRegistry) {
        registry.registerForPlayers(BUNDLE_SELECT, ::CCBundleSelect, RespawnCopyStrategy.NEVER_COPY)
    }

    companion object {
        val BUNDLE_SELECT: ComponentKey<CCBundleSelect> = ComponentRegistry.getOrCreate(Yaha.id("bundle_select"),
            CCBundleSelect::class.java)
    }
}