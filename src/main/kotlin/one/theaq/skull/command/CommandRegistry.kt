package one.theaq.skull.command

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object CommandRegistry {
    private val COMMANDS = mutableListOf<BaseCommand>()

    val SKULL_COMMAND = addCommand(SkullCommand())

    private fun addCommand(command: BaseCommand): BaseCommand {
        COMMANDS += command
        return command
    }

    fun registerCommands(dispatcher: CommandDispatcher<CommandSourceStack>, context: CommandBuildContext, selection: Commands.CommandSelection) {
        COMMANDS.forEach { it.register(dispatcher) }
    }
}