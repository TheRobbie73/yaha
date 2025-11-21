package org.robbie.yaha.registry

import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import org.robbie.yaha.Yaha

object YahaItems {
    val TIME_BOMB_ITEM = Item(Item.Settings())

    fun register() {
        Registry.register(Registries.ITEM, Yaha.id("time_bomb"), TIME_BOMB_ITEM)
    }
}