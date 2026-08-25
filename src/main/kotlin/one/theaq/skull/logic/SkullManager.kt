package one.theaq.skull.logic

import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.phys.Vec3

class SkullManager {

    private val skulls: MutableList<Skull> = mutableListOf()

    fun tickSkulls(server: MinecraftServer) {
        skulls.forEach {
            it.tick()
        }
    }

    fun createSkull(level: ServerLevel) {
        this.createSkull(level, Vec3(0.0, 0.0, 0.0))
    }

    fun createSkull(level: ServerLevel, pos: Vec3) {
        val skull = Skull(level)
        skull.pos = pos

        skulls += skull
    }

    companion object {
        val INSTANCE: SkullManager = SkullManager()
    }
}