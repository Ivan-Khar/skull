@file:Suppress("UnstableApiUsage")

import multiloader.utils.*

plugins {
    kotlin("jvm")
    id("multiloader.common")
    id("net.fabricmc.fabric-loom")
    id("com.google.devtools.ksp") version "2.3.9"
    alias(ft.plugins.default)
    alias(ft.plugins.fabric)
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
    maven("https://maven.nucleoid.xyz")

    strictMaven("https://www.cursemaven.com", "Curseforge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
}

val minecraft: String by project
val loader: String by project
base.archivesName = "${mod("id")}-${mod("version")}+$minecraft-$loader"

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")

    implementation("net.fabricmc:fabric-loader:0.19.3")
    implementation("net.fabricmc.fabric-api:fabric-api:${deps("fabric_api")}")
    implementation("net.fabricmc:fabric-language-kotlin:${deps("fabric-language-kotlin")}")
    implementation("eu.pb4:polymer-core:${deps("polymer")}")
    implementation("eu.pb4:polymer-virtual-entity:${deps("polymer")}")

}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

kotlin {
    jvmToolchain(25)
}

loom {
    accessWidenerPath.set(rootProject.file("src/main/resources/$minecraft.accesswidener"))

    fabricApi {
        configureDataGeneration {
            createRunConfiguration = true
            client = true
            modId = "skull"
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
    fabric.configure(sourceSets.main) {
        entrypoint("fabric-datagen", "net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint")
        entrypoint("fabric-client-gametest", "net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest")
    }

    mixins.configure(sourceSets.main) {
        mixin("skull.mixins.json", "default")
        mixin("skull.client.mixins.json", "client") { env("CLIENT") }
        mixin("skull.fabric.mixins.json", "fabric")
    }
}

tasks {
    register<Copy>("buildAndCollect") {
        group = "build"
        from(jar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }

    jar {
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
    packageName("one.theaq.skull")
    useJavaOutput()

    buildConfigField("String", "MINECRAFT_VERSION", "\"$minecraft\"")
    buildConfigField("String", "LOADER", "\"$loader\"")
    buildConfigField("String", "MOD_VERSION", "\"${mod("version")}\"")
    buildConfigField("int", "DATA_VERSION", mod("version")!!.replace(".", "").toInt())
}
