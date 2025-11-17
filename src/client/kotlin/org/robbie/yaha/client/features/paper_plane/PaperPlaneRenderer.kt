package org.robbie.yaha.client.features.paper_plane

import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.ProjectileEntityRenderer
import net.minecraft.util.Identifier
import org.robbie.yaha.Yaha
import org.robbie.yaha.features.paper_plane.PaperPlaneEntity

class PaperPlaneRenderer(context: EntityRendererFactory.Context) : ProjectileEntityRenderer<PaperPlaneEntity>(context) {
    override fun getTexture(entity: PaperPlaneEntity?): Identifier? = Yaha.id("textures/entity/paper_plane.png")
}