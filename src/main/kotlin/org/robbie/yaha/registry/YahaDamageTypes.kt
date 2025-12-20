package org.robbie.yaha.registry

import net.minecraft.entity.damage.DamageType
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import org.robbie.yaha.Yaha

object YahaDamageTypes {
    val PAPER_PLANE: RegistryKey<DamageType> = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Yaha.id("paper_plane"))
    val ANVIL: RegistryKey<DamageType> = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Yaha.id("anvil"))

    fun register() {}
}