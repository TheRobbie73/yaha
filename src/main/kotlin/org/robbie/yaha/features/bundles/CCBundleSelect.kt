package org.robbie.yaha.features.bundles

import dev.onyxstudios.cca.api.v3.component.Component
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import org.robbie.yaha.registry.YahaCardinalComponents

class CCBundleSelect(val owner: PlayerEntity) : Component, AutoSyncedComponent {
    var selected = 0

    fun syncSelected(newSelect: Int) {
        if (selected == newSelect) return
        selected = newSelect
        YahaCardinalComponents.BUNDLE_SELECT.sync(owner)
    }

    override fun readFromNbt(tag: NbtCompound) {
        selected = tag.getInt("Selected")
    }

    override fun writeToNbt(tag: NbtCompound) {
        tag.putInt("Selected", selected)
    }
}