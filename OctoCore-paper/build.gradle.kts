plugins {
    id("net.octopvp.java-conventions")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(project(":OctoCore-common"))
    implementation("net.octopvp:Commander-Bukkit:0.0.1-DEV")
    implementation("com.warrenstrange:googleauth:1.5.0")
    implementation("dev.samstevens.totp:totp:1.7.1")
    implementation("org.slf4j:slf4j-api:2.0.0-alpha1")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.yaml")
        exclude(group = "junit")
    }
    compileOnly("com.comphenix.protocol:ProtocolLib:4.6.0")
    compileOnly("com.lunarclient:bukkitapi:1.0-SNAPSHOT")
    //compileOnly(files("../lib/bukkitapi.jar"))
    compileOnly("net.citizensnpcs:citizens-main:2.0.27-SNAPSHOT")
    compileOnly("net.octopvp:OctoSpigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("com.viaversion:viaversion-api:4.4.2")
    compileOnly("net.octopvp:OctoSpigot:1.8.8-R0.1-SNAPSHOT")

    //system("net.octopvp:OctoSpigot:1.8.8-R0.1-SNAPSHOT")
}

var relocateBase = "net.octopvp.octocore.paper.relocate."
tasks {
    shadowJar {
        relocate("net.octopvp.commander", relocateBase + "commander")
        relocate("com.mongodb", relocateBase + "mongodb")
        relocate("redis.clients.jedis", relocateBase + "redis")
        archiveFileName.set("OctoCore-paper.jar")
    }

    jar {
        archiveBaseName.set("OctoCore-paper-no-deps")
    }
}

description = "OctoCore Paper"
