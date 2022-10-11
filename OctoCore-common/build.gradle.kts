plugins {
    id("net.octopvp.java-conventions")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
}

dependencies {
    implementation("com.github.oshi:oshi-core:5.6.0")
    compileOnly("io.github.waterfallmc:waterfall-chat:1.18-R0.1-SNAPSHOT")
    compileOnly("com.google.code.gson:gson:2.8.9")
}

var relocateBase = "net.octopvp.octocore.common.relocate."
tasks {
    shadowJar {
        archiveFileName.set("OctoCore-common.jar")

        dependencies {
            exclude(

            )
        }
    }

    jar {
        archiveBaseName.set("OctoCore-common-no-deps")
    }
}


description = "OctoCore Commons"
