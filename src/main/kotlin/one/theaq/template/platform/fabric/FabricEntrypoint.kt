//? if fabric {
package one.theaq.template.platform.fabric

import net.fabricmc.api.ModInitializer
import one.theaq.template.Template

object FabricEntrypoint : ModInitializer {
    override fun onInitialize() {
        Template.initialize()
    }
}
//?}