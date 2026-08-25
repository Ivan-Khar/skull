package one.theaq.skull.command

import com.mojang.brigadier.builder.ArgumentBuilder
import net.minecraft.commands.CommandSourceStack
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
        command.executes {
            it.source.sendSystemMessage(Component.literal("spawned skull at 0 0"))
            SkullManager.INSTANCE.createSkull(it.source.level)
            return@executes 0
        }

        return command
    }
}