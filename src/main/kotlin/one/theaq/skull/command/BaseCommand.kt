package one.theaq.skull.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.ArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.server.permissions.Permission
import net.minecraft.server.permissions.Permissions
import one.theaq.skull.Main

abstract class BaseCommand {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val mainBuilder = commandBuilder(
            Commands.literal("${Main.MOD_ID}:${getName()}")
                .requires(::permissionCheck)
        )

        val main = dispatcher.register(mainBuilder)
        val alias = dispatcher.register(
            Commands.literal(getName())
            .redirect(main)
        )
    }

    open fun <T: ArgumentBuilder<CommandSourceStack, T>> commandBuilder(command: T): T {
        command.executes {
            it.source.sendSystemMessage(Component.literal("a"))
            return@executes 0
        }

        return command
    }

    open fun getPermissionLevel(): Permission {
        return Permissions.COMMANDS_OWNER
    }

    open fun getName(): String {
        TODO("override getName() in $this")
    }

    private fun permissionCheck(commandSourceStack: CommandSourceStack): Boolean {
        return commandSourceStack.permissions().hasPermission(getPermissionLevel())
    }
}