package org.robbie.yaha.client.features.bundles

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.screen.slot.Slot
import org.robbie.yaha.features.bundles.IotaHolderBundle
import org.robbie.yaha.registry.YahaCardinalComponents

object IotaBundleTooltipHandler {
    var hoveredSlot: Slot? = null
    var selected: Int = 0

    // select slot in bundle with scroll wheel
    fun beforeMouseScroll(screen: Screen, mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double) {
        if (verticalAmount == 0.0) return
        if (screen !is HandledScreen<*>) return

        val slot = hoveredSlot
        if (slot == null || !slot.hasStack()) return
        val itemStack = slot.stack
        if (itemStack.item !is IotaHolderBundle) return
        val count = IotaHolderBundle.getBundleOccupancy(itemStack)
        if (count == 0) return

        syncSelected((selected + if (verticalAmount < 0) 1 else -1).mod(count))
    }

    // reset the selected slot when hovering over a new bundle
    fun beforeRender(screen: Screen, drawContext: DrawContext, mouseX: Int, mouseY: Int, tickDelta: Float) {
        if (screen !is HandledScreen<*>) return
        val slot = getHoveredSlot(screen, mouseX, mouseY)
        if (slot != hoveredSlot) syncSelected(0)
        hoveredSlot = slot
    }

    // selected is stored on this client-side object to make sure the tooltip doesnt look laggy;
    // tooltip will use IotaBundleTooltipHandler.selected while item will use CCBundleSelect.selected
    private fun syncSelected(newSelect: Int) {
        selected = newSelect
        val player = MinecraftClient.getInstance().player
        if (player == null) return
        YahaCardinalComponents.BUNDLE_SELECT.get(player).syncSelected(selected)
    }

    private fun getHoveredSlot(screen: HandledScreen<*>, mouseX: Int, mouseY: Int): Slot? {
        for (slot in screen.screenHandler.slots) {
            if (
                mouseX >= screen.x + slot.x &&
                mouseX <= screen.x + slot.x + 16 &&
                mouseY >= screen.y + slot.y &&
                mouseY <= screen.y + slot.y + 16
            ) return slot
        }
        return null
    }
}