plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://m2.dv8tion.net/releases")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }

    maven {
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }

    maven {
        url = uri("https://jitpack.io")
    }

    maven {
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }

    maven {
        url = uri("https://repo.dmulloy2.net/repository/public/")
    }

    maven {
        url = uri("https://repo.citizensnpcs.co/")
    }

    maven {
        url = uri("https://repo.viaversion.com")
    }
}

dependencies {
    implementation("org.reflections:reflections:0.10.2")
    implementation("org.mongodb:mongodb-driver-sync:4.2.2")
    implementation("redis.clients:jedis:2.9.0")
    implementation("org.apache.commons:commons-compress:1.21")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("club.minnced:discord-webhooks:0.5.7")
    implementation("org.javatuples:javatuples:1.2")
    implementation("io.sentry:sentry:5.6.0")
    implementation("com.google.guava:guava:31.1-jre")
    implementation("com.github.oshi:oshi-core:5.6.0")

}

group = "net.octopvp"
version = "1.0-SNAPSHOT"
//java.sourceCompatibility = JavaVersion.VERSION_1_8

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}
