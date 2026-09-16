package one.theaq.skull.logic

import net.minecraft.core.BlockPos
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.dimension.BuiltinDimensionTypes
import net.minecraft.world.phys.Vec3
import one.theaq.skull.config.Configs
import java.util.*

class SkullManager {
    private val skulls: MutableList<Skull> = mutableListOf()
    private val markedForRemoval: MutableList<Skull> = mutableListOf()
    private val killedBySkull: MutableList<LivingEntity> = mutableListOf()
    private val entityTargeted: MutableList<LivingEntity> = mutableListOf()
    private val config = Configs.COMMON

    fun createSkull(level: ServerLevel, pos: BlockPos, target: Optional<LivingEntity> = Optional.empty()): Skull  {
        return createSkull(level, Vec3(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble()), target)
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
        if (!config.spawn.spawnWhenEnoughPlayers) return

        val server = player.level().server
        if (server.playerCount < config.spawn.playerCountRequirement) return

        val spawnDimensionRegistry = server.registryAccess().get(BuiltinDimensionTypes.NETHER) //TODO: fix config
        if (spawnDimensionRegistry.isEmpty) return

        val spawnDimension = server.allLevels.find { level -> level.dimensionType() == spawnDimensionRegistry.get().value() }
        if (spawnDimension == null) return

        val skullsInDimension = skulls.filter { skull -> skull.level.dimension() == spawnDimension }
        if (skullsInDimension.count() >= config.spawn.skullCount) return

        val skullsToSpawn = config.spawn.skullCount - skulls.count()
        for (skull in 1..skullsToSpawn) createSkull(spawnDimension, spawnDimension.respawnData.pos().above(10))
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