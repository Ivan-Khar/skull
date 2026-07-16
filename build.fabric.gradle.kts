@file:Suppress("UnstableApiUsage")

import dev.kikugie.fletching_table.annotation.MixinEnvironment
import org.gradle.kotlin.dsl.mappings
import org.gradle.kotlin.dsl.remapJar
import template.utils.*

plugins {
    kotlin("jvm")
    id("template.common")
    id("fabric-loom")
    id("com.google.devtools.ksp") version "2.3.9"
    id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.22"
    id("com.github.gmazzo.buildconfig") version "5.7.1"
}

repositories {
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroupAndSubgroups) }
    }

    maven("https://maven.bawnorton.com/releases")
    maven("https://maven.quiltmc.org/repository/release/")
    maven("https://maven.blamejared.com/")
    maven("https://maven.shedaniel.me/")
    maven("https://maven.parchmentmc.org")

    strictMaven("https://www.cursemaven.com", "Curseforge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
}

val minecraft: String by project
val loader: String by project
base.archivesName = "${mod("id")}-${mod("version")}+$minecraft-$loader"

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings(loom.layered {
        officialMojangMappings()
        if (sc.current.parsed < "26.0") deps("parchment") {
            parchment("org.parchmentmc.data:parchment-$it@zip")
        }
    })

    modImplementation("net.fabricmc:fabric-loader:0.17.3")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${deps("fabric_api")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${deps("fabric-language-kotlin")}")

    remoteDepBuilder(project, fletchingTable::modrinth)
        .dep("sodium") { modImplementation(it) }
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    jvmToolchain(21)
}

loom {
    accessWidenerPath.set(rootProject.file("src/main/resources/$minecraft.accesswidener"))

    fabricApi {
        configureDataGeneration {
            createRunConfiguration = true
            client = true
            modId = "template"
        }

        configureTests {
            enableGameTests = false
            eula = true
            clearRunDirectory = false
        }
    }

    //https://github.com/NikitaCartes/EasyAuth/blob/stonecutter/build.fabric-deobf.gradle.kts#L51-L53
    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1") // Adds names to lambdas - useful for mixins
    }

    runConfigs.all {
        ideConfigGenerated(true)
        runDir = "../../run"
    }

    runConfigs["client"].apply {
        programArgs("--username=Aqua_tic", "--uuid=83d32844-e63e-4e24-a032-c0647c058de2")
        name = "Fabric Client $minecraft"
    }

    runConfigs["server"].apply {
        name = "Fabric Server $minecraft"
    }

    runConfigs["clientGameTest"].apply {
        name = "Fabric Client Game Test $minecraft"
    }

    runConfigs["datagen"].apply {
        name = "Fabric Data Generation $minecraft"
    }

    afterEvaluate {
        runConfigs.configureEach {
            applyMixinDebugSettings(::vmArg, ::property)
        }
    }
}

fletchingTable {
    fabric {
        entrypointMappings.put("fabric-datagen", "net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint")
        entrypointMappings.put(
            "fabric-client-gametest",
            "net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest"
        )
    }

    mixins.register("main") {
        mixin("default", "template.mixins.json")
        mixin("client", "template.client.mixins.json") {
            environment = MixinEnvironment.Env.CLIENT
        }
        mixin("fabric", "template.fabric.mixins.json")
    }
}

tasks {
    register<Copy>("buildAndCollect") {
        group = "build"
        from(remapJar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }

    remapJar {
        dependsOn("runDatagen")
    }

    named<Jar>("sourcesJar") {
        dependsOn("runDatagen")
    }

    processResources {
        exclude("META-INF/neoforge.mods.toml")
        exclude { it.name.endsWith("-accesstransformer.cfg") }
    }
}

buildConfig {
    className("TemplateConstants")
    packageName("one.theaq.template")
    useJavaOutput()

    buildConfigField("String", "MINECRAFT_VERSION", "\"$minecraft\"")
    buildConfigField("String", "LOADER", "\"$loader\"")
    buildConfigField("String", "MOD_VERSION", "\"${mod("version")}\"")
    buildConfigField("int", "DATA_VERSION", mod("version")!!.replace(".", "").toInt())
}