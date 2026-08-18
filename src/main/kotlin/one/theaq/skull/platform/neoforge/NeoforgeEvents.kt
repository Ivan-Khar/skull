//? if neoforge {
/*package one.theaq.skull.platform.neoforge

import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.RegisterCommandsEvent
import one.theaq.skull.command.CommandRegistry

class NeoforgeEvents {

    init {
        val EVENT_BUS = NeoForge.EVENT_BUS.addListener(::commandRegistrationEvent)
    }

    fun commandRegistrationEvent(event: RegisterCommandsEvent) {
        CommandRegistry.registerCommands(event.dispatcher, event.buildContext, event.commandSelection)
    }
}
*///?}