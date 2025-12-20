package org.robbie.yaha.client.registry

import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents
import net.minecraft.client.item.ModelPredicateProviderRegistry
import net.minecraft.util.Identifier
import org.robbie.yaha.client.features.bundles.IotaBundleTooltipComponent
import org.robbie.yaha.client.features.bundles.IotaBundleTooltipHandler
import org.robbie.yaha.features.bundles.IotaHolderBundle
import org.robbie.yaha.registry.YahaItems

object YahaItemsClient {
    fun register() {
        ModelPredicateProviderRegistry.register(
            YahaItems.SPINDLE,
            Identifier("filled")
        ) { itemStack, clientWorld, livingEntity, seed ->
            IotaHolderBundle.getBundleOccupancy(itemStack).toFloat() / IotaHolderBundle.MAX_COUNT.toFloat()
        }

        ModelPredicateProviderRegistry.register(
            YahaItems.POUCH,
            Identifier("filled")
        ) { itemStack, clientWorld, livingEntity, seed ->
            IotaHolderBundle.getBundleOccupancy(itemStack).toFloat() / IotaHolderBundle.MAX_COUNT.toFloat()
        }

        TooltipComponentCallback.EVENT.register { data -> IotaBundleTooltipComponent.of(data) }
        ScreenEvents.AFTER_INIT.register { client, screen, scaledWidth, scaledHeight ->
            ScreenMouseEvents.beforeMouseScroll(screen).register(
                ScreenMouseEvents.BeforeMouseScroll(IotaBundleTooltipHandler::beforeMouseScroll)
            )
            ScreenEvents.beforeRender(screen).register(
                ScreenEvents.BeforeRender(IotaBundleTooltipHandler::beforeRender)
            )
        }
    }
}