package org.robbie.yaha.registry

import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import org.robbie.yaha.Yaha

object YahaDamageTypes {
    val PAPER_PLANE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Yaha.id("paper_plane"))
}