package one.theaq.skull.data.fabric

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.core.HolderLookup
import one.theaq.skull.Main
import one.theaq.skull.config.Common
import java.util.concurrent.CompletableFuture

class LanguageProvider(dataOutput: FabricPackOutput, registryLookup: CompletableFuture<HolderLookup.Provider>): FabricLanguageProvider(dataOutput, registryLookup) {
    override fun generateTranslations(
        registryLookup: HolderLookup.Provider,
        translationBuilder: TranslationBuilder
    ) {
        ConfigApi.buildTranslations(Common::class, Main.location("common"), "en_us", true, translationBuilder::add)
    }
}