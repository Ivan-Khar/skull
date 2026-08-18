//? if fabric {
package one.theaq.skull.platform.fabric

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import one.theaq.skull.command.CommandRegistry

class FabricEvents {
    init {
        CommandRegistrationCallback.EVENT.register(CommandRegistry::registerCommands)
    }
}
//?}