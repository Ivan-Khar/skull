package one.theaq.skull.logic

import eu.pb4.polymer.virtualentity.api.ElementHolder
import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment
import eu.pb4.polymer.virtualentity.api.attachment.ManualAttachment
import eu.pb4.polymer.virtualentity.api.elements.BlockDisplayElement
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySelector
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.Vec3
import java.util.Optional
import java.util.UUID
import kotlin.jvm.optionals.getOrElse

class Skull(val level: ServerLevel) {

    val server: MinecraftServer = level.server

    var pos: Vec3 = Vec3.ZERO
    var oldPos: Vec3 = Vec3.ZERO

    var targetUUID: Optional<UUID> = Optional.empty()
    var recentlyKilled: MutableMap<UUID, Int> = mutableMapOf()

    val displayElement: BlockDisplayElement = BlockDisplayElement(Blocks.SKELETON_SKULL.defaultBlockState())
    val elementHolder: ElementHolder = ElementHolder()
    val holderAttachment: HolderAttachment = ManualAttachment(elementHolder, level, this::pos)

    init {
        elementHolder.addElement(displayElement)
    }

    fun setPos(newPos: Vec3) {
        pos = newPos
    }

    fun tick() {
        recentlyKilled.values.removeAll { tick -> server.tickCount - tick > 200 }

        val targetPlayer = getTarget()
        render()
        checkCollisions()
        move(targetPlayer)
    }

    fun move(target: Optional<Entity>) {
        oldPos = pos

    }

    fun checkCollisions() {
        val collidingEntities = level.allEntities.filter { it.position().distanceTo(this.pos) < 1 }
        collidingEntities.forEach { onCollision(it) }
    }

    fun render() {
        val nearbyPlayers = level.players().filter { it.position().distanceTo(this.pos) < 128 }
        nearbyPlayers.forEach { holderAttachment.startWatching(it) }
        holderAttachment.tick()
    }

    fun getTarget(): Optional<Entity> {
        val uuid = targetUUID.getOrElse {
            val newTarget = getNewTarget()
            if (newTarget.isEmpty) return Optional.empty()

            getNewTarget().get()
        }

        return Optional.ofNullable(level.getEntity(uuid))
    }

    fun getNewTarget(): Optional<UUID> {
        val playerTargets = level.getPlayers(EntitySelector.NO_SPECTATORS)
        if (playerTargets.isEmpty()) return Optional.empty()

        if (targetUUID.isPresent && playerTargets.size > 1) playerTargets.removeAll { it.uuid == targetUUID }

        val newTarget: Player = playerTargets.random()
        return Optional.of(newTarget.uuid)
    }

    fun onCollision(collider: Entity) {
        if (collider.uuid != targetUUID) return

        collider.kill(level)
    }
}