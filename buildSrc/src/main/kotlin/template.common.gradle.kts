import gradle.kotlin.dsl.accessors._f84a4400f48078eb8b5915d16c03c4c4.idea
import org.gradle.api.internal.artifacts.dsl.dependencies.DependenciesExtensionModule.module
import org.gradle.kotlin.dsl.invoke
import org.gradle.language.jvm.tasks.ProcessResources
import template.utils.mod

plugins {
    idea
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

val split = name.lastIndexOf('-')
val minecraft: String = name.substring(0, split)
val loader = name.substring(split + 1)

tasks {
    named<ProcessResources>("processResources") {
        exclude {
            val awExclude = it.name.endsWith(".accesswidener") && it.name != "$minecraft.accesswidener"
            val atExclude = it.name.endsWith("-accesstransformer.cfg") && it.name != "$minecraft-accesstransformer.cfg"
            awExclude || atExclude
        }

        val props = mapOf(
            "mod_id" to mod("id"),
            "mod_name" to mod("name"),
            "mod_version" to mod("version"),
            "mod_description" to mod("description"),
            "mod_author" to mod("author"),
            "mod_license" to mod("license"),
            "minecraft_version" to minecraft
        )

        inputs.properties(props)
        filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml", "pack.mcmeta")) {
            expand(props)
        }
    }
}

apply {
    project.extensions.extraProperties.set("minecraft", minecraft)
    project.extensions.extraProperties.set("loader", loader)
}
