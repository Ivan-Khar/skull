package one.theaq.skull.command

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.server.permissions.Permission
import net.minecraft.server.permissions.Permissions
import net.minecraft.world.level.storage.ServerLevelData
import one.theaq.skull.logic.SkullManager

class SkullCommand: BaseCommand() {
    val skullManager = SkullManager.INSTANCE;

    override fun getName(): String {
        return "skull"
    }

    override fun getPermissionLevel(): Permission {
        return Permissions.COMMANDS_GAMEMASTER
    }

    override fun <T : ArgumentBuilder<CommandSourceStack, T>> commandBuilder(command: T): T {
        command
            .then(Commands.literal("spawn").executes(::spawnSkull))
            .then(Commands.literal("list").then(Commands.argument("expand", BoolArgumentType.bool()).executes(::listSkulls)))
            .then(Commands.literal("delete").then(Commands.argument("radius", DoubleArgumentType.doubleArg(-1.0, Double.MAX_VALUE)).executes(::deleteSkull)))
            .then(Commands.literal("deleteAll").executes(::deleteAllSkulls))

        return command
    }

    fun spawnSkull(context: CommandContext<CommandSourceStack>): Int {
        skullManager.createSkull(context.source.level)

        context.source.sendSystemMessage(Component.literal("Spawned skull at 0 0"))
        return 0
    }

    fun listSkulls(context: CommandContext<CommandSourceStack>): Int {
        val skulls = skullManager.getAllSkulls()
        val textResponse = Component.literal("Skull list: \n")
        val expanded = BoolArgumentType.getBool(context, "expand")
        skulls.forEach {
            textResponse.append("${it.uuid.toString().substring(0.. if (expanded) 35 else 7)} at ${String.format("%.2f %.2f %.2f", it.pos.x, it.pos.y, it.pos.z)} ${ if (it.targetOptional.isPresent) "targeting ${it.targetOptional.get().displayName.string}" else "searching for target" }\n")
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