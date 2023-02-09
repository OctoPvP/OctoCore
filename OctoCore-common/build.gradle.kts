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
    maven {
        url = uri("https://repo.octopvp.net/repo")
        name = "octomc"
        credentials {
            username = findProperty("octomcUsername") as String
            password = findProperty("octomcPassword") as String
        }
    }
}

dependencies {
    implementation("org.objenesis:objenesis:3.3")
    //implementation("org.slf4j:slf4j-log4j12:1.7.5")
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
    compileJava {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
    jar {
        archiveBaseName.set("OctoCore-common-no-deps")
    }
}


description = "OctoCore Commons"
