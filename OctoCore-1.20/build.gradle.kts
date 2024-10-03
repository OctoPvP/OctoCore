plugins {
    id("net.octopvp.java-conventions")
    id("net.octopvp.server-conventions")
    id("io.freefair.lombok") version "6.5.1"
    id("io.papermc.paperweight.userdev") version "1.7.3"
}

repositories {
    mavenCentral()
    mavenLocal()
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
    implementation(project(":OctoCore-Core"))
    implementation(project(":OctoCore-common"))
    paperweightDevBundle("net.octopvp.octospigot", "1.20.4-R0.1-SNAPSHOT")
}
tasks {
    assemble {
        dependsOn(reobfJar)
    }
    shadowJar {
        archiveFileName.set("OctoCore-1.20.jar")
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
        options.release.set(17)
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
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
