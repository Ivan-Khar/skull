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
import org.joml.Quaternionf
import org.joml.Vector3f
import java.util.Optional
import java.util.UUID
import kotlin.math.atan2
import kotlin.math.sqrt

class Skull(val level: ServerLevel) {

    val server: MinecraftServer = level.server

    var pos: Vec3 = Vec3.ZERO
    var oldPos: Vec3 = Vec3.ZERO

    var targetOptional: Optional<Entity> = Optional.empty()
    var recentlyKilled: MutableMap<UUID, Int> = mutableMapOf()

    val displayElement: ItemDisplayElement = ItemDisplayElement(Blocks.SKELETON_SKULL.asItem())
    val elementHolder: ElementHolder = ElementHolder()
    val holderAttachment: HolderAttachment = ManualAttachment(elementHolder, level, this::pos)

    init {
        displayElement.interpolationDuration = 1
        displayElement.setDisplaySize(0.5f, 0.5f)
        displayElement.scale = Vector3f(1.0f, 1.0f, 1.0f)
        elementHolder.addElement(displayElement)
        displayElement.startInterpolation()
        displayElement.teleportDuration = 2
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

        val deltaPos = targetPos.subtract(pos).scale(0.025)

        this.pos = pos.add(deltaPos)
    }

    fun checkCollisions() {
        val collidingEntities = level.allEntities.filter { it.eyePosition.distanceTo(this.oldPos) < 0.5 }
        collidingEntities.forEach { onCollision(it) }
    }

    fun render() {
        val holderWatching = holderAttachment.holder().watchingPlayers
        val nearbyPlayers = level.players().filter { it.eyePosition.distanceTo(this.pos) < 64 }
        holderWatching.filter { it.player !in nearbyPlayers }.forEach { holderAttachment.stopWatching(it) } // Prob breaks with immersive portals
        nearbyPlayers.forEach { holderAttachment.startWatching(it) }

        holderAttachment.tick()
        if (targetOptional.isEmpty) return
        val target = targetOptional.get()

        // rotation
        val targetDelta = pos.subtract(target.eyePosition)
        val pitch = (atan2(sqrt(targetDelta.z * targetDelta.z + targetDelta.x * targetDelta.x), targetDelta.y) - Math.PI/2).toFloat()
        val yaw = (atan2(targetDelta.z, targetDelta.x) - Math.PI/2).toFloat()
        val quaternionPitch = Quaternionf().fromAxisAngleRad(1.0f, 0.0f, 0.0f, pitch)
        val quaternionYaw = Quaternionf().fromAxisAngleRad(0.0f, -1.0f, 0.0f, yaw)
        displayElement.leftRotation = quaternionYaw.mul(quaternionPitch)

        // position
        val posTranslation = oldPos.subtract(pos).add(0.0, 0.25, 0.0)
        displayElement.translation = posTranslation.toVector3f()

        displayElement.startInterpolation()
    }

    fun checkTarget() {
        if (targetOptional.isPresent && targetOptional.get() in level.players()) return

        targetOptional = getNewTarget()
    }

    fun getNewTarget(): Optional<Entity> {
        val playerTargets = level.getPlayers(EntitySelector.NO_SPECTATORS)
        playerTargets.removeAll { it.uuid in recentlyKilled.keys }
        if (playerTargets.isEmpty()) return Optional.empty()

        val newTarget: Player = playerTargets.random()
        return Optional.of(newTarget)
    }

    fun onCollision(collider: Entity) {
        if (targetOptional.isEmpty || collider.uuid != targetOptional.get().uuid) return

        recentlyKilled += Pair(targetOptional.get().uuid, server.tickCount)
        collider.kill(level)
        targetOptional = Optional.empty()
    }
}