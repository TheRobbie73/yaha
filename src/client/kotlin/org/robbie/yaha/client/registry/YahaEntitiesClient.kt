package org.robbie.yaha.client.registry

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import org.robbie.yaha.client.features.paper_plane.PaperPlaneRenderer
import org.robbie.yaha.registry.YahaEntities

object YahaEntitiesClient {
    fun register() {
        EntityRendererRegistry.register(YahaEntities.PAPER_PLANE_ENTITY, ::PaperPlaneRenderer)
    }
}