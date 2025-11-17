package org.robbie.yaha.registry

import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import org.robbie.yaha.Yaha
import org.robbie.yaha.features.paper_plane.PaperPlaneEntity

object YahaEntities {
    val PAPER_PLANE_ENTITY: EntityType<PaperPlaneEntity> = EntityType.Builder
        .create(::PaperPlaneEntity, SpawnGroup.MISC)
        .setDimensions(0.5f, 0.5f)
        .maxTrackingRange(4)
        .trackingTickInterval(20)
        .build(Yaha.MOD_ID + ":paper_plane")

    fun register() {
        Registry.register(Registries.ENTITY_TYPE, Yaha.id("paper_plane"), PAPER_PLANE_ENTITY)
    }
}