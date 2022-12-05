plugins {
    id("net.octopvp.java-conventions")
    id("io.freefair.lombok") version "6.5.1"
}

repositories {
    mavenCentral()
    mavenLocal()
}
var targetJavaVersion = "1.8" // We're using 1.8 to support 1.8.9 for the core, and so does the 1_8 module, but the 1_19 module uses java 17
dependencies {
    implementation(project(":OctoCore-common"))
    implementation("net.octopvp:Commander-Bukkit:0.0.1-DEV")
    implementation("com.warrenstrange:googleauth:1.5.0")
    implementation("dev.samstevens.totp:totp:1.7.1")
    implementation("org.slf4j:slf4j-api:2.0.0-alpha1")
    implementation("net.octopvp:agile-gui:1.0.0") // TODO: shadowjar

    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.yaml")
        exclude(group = "junit")
    }
    compileOnly("com.comphenix.protocol:ProtocolLib:4.6.0")
    compileOnly("com.lunarclient:bukkitapi:1.0-SNAPSHOT")
    //compileOnly(files("../lib/bukkitapi.jar"))
    compileOnly("net.citizensnpcs:citizens-main:2.0.27-SNAPSHOT")
    compileOnly("com.viaversion:viaversion-api:4.4.2")

    compileOnly("net.octopvp:OctoSpigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("net.md-5:bungeecord-chat:1.16-R0.4")
    compileOnly("com.mojang:authlib:1.5.25")
    //compileOnly("net.octopvp:OctoSpigot-Server:1.8.8-R0.1-SNAPSHOT") server is not needed because those are abstracted away into the version specific modules
}

var relocateBase = "net.octopvp.octocore.core.relocate."
tasks {
    compileJava {
        sourceCompatibility = targetJavaVersion
        targetCompatibility = targetJavaVersion
    }
}

description = "OctoCore Core"
