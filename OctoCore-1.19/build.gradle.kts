plugins {
    id("net.octopvp.java-conventions")
    id("io.freefair.lombok") version "6.5.1"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(project(":OctoCore-Core"))
    compileOnly("net.octopvp:OctoSpigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("net.octopvp:OctoSpigot-Server:1.8.8-R0.1-SNAPSHOT")
}
tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveVersion.set("")
    }
    withType<ProcessResources> {
        filesMatching("plugin.yml") {
            expand(project.properties)
        }
    }
    compileJava {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
    jar {
        archiveClassifier.set("no_deps")
        archiveVersion.set("")
    }
}
tasks.getByName("build").dependsOn("shadowJar")
