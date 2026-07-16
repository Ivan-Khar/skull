package one.theaq.template

//~identifier
import net.minecraft.resources.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Template {
    const val MOD_ID: String = "template"
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

    fun initialize() {
        LOGGER.info("$MOD_ID initialized")
    }

    fun location(path: String): Identifier {
        return Identifier.fromNamespaceAndPath(MOD_ID, path)
    }

    fun commonLocation(path: String): Identifier {
        return Identifier.fromNamespaceAndPath("c", path)
    }
}