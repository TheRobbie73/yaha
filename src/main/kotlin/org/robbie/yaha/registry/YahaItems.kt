package org.robbie.yaha.registry

import at.petrak.hexcasting.common.items.storage.ItemFocus
import at.petrak.hexcasting.common.items.storage.ItemThoughtKnot
import net.minecraft.item.FoodComponent
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Rarity
import org.robbie.yaha.Yaha
import org.robbie.yaha.features.bundles.IotaHolderBundle

object YahaItems {
    val TIME_BOMB = Item(Item.Settings().maxCount(1).food(FoodComponent.Builder().hunger(2).alwaysEdible().build()).rarity(Rarity.UNCOMMON))
    val SPINDLE = IotaHolderBundle(Item.Settings().maxCount(1), { it is ItemThoughtKnot }) // HexItems is not initialised! cant use THOUGHT_KNOT!!!
    val POUCH = IotaHolderBundle(Item.Settings().maxCount(1), { it is ItemFocus })

    fun register() {
        register("time_bomb", TIME_BOMB)
        register("spindle", SPINDLE)
        register("pouch", POUCH)
    }

    private fun register(name: String, item: Item) {
        Registry.register(Registries.ITEM, Yaha.id(name), item)
    }
}