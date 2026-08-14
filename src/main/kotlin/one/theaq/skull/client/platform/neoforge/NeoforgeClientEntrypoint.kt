//? if neoforge {
package one.theaq.skull.client.platform.neoforge

import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.common.Mod
import one.theaq.skull.Main
import one.theaq.skull.client.MainClient

@Mod(Main.MOD_ID, dist = [Dist.CLIENT])
class NeoforgeClientEntrypoint {
    init {
        MainClient.initialize()
    }
}
//?}