package org.robbie.yaha.client

import net.fabricmc.api.ClientModInitializer
import org.robbie.yaha.client.registry.YahaEntitiesClient

class YahaClient : ClientModInitializer {

    override fun onInitializeClient() {
        YahaEntitiesClient.register()
    }
}
