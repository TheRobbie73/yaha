package org.robbie.yaha.client.registry

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.FlyingItemEntityRenderer
import org.robbie.yaha.client.features.paper_plane.PaperPlaneRenderer
import org.robbie.yaha.registry.YahaEntities

object YahaEntitiesClient {
    fun register() {
        EntityRendererRegistry.register(YahaEntities.PAPER_PLANE_ENTITY, ::PaperPlaneRenderer)
        EntityRendererRegistry.register(YahaEntities.TIME_BOMB_ENTITY, {context -> FlyingItemEntityRenderer(context, 2f, true)})
    }
}