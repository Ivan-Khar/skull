//? if fabric {
package one.theaq.skull.platform.fabric

import net.fabricmc.api.ModInitializer
import one.theaq.skull.Main

object FabricEntrypoint : ModInitializer {
    override fun onInitialize() {
        Main.initialize()
    }
}
//?}