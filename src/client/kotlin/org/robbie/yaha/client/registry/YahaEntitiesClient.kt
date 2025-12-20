package org.robbie.yaha.client.registry

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.client.render.entity.FlyingItemEntityRenderer
import net.minecraft.client.render.entity.model.EntityModelLayer
import org.robbie.yaha.Yaha
import org.robbie.yaha.client.features.anvil.AnvilEntityModel
import org.robbie.yaha.client.features.anvil.AnvilEntityRenderer
import org.robbie.yaha.client.features.paper_plane.PaperPlaneRenderer
import org.robbie.yaha.registry.YahaEntities

object YahaEntitiesClient {
    val ANVIL: EntityModelLayer = EntityModelLayer(Yaha.id("anvil"), "main")

    fun register() {
        EntityRendererRegistry.register(YahaEntities.PAPER_PLANE_ENTITY, ::PaperPlaneRenderer)
        EntityRendererRegistry.register(YahaEntities.TIME_BOMB_ENTITY, {context -> FlyingItemEntityRenderer(context, 2f, true)})
        EntityRendererRegistry.register(YahaEntities.ANVIL_ENTITY, ::AnvilEntityRenderer)
        EntityModelLayerRegistry.registerModelLayer(ANVIL, AnvilEntityModel.Companion::getTexturedModelData)
    }
}