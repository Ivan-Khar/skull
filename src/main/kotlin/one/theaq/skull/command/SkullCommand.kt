package one.theaq.skull.command

import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.server.permissions.Permission
import net.minecraft.server.permissions.Permissions
import one.theaq.skull.logic.SkullManager

class SkullCommand: BaseCommand() {

    override fun getName(): String {
        return "skull"
    }

    override fun getPermissionLevel(): Permission {
        return Permissions.COMMANDS_GAMEMASTER
    }

    override fun <T : ArgumentBuilder<CommandSourceStack, T>> commandBuilder(command: T): T {
        command
            .then(Commands.literal("spawn").executes(::spawnSkull))
            .then(Commands.literal("delete").then(Commands.argument("radius", DoubleArgumentType.doubleArg(-1.0, Double.MAX_VALUE)).executes(::deleteSkull)))
            .then(Commands.literal("deleteAll").executes(::deleteAllSkulls))

        return command
    }

    fun spawnSkull(context: CommandContext<CommandSourceStack>): Int {
        context.source.sendSystemMessage(Component.literal("spawned skull at 0 0"))
        SkullManager.INSTANCE.createSkull(context.source.level)
        return 0
    }

    fun deleteSkull(context: CommandContext<CommandSourceStack>): Int {
        context.source.sendSystemMessage(Component.literal("spawned skull at 0 0"))
        SkullManager.INSTANCE.getSkulls(context.source.level, context.source.position, DoubleArgumentType.getDouble(context, "radius")).forEach {
            SkullManager.INSTANCE.removeSkull(it)
        }
        return 0
    }

    fun deleteAllSkulls(context: CommandContext<CommandSourceStack>): Int {
        context.source.sendSystemMessage(Component.literal("deleted all skulls"))
        SkullManager.INSTANCE.removeAllSkulls()
        return 0
    }
}