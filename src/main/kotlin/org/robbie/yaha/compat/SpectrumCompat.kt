package org.robbie.yaha.compat

import de.dafuqs.spectrum.recipe.anvil_crushing.AnvilCrusher
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.util.TypeFilter
import org.robbie.yaha.features.anvil.AnvilEntity
import kotlin.math.ceil

object SpectrumCompat {
    fun crush(anvil: AnvilEntity) {
        if (anvil.world.isClient) return
        anvil.world.getEntitiesByType(
            TypeFilter.instanceOf(ItemEntity::class.java),
            anvil.boundingBox,
            Entity::isAlive
        ).forEach { entity -> AnvilCrusher.crush(
            entity,
            ceil(20 * anvil.velocity.length()).toFloat()
        ) }
    }
}