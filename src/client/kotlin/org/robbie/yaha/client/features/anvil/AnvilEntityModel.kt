package org.robbie.yaha.client.features.anvil

import net.minecraft.client.model.ModelData
import net.minecraft.client.model.ModelPart
import net.minecraft.client.model.ModelPartBuilder
import net.minecraft.client.model.ModelTransform
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.render.entity.model.SinglePartEntityModel
import net.minecraft.entity.Entity

class AnvilEntityModel<T : Entity>(val root: ModelPart) : SinglePartEntityModel<T>() {
    override fun getPart() = root
    override fun setAngles(
        entity: T,
        limbAngle: Float,
        limbDistance: Float,
        animationProgress: Float,
        headYaw: Float,
        headPitch: Float
    ) {}

    companion object {
        fun getTexturedModelData(): TexturedModelData {
            val modelData = ModelData()
            val modelPartData = modelData.root

            modelPartData.addChild(
                "main",
                ModelPartBuilder.create()
                    .uv(0, 0)
                    .cuboid(-5f, -16f, -8f, 10f, 6f, 16f)
                    .uv(0, 22)
                    .cuboid(-2f, -10f, -4f, 4f, 5f, 8f)
                    .uv(0, 35)
                    .cuboid(-4f, -5f, -5f, 8f, 1f, 10f)
                    .uv(0, 46)
                    .cuboid(-6f, -4f, -6f, 12f, 4f, 12f),
                ModelTransform.NONE
            )

            return TexturedModelData.of(modelData, 64, 64)
        }
    }
}