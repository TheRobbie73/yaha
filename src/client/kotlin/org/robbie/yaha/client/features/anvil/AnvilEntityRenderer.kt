package org.robbie.yaha.client.features.anvil

import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import org.robbie.yaha.Yaha
import org.robbie.yaha.client.registry.YahaEntitiesClient
import org.robbie.yaha.features.anvil.AnvilEntity


class AnvilEntityRenderer(context: EntityRendererFactory.Context) : EntityRenderer<AnvilEntity>(context) {
    val model: AnvilEntityModel<AnvilEntity> = AnvilEntityModel(context.getPart(YahaEntitiesClient.ANVIL))

    override fun render(
        entity: AnvilEntity,
        yaw: Float,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int
    ) {
        matrices.push()
        matrices.scale(-1f, -1f, 1f)

        val vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(getTexture(entity)))
        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f)

        matrices.pop()
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light)
    }

    override fun getTexture(entity: AnvilEntity?) = Yaha.id("textures/entity/anvil.png")
}