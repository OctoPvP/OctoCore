plugins {
    id("net.octopvp.java-conventions")
    id("net.octopvp.server-conventions")
    id("io.freefair.lombok") version "8.6"
}

val octomcUsername = findProperty("octomcUsername") as String?
val octomcPassword = findProperty("octomcPassword") as String?

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven {
        url = uri("https://repo.octopvp.net/repo")
        name = "octomc"
        if (octomcUsername != null && octomcPassword != null) {
            credentials {
                username = octomcUsername
                password = octomcPassword
            }
        }
    }
}

dependencies {
    implementation(project(":OctoCore-Core"))
    implementation(project(":OctoCore-common"))
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")

}
tasks {
    shadowJar {
        archiveFileName.set("OctoCore-1.21.jar")
    }
    withType<ProcessResources> {
        filesMatching("plugin.yml") {
            expand(project.properties)
        }
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(21)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    jar {
        archiveClassifier.set("no_deps")
        archiveVersion.set("")
    }
}
tasks.getByName("build").dependsOn("shadowJar")
java {
    // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}
