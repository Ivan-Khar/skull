package one.theaq.skull.logic

import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.phys.Vec3

class SkullManager {

    private val skulls: MutableList<Skull> = mutableListOf()
    private val markedForRemoval: MutableList<Skull> = mutableListOf()

    fun createSkull(level: ServerLevel) {
        this.createSkull(level, Vec3(0.0, 0.0, 0.0))
    }

    fun createSkull(level: ServerLevel, pos: Vec3) {
        val skull = Skull(this, level)
        skull.pos = pos

        skulls += skull
    }

    fun removeSkull(skull: Skull): Int {
        if (!skulls.contains(skull)) return -1

        markedForRemoval += skull
        return 0
    }

    fun tickSkulls(server: MinecraftServer) {
        markedForRemoval.forEach {
            it.destroy()
            skulls.remove(it)
        }
        skulls.forEach {
            it.tick()
        }
    }

    companion object {
        val INSTANCE: SkullManager = SkullManager()
    }
}