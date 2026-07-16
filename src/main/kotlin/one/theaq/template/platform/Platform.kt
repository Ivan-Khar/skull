package one.theaq.template.platform

import java.nio.file.Path

//? if fabric {
import net.fabricmc.loader.api.FabricLoader

object Platform {
    fun isModLoaded(modId: String): Boolean {
        return FabricLoader.getInstance().isModLoaded(modId)
    }

    val isDev: Boolean
        get() = FabricLoader.getInstance().isDevelopmentEnvironment()

    val debugDirectory: Path
        get() = FabricLoader.getInstance().getConfigDir().getParent().resolve(".template_debug")
}
//?} else if neoforge {

/*import net.neoforged.fml.ModList
import net.neoforged.fml.loading.FMLLoader
import net.neoforged.fml.loading.LoadingModList

object Platform {
    fun isModLoaded(modId: String): Boolean {
        val modList = ModList.get()
        if (modList != null) {
            return modList.isLoaded(modId)
        }
        //? if >=1.21.10 {
        val loadingModList = FMLLoader.getCurrent().getLoadingModList()
        //?} else {
        /*val loadingModList = LoadingModList.get()
        *///?}
        if (loadingModList != null) {
            return loadingModList.getModFileById(modId) != null
        }
        return false
    }

    //? if >=1.21.10 {
    fun isDev(): Boolean {
        return !FMLLoader.getCurrent().isProduction
    }

    fun getDebugDirectory(): Path {
        return FMLLoader.getCurrent().gameDir.resolve(".template_debug")
    }
    //?} else {
    /*fun isDev(): Boolean {
        return !FMLLoader.isProduction()
    }

    fun getDebugDirectory(): Path {
        return FMLLoader.getGamePath().resolve(".template_debug")
    }
    *///?}
}

*///?}