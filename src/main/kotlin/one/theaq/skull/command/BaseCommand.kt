package one.theaq.skull.command

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack

abstract class BaseCommand {
    open fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        TODO("override register() in $this")
    }

    open fun getPermissionLevel(): Int {
        return 4
    }

    open fun getName() {
        TODO("override getName() in $this")
    }
}