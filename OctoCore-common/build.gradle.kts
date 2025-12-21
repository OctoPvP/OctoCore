plugins {
    id("net.octopvp.java-conventions")
    id("io.freefair.lombok") version "9.0.0"
    id("maven-publish")
}
description = "OctoCore Commons"

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencies {
    implementation("org.objenesis:objenesis:3.3")
    //implementation("org.slf4j:slf4j-log4j12:1.7.5")
    compileOnly("com.google.code.gson:gson:2.8.9")
    implementation("com.warrenstrange:googleauth:1.4.0")
    implementation("net.kyori:adventure-api:4.14.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.14.0")
    // implementation("com.yubico:webauthn-server-core:2.4.0")
}

var relocateBase = "net.octopvp.octocore.common.relocate."
tasks {
    shadowJar {
        archiveFileName.set("OctoCore-common.jar")

        dependencies {
            exclude(
                    "*"
            )
        }
    }
    compileJava {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
    jar {
        archiveBaseName.set("OctoCore-common-no-deps")
    }
}