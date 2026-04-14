plugins {
    id("net.octopvp.java-conventions")
    id("io.freefair.lombok") version "8.12.2"
}

description = "OctoRPG - Diary of an 8-Bit Warrior Plugin"

val targetJavaVersion = "21"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":OctoCore-common"))
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
        sourceCompatibility = targetJavaVersion
        targetCompatibility = targetJavaVersion
        options.compilerArgs.add("-parameters")
    }
}
