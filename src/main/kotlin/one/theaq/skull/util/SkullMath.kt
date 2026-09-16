package one.theaq.skull.util

object SkullMath {
    fun map(x: Double, inMin: Double, inMax: Double, outMin: Double, outMax: Double): Double {
        return (x - inMin) * (outMax - outMin) / (inMax - inMin) + outMin
    }
}