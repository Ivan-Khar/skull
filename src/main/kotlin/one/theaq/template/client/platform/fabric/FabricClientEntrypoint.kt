//? if fabric {
package one.theaq.template.client.platform.fabric

import net.fabricmc.api.ClientModInitializer
import one.theaq.template.client.TemplateClient

class FabricClientEntrypoint : ClientModInitializer {
    override fun onInitializeClient() {
        TemplateClient.initialize()
    }
}
//?}