plugins {
    id("net.octopvp.java-conventions")
    id("net.octopvp.server-conventions")
    id("io.freefair.lombok") version "6.5.1"
    id("io.papermc.paperweight.userdev") version "1.4.0"
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    paperweightDevBundle("net.octopvp.octospigot", "1.19.3-R0.1-SNAPSHOT")
    implementation(project(":OctoCore-Core"))
    compileOnly("net.octopvp.octospigot:OctoSpigot-API:1.19.3-R0.1-SNAPSHOT")
    //compileOnly("net.octopvp:octospigot-server:1.19.3-R0.1-SNAPSHOT")
    //val homeDir = System.getenv("HOMEDRIVE") + System.getenv("HOMEPATH");
    //val s = homeDir + "\\.m2\\repository\\net\\octopvp\\OctoSpigot-Server\\1.19.3-R0.1-SNAPSHOT\\octospigot-server-1.19.3-R0.1-SNAPSHOT-mojang-mapped.jar";
    //compileOnly(files(s))
    compileOnly("com.mojang:authlib:1.5.25")
}
tasks {
    assemble {
        dependsOn(reobfJar)
    }
    shadowJar {

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
