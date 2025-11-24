package org.robbie.yaha.registry

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.common.items.storage.ItemFocus
import at.petrak.hexcasting.common.items.storage.ItemThoughtKnot
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.FoodComponent
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Rarity
import org.robbie.yaha.Yaha
import org.robbie.yaha.features.bundles.IotaHolderBundle

object YahaItems {
    val TIME_BOMB = Item(Item.Settings().maxCount(1).food(FoodComponent.Builder().hunger(2).alwaysEdible().build()).rarity(Rarity.UNCOMMON))
    val SPINDLE = IotaHolderBundle(Item.Settings().maxCount(1), { it is ItemThoughtKnot }) // HexItems is not initialised! cant use THOUGHT_KNOT!!!
    val POUCH = IotaHolderBundle(Item.Settings().maxCount(1), { it is ItemFocus })

    // uncomment when uhhhh 10 items in addon
    // val YAHA_GROUP = FabricItemGroup.builder()
    //     .icon { TIME_BOMB.defaultStack }
    //     .displayName(Text.translatable("itemGroup.yaha.yaha"))
    //     .entries { _, entries ->
    //         entries.add(TIME_BOMB.defaultStack)
    //         entries.add(SPINDLE.defaultStack)
    //         entries.add(POUCH.defaultStack)
    //     }
    //     .build()

    fun register() {
        register("time_bomb", TIME_BOMB)
        register("spindle", SPINDLE)
        register("pouch", POUCH)

        // Registry.register(Registries.ITEM_GROUP, RegistryKey.of(Registries.ITEM_GROUP.key, Yaha.id("yaha")), YAHA_GROUP)
        ItemGroupEvents.modifyEntriesEvent(
            RegistryKey.of(Registries.ITEM_GROUP.key, HexAPI.modLoc("hexcasting"))
        ).register {
            it.add(TIME_BOMB.defaultStack)
            it.add(SPINDLE.defaultStack)
            it.add(POUCH.defaultStack)
        }
    }

    private fun register(name: String, item: Item) {
        Registry.register(Registries.ITEM, Yaha.id(name), item)
    }
}