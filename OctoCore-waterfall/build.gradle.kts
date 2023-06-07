plugins {
    id("net.octopvp.java-conventions")
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
    compileOnly("io.github.waterfallmc:waterfall-api:1.19-R0.1-SNAPSHOT")
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
    compileJava {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    jar {
        archiveClassifier.set("no_deps")
        archiveVersion.set("")
    }
}
tasks.getByName("build").dependsOn("shadowJar")
description = "OctoCore Waterfall"
