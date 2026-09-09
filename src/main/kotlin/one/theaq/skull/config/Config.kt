package one.theaq.skull.config

import java.nio.file.Paths
import kotlin.io.path.notExists

class Config {

    private val configPath = Paths.get("./config/theaq/")
    private val configFile = configPath.resolve("skull.properties")

    fun load() {
        if (configFile.notExists()) createFile()

    }

    fun createFile() {

    }

    companion object {
        val INSTANCE = Config()
    }
}