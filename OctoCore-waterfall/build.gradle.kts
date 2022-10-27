plugins {
    id("net.octopvp.java-conventions")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.freefair.lombok") version "6.5.1"
}
repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
}
dependencies {
    implementation(project(":OctoCore-common"))
    compileOnly("io.github.waterfallmc:waterfall-api:1.18-R0.1-SNAPSHOT")
    //compileOnly("net.octopvp:OctoBungee-api:1.17-R0.1-SNAPSHOT")
    compileOnly("com.google.code.gson:gson:2.8.9")
}
var relocateBase = "net.octopvp.octocore.waterfall.relocate."
tasks {
    shadowJar {
        relocate("com.mongodb", relocateBase + "mongodb")
        relocate("redis.clients.jedis", relocateBase + "redis")
        relocate("net.badbird5907.blib", relocateBase + "blib")
        archiveFileName.set("OctoCore-waterfall.jar")
    }

    jar {
        archiveBaseName.set("OctoCore-waterfall-no-deps")
    }
}
description = "OctoCore Waterfall"
