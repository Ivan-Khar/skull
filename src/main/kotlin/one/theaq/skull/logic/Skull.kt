package one.theaq.skull.logic

import eu.pb4.polymer.virtualentity.api.ElementHolder
import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment
import eu.pb4.polymer.virtualentity.api.attachment.ManualAttachment
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.EntitySelector
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.phys.Vec3
import one.theaq.skull.config.Configs
import org.joml.Quaternionf
import java.util.Optional
import java.util.UUID
import kotlin.math.atan2
import kotlin.math.sqrt

class Skull(
    val manager: SkullManager,
    val level: ServerLevel,
    var pos: Vec3,
    var targetOptional: Optional<LivingEntity> = Optional.empty()
) {
    val server: MinecraftServer = level.server
    val config = Configs.COMMON
    var uuid: UUID = UUID.randomUUID()

    var oldPos: Vec3 = pos
    var recentlyKilled: MutableMap<UUID, Int> = mutableMapOf()
    var lastTargetUpdate: Int = 0

    val displayElement: ItemDisplayElement = ItemDisplayElement(config.skull.block.asItem())
    val elementHolder: ElementHolder = ElementHolder()
    val holderAttachment: HolderAttachment = ManualAttachment(elementHolder, level, this::pos)

    init {
        if (targetOptional.isPresent) manager.addToTargeted(targetOptional.get())

        displayElement.interpolationDuration = 2
        displayElement.teleportDuration = 5
        displayElement.setDisplaySize(0.5f, 0.5f)
        displayElement.itemDisplayContext = ItemDisplayContext.HEAD
        elementHolder.addElement(displayElement)
    }

    fun tick() {
        recentlyKilled.values.removeAll { tick -> server.tickCount - tick > config.skull.playerGracePeriod }

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

        val speed = when {
            targetDistance in 16.0..64.0 -> config.skull.fastSpeed
            targetDistance in 64.0..512.0 -> config.skull.fasterSpeed
            targetDistance > 512.0 -> config.skull.fastestSpeed
            else -> config.skull.baseSpeed
        }

        this.pos = pos.add(targetVector.scale(speed))
    }

    fun checkCollisions() {
        val collidingEntities = level.allEntities.filter { it.eyePosition.distanceTo(this.oldPos) < 0.5 && it is LivingEntity }
        collidingEntities.forEach { onCollision(it as LivingEntity) }
    }

    fun render() {
        // might be too expensive tbh but should be fine
        val holderWatching = holderAttachment.holder().watchingPlayers
        val nearbyPlayers = level.players().filter { it.eyePosition.distanceTo(this.pos) < 128 }
        holderWatching.filter { it.player !in nearbyPlayers }.forEach { holderAttachment.stopWatching(it) }
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
        val updateTimeout = config.skull.timeoutOnNoTargets
        if (server.tickCount - lastTargetUpdate < updateTimeout) return

        if (targetOptional.isEmpty) {
            getNewTarget(SwitchTargetReason.EMPTY_TARGET)
            return
        }

        when {
            !server.playerList.players.contains(targetOptional.get())       -> getNewTarget(SwitchTargetReason.LEFT_SERVER)
            level.dimension() != targetOptional.get().level().dimension()   -> getNewTarget(SwitchTargetReason.DIFFERENT_DIMENSION)
            targetOptional.get().isDeadOrDying                              -> getNewTarget(SwitchTargetReason.DIED)
            targetOptional.get().isSpectator                                -> getNewTarget(SwitchTargetReason.SPECTATOR)
        }
    }

    fun getNewTarget(reason: SwitchTargetReason) {
        lastTargetUpdate = server.tickCount

        val playerTargets = level.getPlayers(EntitySelector.NO_SPECTATORS)
        playerTargets.removeAll { it.uuid in recentlyKilled.keys || !it.isAlive }
        if (playerTargets.isEmpty()) { clearTarget(); return }

        val newTarget: ServerPlayer = playerTargets.random()
        if (config.messages.notifyOnTarget) sendSubTitle(newTarget, "skull.targeting.notification")
        setTarget(newTarget)
    }

    fun onCollision(collider: LivingEntity) {
        if (targetOptional.isEmpty || collider.uuid != targetOptional.get().uuid) return
        val target = targetOptional.get()

        if (!target.isAlive) return

        killEntity(collider)
    }

    fun killEntity(target: LivingEntity) {
        if (config.skull.disappearOnKill) manager.removeSkull(this)

        recentlyKilled += Pair(targetOptional.get().uuid, server.tickCount)
        manager.addToKilledBySkull(target)
        target.kill(level)

        clearTarget()
    }

    fun setTarget(entity: LivingEntity) {
        manager.addToTargeted(entity)
        targetOptional = Optional.of(entity)
    }

    fun clearTarget() {
        if (targetOptional.isPresent) {
            sendSubTitle(targetOptional.get(), "skull.targeting.notification")
            manager.removeFromTargeted(targetOptional.get())
        }
        targetOptional = Optional.empty()
    }

    /**
     *  Use [removeSkull(skull: Skull)][SkullManager.removeSkull] to remove skull
     *  removes skulls from holderAttachments
     */
    fun destroy() {
        clearTarget()
        level.players().filter { it.eyePosition.distanceTo(pos) < 128 }.forEach { player ->
            level.sendParticles(player, ParticleTypes.ASH, false, false, this.pos.x, this.pos.y, this.pos.z, 50, 0.125, 0.125, 0.125, 0.025)
        }
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

    companion object {
        fun sendSubTitle(entity: LivingEntity, translation: String) {
            if (entity !is ServerPlayer) return

            val subtitlePacket = ClientboundSetActionBarTextPacket(Component.translatable(translation).withColor(TextColor.GRAY))
            entity.connection.send(subtitlePacket)
        }
    }
}