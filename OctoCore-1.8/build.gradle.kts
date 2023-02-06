plugins {
    id("net.octopvp.java-conventions")
    id("net.octopvp.server-conventions")
    id("io.freefair.lombok") version "6.5.1"
}

repositories {
    mavenCentral()
    mavenLocal()
    maven ("https://repo.octopvp.net/repo"){
        name = "octomc"
        credentials(PasswordCredentials::class)
        authentication {
            create<BasicAuthentication>("basic")
        }
    }
}

dependencies {
    implementation(project(":OctoCore-Core"))
    compileOnly("net.octopvp:OctoSpigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("net.octopvp:OctoSpigot-Server:1.8.8-R0.1-SNAPSHOT")
}
tasks {
    shadowJar {

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
tasks.getByName("build").dependsOn("shadowJar") // maybe jar dependsOn shadowJar?
