import org.gradle.api.tasks.WriteProperties

plugins {
    id("net.octopvp.java-conventions")
    id("net.octopvp.server-conventions")
    id("io.freefair.lombok") version "8.12.2"
}

description = "OctoRPG - rpg plugin for octocore"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    implementation(project(":OctoCore-common"))
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("net.dmulloy2:ProtocolLib:5.4.0")
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.8.11")
    implementation(files("../libs/agile-gui-1.4.0.jar"))
}

tasks {
    shadowJar {
        archiveFileName.set("OctoRPG.jar")
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
    jar {
        archiveClassifier.set("no_deps")
        archiveVersion.set("")
    }
}

tasks.getByName("build").dependsOn("shadowJar")

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}
