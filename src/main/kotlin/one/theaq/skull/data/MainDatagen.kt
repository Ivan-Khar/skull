package one.theaq.skull.data

//? if fabric {
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import one.theaq.skull.data.fabric.LanguageProvider

class MainDatagen : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val dataPack = fabricDataGenerator.createPack()
        dataPack.addProvider(::LanguageProvider)
    }
}
//?} else {

/*import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent
import one.theaq.skull.Main

@EventBusSubscriber(modid = Main.MOD_ID)
object TemplateDataGen {
    //? if >= 1.21.4 {
    @SubscribeEvent
    fun gatherServerData(event: GatherDataEvent.Server) {

    }

    @SubscribeEvent
    fun gatherClientData(event: GatherDataEvent.Client) {

    }
    //?} else {
    /*@SubscribeEvent
    fun gatherData(event: GatherDataEvent) {

    }
    *///?}
}

*///?}