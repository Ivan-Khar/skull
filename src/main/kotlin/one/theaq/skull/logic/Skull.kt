package one.theaq.skull.logic

import eu.pb4.polymer.virtualentity.api.ElementHolder
import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment
import eu.pb4.polymer.virtualentity.api.attachment.ManualAttachment
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySelector
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.Vec3
import one.theaq.skull.Main
import one.theaq.skull.util.SkullMath
import org.joml.Quaternionf
import org.joml.Vector3f
import java.util.Optional
import java.util.UUID
import kotlin.math.atan2
import kotlin.math.sqrt

class Skull(
    val manager: SkullManager,
    val level: ServerLevel,
    var pos: Vec3,
    var targetOptional: Optional<Player> = Optional.empty()
) {
    val server: MinecraftServer = level.server
    var uuid: UUID = UUID.randomUUID()

    var oldPos: Vec3 = pos
    var recentlyKilled: MutableMap<UUID, Int> = mutableMapOf()
    var lastTargetUpdate: Int = 0

    val displayElement: ItemDisplayElement = ItemDisplayElement(Blocks.SKELETON_SKULL.asItem()) // TODO: <- Config option for item type
    val elementHolder: ElementHolder = ElementHolder()
    val holderAttachment: HolderAttachment = ManualAttachment(elementHolder, level, this::pos)

    init {
        displayElement.interpolationDuration = 2
        displayElement.teleportDuration = 5
        displayElement.setDisplaySize(0.5f, 0.5f)
        displayElement.scale = Vector3f(1.0f, 1.0f, 1.0f)
        elementHolder.addElement(displayElement)
    }

    fun tick() {
        recentlyKilled.values.removeAll { tick -> server.tickCount - tick > 200 }

        checkTarget()
        render()
        checkCollisions()
        move()
    }

    fun move() {
        oldPos = pos
        if (targetOptional.isEmpty) return
        val target = targetOptional.get()
        val targetPos = target.eyePosition

        val targetDelta = targetPos.subtract(pos)
        val targetVector = targetPos.subtract(pos).normalize()
        val targetDistance = targetDelta.length()
        val baseSpeed = 0.05 // TODO: <- Config option
        val speed = when {
            targetDistance < 16.0 -> baseSpeed
            targetDistance in 16.0..64.0 -> SkullMath.map(targetDistance, 16.0, 64.0, baseSpeed, baseSpeed * 10)
            targetDistance in 64.0..1024.0 -> SkullMath.map(targetDistance, 64.0, 1024.0, baseSpeed * 10, baseSpeed * 200)
            targetDistance > 1024.0 -> baseSpeed * 500
            else -> baseSpeed
        }

        this.pos = pos.add(targetVector.scale(speed))
    }

    fun checkCollisions() {
        val collidingEntities = level.allEntities.filter { it.eyePosition.distanceTo(this.oldPos) < 0.5 }
        collidingEntities.forEach { onCollision(it) }
    }

    fun render() {
        val holderWatching = holderAttachment.holder().watchingPlayers
        val nearbyPlayers = level.players().filter { it.eyePosition.distanceTo(this.pos) < 128 }
        holderWatching.filter { it.player !in nearbyPlayers }.forEach { holderAttachment.stopWatching(it) } // TODO: Prob breaks with immersive portals
        nearbyPlayers.forEach { holderAttachment.startWatching(it) }

        holderAttachment.tick()

        // position
        val posTranslation = oldPos.subtract(pos).add(0.0, 0.25, 0.0)
        displayElement.translation = posTranslation.toVector3f()

        // rotation
        if (targetOptional.isEmpty) return
        val target = targetOptional.get()

        val targetDelta = pos.subtract(target.eyePosition)
        val pitch = (atan2(sqrt(targetDelta.z * targetDelta.z + targetDelta.x * targetDelta.x), targetDelta.y) - Math.PI/2).toFloat()
        val yaw = (atan2(targetDelta.z, targetDelta.x) - Math.PI/2).toFloat()
        val quaternionPitch = Quaternionf().fromAxisAngleRad(1.0f, 0.0f, 0.0f, pitch)
        val quaternionYaw = Quaternionf().fromAxisAngleRad(0.0f, -1.0f, 0.0f, yaw)
        displayElement.leftRotation = quaternionYaw.mul(quaternionPitch)

        displayElement.startInterpolationIfDirty()
    }

    fun checkTarget() {
        val updateTimeout = 20 // should be 1 second
        if (server.tickCount - lastTargetUpdate < updateTimeout) return

        if (targetOptional.isEmpty) {
            getNewTarget(SwitchTargetReason.EMPTY_TARGET)
            return
        }

        when {
            !server.playerList.players.contains(targetOptional.get())   -> getNewTarget(SwitchTargetReason.LEFT_SERVER)
            !level.getPlayers { true }.contains(targetOptional.get())   -> getNewTarget(SwitchTargetReason.DIFFERENT_DIMENSION)
            !targetOptional.get().isAlive                               -> getNewTarget(SwitchTargetReason.DIED)
            targetOptional.get().isSpectator                            -> getNewTarget(SwitchTargetReason.SPECTATOR)
        }
    }

    fun getNewTarget(reason: SwitchTargetReason) {
        Main.LOGGER.info("getting new target with $reason reason")
        lastTargetUpdate = server.tickCount
        
        val playerTargets = level.getPlayers(EntitySelector.NO_SPECTATORS)
        playerTargets.removeAll { it.uuid in recentlyKilled.keys || !it.isAlive }
        if (playerTargets.isEmpty()) { targetOptional = Optional.empty(); return }

        val newTarget: Player = playerTargets.random()
        targetOptional = Optional.of(newTarget)
    }

    fun onCollision(collider: Entity) {
        if (targetOptional.isEmpty || collider.uuid != targetOptional.get().uuid) return
        val target = targetOptional.get()

        if (!target.isAlive) return
        manager.removeSkull(this) // TODO: <- Config option
        recentlyKilled += Pair(targetOptional.get().uuid, server.tickCount)
        collider.kill(level)
        targetOptional = Optional.empty()
    }

    /**
     *  Use [removeSkull(skull: Skull)][SkullManager.removeSkull] instead
     */
    fun destroy() {
        holderAttachment.destroy()
    }

    enum class SwitchTargetReason {
        EMPTY_TARGET,
        LEFT_SERVER,
        DIED,
        DIFFERENT_DIMENSION,
        SPECTATOR,
        COMMAND_SWITCHED
    }
}