package one.theaq.skull.logic

import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.phys.Vec3
import java.util.*

class SkullManager {
    private val skulls: MutableList<Skull> = mutableListOf()
    private val markedForRemoval: MutableList<Skull> = mutableListOf()
    private val killedBySkull: MutableList<UUID> = mutableListOf()
    private val entityTargeted: MutableList<UUID> = mutableListOf()

    fun createSkull(level: ServerLevel): Skull {
        return this.createSkull(level)
    }

    fun createSkull(level: ServerLevel, pos: Vec3 = Vec3(0.0, 0.0, 0.0), target: Optional<LivingEntity> = Optional.empty()): Skull  {
        val skull = Skull(this, level, pos, target)

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

    fun addToKilledBySkull(uuid: UUID) {
        killedBySkull.add(uuid)
    }

    fun removeFromKilledBySkull(uuid: UUID): Boolean {
        return killedBySkull.remove(uuid)
    }

    fun addToTargeted(uuid: UUID) {
        entityTargeted.add(uuid)
    }

    fun isTargetedBySkull(uuid: UUID): Boolean {
        return entityTargeted.contains(uuid)
    }

    fun removeFromTargeted(uuid: UUID): Boolean {
        return entityTargeted.remove(uuid)
    }

    companion object {
        val INSTANCE: SkullManager = SkullManager()
    }
}