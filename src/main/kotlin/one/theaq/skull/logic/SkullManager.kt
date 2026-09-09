package one.theaq.skull.logic

import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.phys.Vec3
import java.util.UUID

class SkullManager {

    private val skulls: MutableList<Skull> = mutableListOf()
    private val markedForRemoval: MutableList<Skull> = mutableListOf()

    fun createSkull(level: ServerLevel): Skull {
        return this.createSkull(level, Vec3(0.0, 0.0, 0.0))
    }

    fun createSkull(level: ServerLevel, pos: Vec3): Skull  {
        val skull = Skull(this, level)
        skull.pos = pos

        skulls += skull
        return skull
    }

    fun removeSkull(skull: Skull): Int {
        if (!skulls.contains(skull)) return -1

        markedForRemoval += skull
        return 0
    }

    fun removeAllSkulls(): Int {
        markedForRemoval.addAll(skulls)
        return 0
    }

    fun getSkulls(level: ServerLevel, pos: Vec3, radius: Double = -1.0): List<Skull> {
        return skulls.filter { it.level == level && (radius < 0 || pos.subtract(it.pos).length() < radius) }
    }

    fun getAllSkulls(): List<Skull> {
        return skulls.toList()
    }

    fun tickSkulls(server: MinecraftServer) {
        markedForRemoval.removeIf {
            it.destroy()
            skulls.remove(it)
            return@removeIf true
        }

        skulls.forEach {
            it.tick()
        }
    }

    companion object {
        val INSTANCE: SkullManager = SkullManager()
    }
}