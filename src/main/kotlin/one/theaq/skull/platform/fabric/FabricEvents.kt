//? if fabric {
package one.theaq.skull.platform.fabric

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import one.theaq.skull.command.CommandRegistry
import one.theaq.skull.logic.SkullManager

class FabricEvents {
    init {
        CommandRegistrationCallback.EVENT.register(CommandRegistry::registerCommands)
        ServerTickEvents.START_SERVER_TICK.register { SkullManager.INSTANCE.tickSkulls(it) }
        ServerPlayerEvents.JOIN.register { SkullManager.INSTANCE.onPlayerJoin(it) }
        ServerLifecycleEvents.SERVER_STARTED.register { SkullManager.INSTANCE.clear() }
    }
}
//?}