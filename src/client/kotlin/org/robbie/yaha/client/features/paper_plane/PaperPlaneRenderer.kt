package org.robbie.yaha.client.features.paper_plane

import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RotationAxis
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.robbie.yaha.Yaha
import org.robbie.yaha.features.paper_plane.PaperPlaneEntity

class PaperPlaneRenderer(context: EntityRendererFactory.Context) : EntityRenderer<PaperPlaneEntity>(context) {
    override fun render(
        entity: PaperPlaneEntity,
        yaw: Float,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int
    ) {
        matrices.push()
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevYaw, entity.yaw)))
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevPitch, entity.pitch)))
        matrices.scale(0.9f/16f, 0.9f/16f, 0.9f/16f)
        matrices.translate(0f, 0f, -4f)

        val vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(getTexture(entity)))

        val entry = matrices.peek()
        val posMat = entry.positionMatrix
        val normMat = entry.normalMatrix

        vertex(posMat, normMat, vertexConsumer, -4, 0, -8, 0f, 0.5f, 0, 1, 0, light)
        vertex(posMat, normMat, vertexConsumer, -4, 0, 8, 1f, 0.5f, 0, 1, 0, light)
        vertex(posMat, normMat, vertexConsumer, 4, 0, 8, 1f, 0f, 0, 1, 0, light)
        vertex(posMat, normMat, vertexConsumer, 4, 0, -8, 0f, 0f, 0, 1, 0, light)

        vertex(posMat, normMat, vertexConsumer, -4, 0, -8, 0f, 0.5f, 0, -1, 0, light)
        vertex(posMat, normMat, vertexConsumer, 4, 0, -8, 0f, 0f, 0, -1, 0, light)
        vertex(posMat, normMat, vertexConsumer, 4, 0, 8, 1f, 0f, 0, -1, 0, light)
        vertex(posMat, normMat, vertexConsumer, -4, 0, 8, 1f, 0.5f, 0, -1, 0, light)

        vertex(posMat, normMat, vertexConsumer, 0, -3, -8, 0f, 11f/16f, 1, 0, 0, light)
        vertex(posMat, normMat, vertexConsumer, 0, 0, -8, 0f, 0.5f, 1, 0, 0, light)
        vertex(posMat, normMat, vertexConsumer, 0, 0, 8, 1f, 0.5f, 1, 0, 0, light)
        vertex(posMat, normMat, vertexConsumer, 0, -3, 8, 1f, 11f/16f, 1, 0, 0, light)

        vertex(posMat, normMat, vertexConsumer, 0, -3, -8, 0f, 11f/16f, -1, 0, 0, light)
        vertex(posMat, normMat, vertexConsumer, 0, -3, 8, 1f, 11f/16f, -1, 0, 0, light)
        vertex(posMat, normMat, vertexConsumer, 0, 0, 8, 1f, 0.5f, -1, 0, 0, light)
        vertex(posMat, normMat, vertexConsumer, 0, 0, -8, 0f, 0.5f, -1, 0, 0, light)

        matrices.pop()
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light)
    }

    fun vertex(
        positionMatrix: Matrix4f,
        normalMatrix: Matrix3f,
        vertexConsumer: VertexConsumer,
        x: Int, y: Int, z: Int,
        u: Float, v: Float,
        normalX: Int, normalY: Int, normalZ: Int,
        light: Int
    ) {
        vertexConsumer.vertex(positionMatrix, x.toFloat(), y.toFloat(), z.toFloat())
            .color(255, 255, 255, 255)
            .texture(u, v)
            .overlay(OverlayTexture.DEFAULT_UV)
            .light(light)
            .normal(normalMatrix, normalX.toFloat(), normalY.toFloat(), normalZ.toFloat())
            .next()
    }

    override fun getTexture(entity: PaperPlaneEntity) = Yaha.id("textures/entity/paper_plane.png")
}