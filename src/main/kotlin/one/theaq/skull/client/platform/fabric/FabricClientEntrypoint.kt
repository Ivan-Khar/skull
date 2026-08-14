//? if fabric {
package one.theaq.skull.client.platform.fabric

import net.fabricmc.api.ClientModInitializer
import one.theaq.skull.client.MainClient

class FabricClientEntrypoint : ClientModInitializer {
    override fun onInitializeClient() {
        MainClient.initialize()
    }
}
//?}