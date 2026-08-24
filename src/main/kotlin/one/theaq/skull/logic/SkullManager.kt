package one.theaq.skull.logic

import net.minecraft.world.phys.Vec3

class SkullManager {

    val skulls: MutableList<Skull> = mutableListOf()

    fun tickSkulls() {
        skulls.forEach {
            it.tick()
        }
    }

    fun createSkull() {

    }

    fun createSkull(pos: Vec3) {

    }

    companion object {
        val INSTANCE: SkullManager = SkullManager()
    }
}