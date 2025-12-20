package org.robbie.yaha.client.features.bundles

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.client.item.TooltipData
import org.robbie.yaha.Yaha
import org.robbie.yaha.features.bundles.IotaBundleTooltipData

val TEXTURE = Yaha.id("textures/gui/container/bundles.png")

class IotaBundleTooltipComponent(data: IotaBundleTooltipData) : TooltipComponent {
    val inventory = data.inventory

    override fun drawItems(textRenderer: TextRenderer, x: Int, y: Int, context: DrawContext) {
        drawSlots(x, y, context)
        drawItems(x, y, context, textRenderer)
        drawSelected(x, y, context, textRenderer)
    }

    private fun drawSlots(x: Int, y: Int, drawContext: DrawContext) {
        repeat(getRows()) { i ->
            repeat(4) { j ->
                drawContext.drawTexture(
                    TEXTURE,
                    x + j * 18 + 4,
                    y + i * 18 + 4,
                    0,
                    0f, 0f,
                    18, 18,
                    64, 64
                )
            }
        }
    }

    private fun drawItems(x: Int, y: Int, drawContext: DrawContext, textRenderer: TextRenderer) {
        for ((idx, itemStack) in inventory.withIndex()) {
            val i = x + idx.rem(4) * 18 + 5
            val j = y + idx.floorDiv(4) * 18 + 5
            drawContext.drawItem(itemStack, i, j)
            drawContext.drawItemInSlot(textRenderer, itemStack, i, j)
        }
    }

    private fun drawSelected(x: Int, y: Int, drawContext: DrawContext, textRenderer: TextRenderer) {
        if (getRows() == 0) return
        val selected = IotaBundleTooltipHandler.selected

        val i = x + selected.rem(4) * 18 + 1
        val j = y + selected.floorDiv(4) * 18

        drawContext.drawTexture(
            TEXTURE,
            i, j, 0,
            0f, 18f,
            24, 24,
            64, 64
        )

        val item = inventory.getOrNull(selected) ?: return
        drawContext.drawItemTooltip(textRenderer, item, i + 20, j + 20)
    }

    override fun getHeight() = getRows() * 18 + 8
    override fun getWidth(textRenderer: TextRenderer) = 4 * 18 + 8

    // no Int.ceilDiv :pensive:
    private fun getRows() = inventory.size.floorDiv(4) + if (inventory.size.rem(4) != 0) 1 else 0

    companion object {
        fun of(data: TooltipData): TooltipComponent? {
            if (data !is IotaBundleTooltipData) return null
            return IotaBundleTooltipComponent(data)
        }
    }
}