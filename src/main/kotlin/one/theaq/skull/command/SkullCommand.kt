package one.theaq.skull.command

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.arguments.coordinates.Vec3Argument
import net.minecraft.commands.arguments.coordinates.WorldCoordinates
import net.minecraft.commands.arguments.selector.EntitySelector
import net.minecraft.network.chat.Component
import net.minecraft.server.permissions.Permission
import net.minecraft.server.permissions.Permissions
import net.minecraft.world.entity.player.Player
import one.theaq.skull.logic.SkullManager
import java.util.Optional

class SkullCommand: BaseCommand() {
    val skullManager = SkullManager.INSTANCE;

    override fun getName(): String {
        return "skull"
    }

    override fun getPermissionLevel(): Permission {
        return Permissions.COMMANDS_GAMEMASTER
    }

    override fun <T : ArgumentBuilder<CommandSourceStack, T>> commandBuilder(command: T): T {
        val spawn = Commands.literal("spawn").executes(::spawnSkull)
            .then(Commands.argument("position", Vec3Argument.vec3()).executes(::spawnSkull)
            .then(Commands.argument("target", EntityArgument.player()).executes(::spawnSkull)))
        val list = Commands.literal("list").executes(::listSkulls)
            .then(Commands.argument("expand", BoolArgumentType.bool()).executes(::listSkulls))
        val delete = Commands.literal("delete").then(Commands.argument("radius", DoubleArgumentType.doubleArg(-1.0, Double.MAX_VALUE)).executes(::deleteSkull))
        val deleteAll = Commands.literal("deleteAll").executes(::deleteAllSkulls)

        command.then(spawn)
            .then(list)
            .then(delete)
            .then(deleteAll)

        return command
    }

    fun spawnSkull(context: CommandContext<CommandSourceStack>): Int {
        val position = getOrDefault(context, "position", WorldCoordinates.absolute(0.0, 0.0, 0.0)).getPosition(context.source)
        val targetSelector = getOptionalOfType(context, "target", EntitySelector::class.java)
        val target =
            if (targetSelector.isEmpty) Optional.empty()
            else Optional.of(targetSelector.get().findSinglePlayer(context.source) as Player)
        
        val skull = skullManager.createSkull(context.source.level, position, target)

        context.source.sendSystemMessage(Component.literal("Spawned skull ${skull.uuid} at ${skull.pos} targeting $target"))
        return 0
    }

    fun listSkulls(context: CommandContext<CommandSourceStack>): Int {
        val skulls = skullManager.getAllSkulls()
        val textResponse = Component.literal("Skull list:")
        val expanded = getOrDefault(context, "expand", false)

        skulls.forEach {
            textResponse.append("\n${it.uuid.toString().substring(0.. if (expanded) 35 else 7)} at ${String.format("%.2f %.2f %.2f", it.pos.x, it.pos.y, it.pos.z)} ${ if (it.targetOptional.isPresent) "targeting ${it.targetOptional.get().displayName.string}" else "searching for target" }")
        }

        context.source.sendSystemMessage(textResponse)
        return skulls.size
    }


    fun deleteSkull(context: CommandContext<CommandSourceStack>): Int {
        val removedSkulls = skullManager.getSkulls(context.source.level, context.source.position, DoubleArgumentType.getDouble(context, "radius")).count {
            skullManager.removeSkull(it) >= 0
        }

        context.source.sendSystemMessage(Component.literal("Destroyed $removedSkulls skulls"))
        return 0
    }

    fun deleteAllSkulls(context: CommandContext<CommandSourceStack>): Int {
        skullManager.removeAllSkulls()

        context.source.sendSystemMessage(Component.literal("Destroyed all skulls"))
        return 0
    }
}