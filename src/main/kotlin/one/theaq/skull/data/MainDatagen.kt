package one.theaq.skull.data

//? if fabric {
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator

@Entrypoint("fabric-datagen")
class MainDatagen : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {

    }
}
//?} else {

/*import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent
import one.theaq.skull.Template

@EventBusSubscriber(modid = Template.MOD_ID)
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