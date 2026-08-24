package one.theaq.skull.logic

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySelector
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import java.util.Optional
import java.util.UUID
import kotlin.jvm.optionals.getOrElse

class Skull(val level: ServerLevel) {
    val server = level.server
    var pos: Vec3 = Vec3.ZERO
    var targetUUID: Optional<UUID> = Optional.empty()
    var recentlyKilled: MutableMap<UUID, Int> = mutableMapOf()

    fun setPos(newPos: Vec3) {
        pos = newPos
    }

    fun tick() {
        recentlyKilled.forEach { (tUUID, tick) -> if (server.tickCount - tick > 200) recentlyKilled.remove(tUUID) }
        val targetPlayer = getTarget()

        move()


    }

    fun move() {

    }

    fun checkCollisions() {

    }

    fun getTarget(): Optional<Entity> {
        val uuid = targetUUID.getOrElse { getNewTarget() }

        return Optional.ofNullable(level.getEntity(uuid))
    }

    fun getNewTarget(): UUID {
        val playerTargets = level.getPlayers(EntitySelector.NO_SPECTATORS)
        val newTarget = playerTargets.random()

        return newTarget.uuid
    }

    fun onCollision(collider: Entity) {
        if (collider.uuid != targetUUID) return

        collider.kill(level)
    }
}