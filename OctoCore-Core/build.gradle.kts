plugins {
    id("net.octopvp.java-conventions")
    id("io.freefair.lombok") version "8.12.2"
}

description = "OctoCore Core"

// We're using 1.8 to support 1.8.9 for the core, and so does the 1_8 module, but the 1_19 module uses java 17
val targetJavaVersion = "21"
val relocateBase = "net.octopvp.octocore.core.relocate."

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")

    maven("https://repo.octopvp.net/public") {
        name = "octomc-public"
    }
    
    maven("https://repo.octopvp.net/repo") {
        name = "octomc"
        credentials {
            username = findProperty("octomcUsername") as String
            password = findProperty("octomcPassword") as String
        }
    }
}

dependencies {
    // -- Internal Modules --
    implementation(project(":OctoCore-common"))

    // -- Platform / Server API --
    // compileOnly("net.octopvp.octospigot:octospigot-api:1.21.11-R0.1-SNAPSHOT")
    // instead of octospigot using spigots actual api
    compileOnly(files("../libs/octospigot-api-1.21.10-R0.1-SNAPSHOT.jar"))
    
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    // compileOnly("org.spigotmc:spigot:1.21.11-R0.1-SNAPSHOT")
    // compileOnly(files("../lib/bukkitapi.jar"))
    // compileOnly(":OctoSpigot-API")
    // compileOnly("net.octopvp:OctoSpigot-Server:1.8.8-R0.1-SNAPSHOT") // server is not needed because those are abstracted away into the version specific modules

    compileOnly("com.mojang:authlib:3.13.56")
    compileOnly("net.md-5:bungeecord-chat:1.16-R0.4")

    // -- Libraries --
    implementation("net.octopvp:Commander-Bukkit:0.0.12-REL") {
        exclude(group = "org.reflections")
    }
    implementation("dev.samstevens.totp:totp:1.7.1")
    implementation("org.slf4j:slf4j-api:2.0.0-alpha1")
    implementation("org.reflections:reflections:0.10.2")

    // -- UI & GUI --
    // implementation("dev.octomc:agile-gui:1.4.0") // TODO: shadowjar
    implementation(files("../libs/agile-gui-1.4.0.jar")) // this is a hack

    // -- Plugin Integrations (Soft Depends) --
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.yaml")
        exclude(group = "junit")
    }
    compileOnly("net.dmulloy2:ProtocolLib:5.4.0")
    // compileOnly("net.citizensnpcs:citizens-main:2.0.27-SNAPSHOT")
    compileOnly("com.viaversion:viaversion-api:4.4.2")

    // -- Adventure & Components --
    implementation("net.kyori:adventure-text-minimessage:4.25.0")
    implementation("net.kyori:adventure-api:4.25.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.25.0")
    implementation("net.kyori:adventure-platform-bukkit:4.4.1")

    implementation("com.github.cryptomorin:XSeries:9.3.1") { isTransitive = false }
    implementation("fr.mrmicky:fastboard:2.1.5")
}

tasks {
    compileJava {
        sourceCompatibility = targetJavaVersion
        targetCompatibility = targetJavaVersion
        options.compilerArgs.add("-parameters")
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(sourceSets["main"].allJava)
}

artifacts {
    add("archives", javadocJar)
    add("archives", sourcesJar)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            pom {
                name.set("OctoCore-common")
                description.set("OctoCore common module")
                url.set("https://github.com/OctoPvP/OctoCore")
                
                from(components["java"])
                artifact(sourcesJar)
                artifact(javadocJar)
                
                scm {
                    url.set("https://github.com/OctoPvP/OctoCore")
                }
            }
        }
    }
    repositories {
        maven("https://repo.octopvp.net/repo") {
            name = "octomc"
            credentials {
                username = findProperty("octomcUsername") as String
                password = findProperty("octomcPassword") as String
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}