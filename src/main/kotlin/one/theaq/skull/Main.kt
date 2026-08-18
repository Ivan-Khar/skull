package one.theaq.skull

//~identifier
import net.minecraft.resources.Identifier
import one.theaq.skull.command.CommandRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Main {
    const val MOD_ID: String = "skull"
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

    fun initialize() {
        CommandRegistry

        LOGGER.info("$MOD_ID initialized")
    }

    fun location(path: String): Identifier {
        return Identifier.fromNamespaceAndPath(MOD_ID, path)
    }

    fun commonLocation(path: String): Identifier {
        return Identifier.fromNamespaceAndPath("c", path)
    }
}