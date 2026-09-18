package one.theaq.skull.logic

import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.phys.Vec3
import one.theaq.skull.config.Configs
import java.util.*

class SkullManager {
    private val skulls: MutableList<Skull> = mutableListOf()
    private val markedForRemoval: MutableList<Skull> = mutableListOf()
    private val killedBySkull: MutableList<LivingEntity> = mutableListOf()
    private val entityTargeted: MutableList<LivingEntity> = mutableListOf()
    private var spawnedSkulls: Boolean = false

    private val config = Configs.COMMON

    fun createSkull(level: ServerLevel, pos: BlockPos, target: Optional<LivingEntity> = Optional.empty()): Skull  {
        return createSkull(level, Vec3(pos), target)
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

    fun onPlayerJoin(player: ServerPlayer) {
        if (spawnedSkulls) return
        if (!config.spawnSection.spawnWhenEnoughPlayers.get()) return

        val server = player.level().server
        if (server.playerCount < config.spawnSection.playerCountRequirement.get()) return

        val configDimension = Identifier.parse(config.spawnSection.spawnDimension)

        val skullsInDimension = skulls.filter { skull -> skull.level.dimension().identifier() == configDimension }
        if (skullsInDimension.count() >= config.spawnSection.skullCount.get()) return

        val spawnDimension = server.allLevels.find { level -> level.dimension().identifier() == configDimension }
        if (spawnDimension == null) return

        val skullsToSpawn = config.spawnSection.skullCount.get() - skulls.count()
        for (skull in 1..skullsToSpawn) createSkull(spawnDimension, spawnDimension.respawnData.pos().above(10))
        spawnedSkulls = true
    }

    fun clear() {
        skulls.clear()
        markedForRemoval.clear()
        killedBySkull.clear()
        entityTargeted.clear()

        spawnedSkulls = false
    }

    fun addToKilledBySkull(entity: LivingEntity) {
        killedBySkull.add(entity)
    }

    fun removeFromKilledBySkull(entity: LivingEntity): Boolean {
        return killedBySkull.remove(entity)
    }

    fun addToTargeted(entity: LivingEntity) {
        entityTargeted.add(entity)
    }

    fun isTargetedBySkull(entity: LivingEntity): Boolean {
        return entityTargeted.contains(entity)
    }

    fun removeFromTargeted(entity: LivingEntity): Boolean {
        return entityTargeted.remove(entity)
    }

    companion object {
        val INSTANCE: SkullManager = SkullManager()
    }
}