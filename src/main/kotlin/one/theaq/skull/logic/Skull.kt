package one.theaq.skull.logic

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.phys.AABB
import java.util.Optional
import java.util.UUID
import kotlin.jvm.optionals.getOrElse

class Skull(val level: Level, ) {

    var posX: Double = 0.0
    var posY: Double = 0.0
    var posZ: Double = 0.0
    var targetUUID: Optional<UUID> = Optional.empty()
    var recentlyKilled: MutableMap<UUID, Int> = mutableMapOf<UUID, Int>()

    fun tick() {
        val targetPlayer = getTarget()
    }

    fun move() {

    }

    fun getTarget(): Optional<LivingEntity> {
        val uuid = targetUUID.getOrElse { getNewTarget() }

        return Optional.ofNullable(level.getPlayerByUUID(uuid))
    }

    fun getNewTarget(): UUID {
        val playerTargets = level.getEntitiesOfClass(
            Player::class.java,
            AABB.of(BoundingBox.infinite())
        ) { entity -> entity.isAlive && !entity.isSpectator }

        val newTarget = playerTargets.random()
    }

    fun onCollision() {

    }
}