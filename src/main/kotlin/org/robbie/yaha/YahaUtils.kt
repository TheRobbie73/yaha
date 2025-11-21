package org.robbie.yaha

import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d

object YahaUtils {
    /**
     * Returns the pitch and yaw for a given direction vector, or null if it is the zero vector
     */
    fun pitchYawFromRotVec(rotVec: Vec3d): Pair<Float, Float>? {
        if (rotVec.lengthSquared() == 0.0) return null
        val yaw = (MathHelper.atan2(-rotVec.x, rotVec.z) * 180f / Math.PI).toFloat()
        val pitch = (MathHelper.atan2(-rotVec.y, rotVec.horizontalLength()) * 180f / Math.PI).toFloat()
        return pitch to yaw
    }
}