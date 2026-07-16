import dev.kikugie.fletching_table.annotation.MixinEnvironment
import template.utils.*

plugins {
    kotlin("jvm")
    id("net.neoforged.moddev")
    id("template.common")
    id("com.google.devtools.ksp") version "2.3.9"
    id("dev.kikugie.fletching-table.neoforge") version "0.1.0-alpha.22"
    id("com.github.gmazzo.buildconfig") version "5.7.1"
}

repositories {
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }

    maven("https://maven.bawnorton.com/releases")
    maven("https://maven.quiltmc.org/repository/release/")
    maven("https://maven.blamejared.com/")
    maven("https://maven.shedaniel.me/")
    maven("https://thedarkcolour.github.io/KotlinForForge/")
    maven("https://maven.parchmentmc.org")

    strictMaven("https://www.cursemaven.com", "Curseforge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
}

val minecraft: String by project
val loader: String by project
base.archivesName = "${mod("id")}-${mod("version")}+$minecraft-$loader"

dependencies {
    remoteDepBuilder(project, fletchingTable::modrinth)

    deps("kotlinforforge-neoforge") { runtimeOnly("thedarkcolour:kotlinforforge-neoforge:${it}") }
    deps("sodium") {
        implementation("maven.modrinth:sodium:$it")
    }
}

java {
    withSourcesJar()
    sourceCompatibility = if (sc.current.parsed >= "26.0") JavaVersion.VERSION_25 else JavaVersion.VERSION_21
    targetCompatibility = if (sc.current.parsed >= "26.0") JavaVersion.VERSION_25 else JavaVersion.VERSION_21
}

kotlin {
    jvmToolchain(if (sc.current.parsed >= "26.0") 25 else 21)
}

neoForge {
    version = deps("neoforge")

    validateAccessTransformers = true
    accessTransformers.from(rootProject.file("src/main/resources/$minecraft-accesstransformer.cfg"))

    mods {
        register(mod("id")!!) {
            sourceSet(sourceSets["main"])
        }
    }

    if (sc.current.parsed < "26.0") {
        deps("parchment") {
            parchment {
                val (mc, version) = it.split(':')
                mappingsVersion = version
                minecraftVersion = mc
            }
        }
    }

    runs {
        all {
            gameDirectory = rootProject.file("run")
        }

        register("client") {
            ideName = "NeoForge Client $minecraft"
            client()

            programArgument("--username=Aqua_tic")
            programArgument("--uuid=83d32844-e63e-4e24-a032-c0647c058de2")
        }

        if (sc.current.parsed >= "1.21.4") {
            register("serverData") {
                ideName = "NeoForge Server Datagen $minecraft"
                serverData()
                programArguments.addAll(
                    "--mod", "${mod("id")}",
                    "--output", project.file("src/main/generated/server").toString()
                )
            }

            register("clientData") {
                ideName = "NeoForge Client Datagen $minecraft"
                clientData()
                programArguments.addAll(
                    "--mod", "${mod("id")}",
                    "--output", project.file("src/main/generated").toString()
                )
            }
        } else {
            register("data") {
                ideName = "NeoForge Datagen $minecraft"
                data()
                programArguments.addAll(
                    "--mod", "${mod("id")}",
                    "--output", project.file("src/main/generated").toString()
                )
            }
        }

        register("server") {
            ideName = "NeoForge Server $minecraft"
            server()
        }

    }

    afterEvaluate {
        runs.configureEach {
            applyMixinDebugSettings(::jvmArgument, ::systemProperty)
        }
    }
}

fletchingTable {
    mixins.register("main") {
        mixin("default", "template.mixins.json")
        mixin("client", "template.client.mixins.json") {
            environment = MixinEnvironment.Env.CLIENT
        }
    }
}

sourceSets.main {
    resources.srcDirs(project.file("src/main/generated"))
    resources.exclude(".cache")
}

tasks {
    named("createMinecraftArtifacts") {
        dependsOn("stonecutterGenerate")
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        from(jar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }

    build {
        dependsOn("runServerData", "runClientData")
    }

    processResources {
        exclude("fabric.mod.json", "template.fabric.mixins.json")
        exclude { it.name.endsWith(".accesswidener") }
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
