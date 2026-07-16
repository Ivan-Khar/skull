plugins {
    kotlin("jvm") version "2.4.0" apply false
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.17-SNAPSHOT" apply false
    id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT" apply false
    id("net.neoforged.moddev") version "2.0.141" apply false
}

stonecutter active "26.2-fabric"

stonecutter parameters {
    constants.match(node.metadata.project.substringAfterLast('-'), "fabric", "neoforge")

    listOf("jei", "rei").forEach {
        constants[it] = node.project.hasProperty("deps.$it")
    }

    replacements.string(current.parsed <= "1.21.11", "identifier") {
        replace("Identifier", "ResourceLocation")
        replace("identifier()", "location()")
    }
}
