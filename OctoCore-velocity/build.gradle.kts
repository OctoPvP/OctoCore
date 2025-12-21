plugins {
    id("net.octopvp.java-conventions")
    id("io.freefair.lombok") version "9.0.0"
    id("net.kyori.blossom") version "2.2.0"
}
repositories {
    mavenCentral()
    mavenLocal()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    implementation(project(":OctoCore-common"))
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
}

val targetJavaVersion = 22
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}
tasks.withType<JavaCompile>().configureEach {
    if (JavaVersion.toVersion(targetCompatibility) >= JavaVersion.VERSION_1_10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(Integer.parseInt(targetCompatibility))
    }
}
val relocateBase = "net.octopvp.octocore.velocity.relocate."
tasks.shadowJar {
    archiveFileName.set("OctoCore-velocity.jar")
    relocate("com.mongodb", relocateBase + "mongodb")
    relocate("redis.clients.jedis", relocateBase + "redis")
}
tasks.getByName("build").dependsOn("shadowJar")
sourceSets {
    main {
        blossom {
            javaSources {
                property("version", project.version.toString())
            }
        }
    }
}

