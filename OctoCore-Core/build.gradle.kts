plugins {
    id("net.octopvp.java-conventions")
    id("io.freefair.lombok") version "6.5.1"
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://repo.octopvp.net/repo")
        name = "octomc"
        credentials {
            username = findProperty("octomcUsername") as String
            password = findProperty("octomcPassword") as String
        }
    }
}
description = "OctoCore Core"
var targetJavaVersion = "1.8" // We're using 1.8 to support 1.8.9 for the core, and so does the 1_8 module, but the 1_19 module uses java 17
dependencies {
    implementation(project(":OctoCore-common"))
    implementation("net.octopvp:Commander-Bukkit:0.0.7-REL") {
        exclude(group = "org.reflections")
    }
    implementation("dev.samstevens.totp:totp:1.7.1")
    implementation("org.slf4j:slf4j-api:2.0.0-alpha1")
    implementation("net.octopvp:agile-gui:1.2.0") // TODO: shadowjar
    implementation("org.reflections:reflections:0.10.2")

    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.yaml")
        exclude(group = "junit")
    }
    compileOnly("com.comphenix.protocol:ProtocolLib:4.6.0")
    //compileOnly(files("../lib/bukkitapi.jar"))
    //compileOnly("net.citizensnpcs:citizens-main:2.0.27-SNAPSHOT")
    compileOnly("com.viaversion:viaversion-api:4.4.2")

    compileOnly("net.octopvp.octospigot:octospigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("net.md-5:bungeecord-chat:1.16-R0.4")
    compileOnly("com.mojang:authlib:1.5.25")
    //compileOnly("net.octopvp:OctoSpigot-Server:1.8.8-R0.1-SNAPSHOT") server is not needed because those are abstracted away into the version specific modules

    // components
    implementation("net.kyori:adventure-text-minimessage:4.14.0")
    implementation("net.kyori:adventure-api:4.14.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.14.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.0")

    implementation("com.github.cryptomorin:XSeries:9.3.1") { isTransitive = false }
    implementation("fr.mrmicky:fastboard:2.0.0")
}

var relocateBase = "net.octopvp.octocore.core.relocate."
tasks {
    compileJava {
        sourceCompatibility = targetJavaVersion
        targetCompatibility = targetJavaVersion
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
        maven ("https://repo.octopvp.net/repo"){
            name = "octomc"
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}

