package org.robbie.yaha.features.bundles

import net.minecraft.client.item.TooltipData
import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList

data class IotaBundleTooltipData(val inventory: DefaultedList<ItemStack>) : TooltipData