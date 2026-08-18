package one.theaq.skull.command

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object CommandRegistry {
    val COMMANDS = emptyList<BaseCommand>()

    val SKULL_COMMAND = addCommand()

    fun addCommand() {

    }

    fun registerCommands(dispatcher: CommandDispatcher<CommandSourceStack>, context: CommandBuildContext, selection: Commands.CommandSelection) {
        COMMANDS.forEach { it.register(dispatcher) }
    }
}