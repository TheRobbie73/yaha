package org.robbie.yaha.registry

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import org.robbie.yaha.Yaha
import org.robbie.yaha.features.anvil.AnvilEntity
import org.robbie.yaha.features.paper_plane.PaperPlaneEntity
import org.robbie.yaha.features.time_bomb.TimeBombEntity

object YahaEntities {
    val PAPER_PLANE_ENTITY: EntityType<PaperPlaneEntity> = EntityType.Builder
        .create(::PaperPlaneEntity, SpawnGroup.MISC)
        .setDimensions(0.5f, 0.5f)
        .build(Yaha.MOD_ID + ":paper_plane")

    val TIME_BOMB_ENTITY: EntityType<TimeBombEntity> = EntityType.Builder
        .create(::TimeBombEntity, SpawnGroup.MISC)
        .setDimensions(0.75f, 0.75f)
        .build(Yaha.MOD_ID + ":time_bomb")

    val ANVIL_ENTITY: EntityType<AnvilEntity> = EntityType.Builder
        .create(::AnvilEntity, SpawnGroup.MISC)
        .setDimensions(1f, 1f)
        .build(Yaha.MOD_ID + ":anvil")

    fun register() {
        register("paper_plane", PAPER_PLANE_ENTITY)
        register("time_bomb", TIME_BOMB_ENTITY)
        register("anvil", ANVIL_ENTITY)
    }

    private fun register(name: String, entityType: EntityType<out Entity>) {
        Registry.register(Registries.ENTITY_TYPE, Yaha.id(name), entityType)
    }
}