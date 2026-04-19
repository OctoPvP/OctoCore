import org.gradle.api.tasks.WriteProperties

plugins {
    id("net.octopvp.java-conventions")
    id("net.octopvp.server-conventions")
    id("io.freefair.lombok") version "9.0.0"
}

description = "OctoRPG - rpg plugin for octocore"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://repo.citizensnpcs.co/")
    maven("https://repo.alessiodp.com/releases/")
    maven("https://repo.byteflux.net/repository/jdk/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly(project(":OctoCore-common"))
    compileOnly(project(":OctoCore-Core"))
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("net.dmulloy2:ProtocolLib:5.4.0")
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.8.11")
    compileOnly("net.citizensnpcs:citizens-main:2.0.35-SNAPSHOT")
    compileOnly("net.byteflux:libby-bukkit:1.3.1")
    compileOnly("fr.mrmicky:fastboard:2.1.5")
    implementation(files("../libs/agile-gui-1.4.0.jar"))
}

tasks {
    shadowJar {
        archiveFileName.set("OctoRPG.jar")
        //fix for bundling mongo with octocore and rpg
        exclude("com/mongodb/**")
        exclude("org/bson/**")
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
