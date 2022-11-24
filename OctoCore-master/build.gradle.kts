buildscript {
    repositories {
        mavenCentral()
        maven { url = uri("https://maven.vaadin.com/vaadin-prereleases") }
        maven { url = uri("https://maven.vaadin.com/vaadin-addons") }
    }
}
plugins {
    id("org.springframework.boot") version "2.7.4"
    id("io.spring.dependency-management") version "1.0.14.RELEASE"
    id("java")
    id("com.vaadin") version "23.2.2"
    //id("net.octopvp.java-conventions") // Our default library config conflicts with spring boot
    id("io.freefair.lombok") version "6.5.1"
}

defaultTasks("clean", "build")

//sourceCompatibility = '17'

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://maven.vaadin.com/vaadin-prereleases") }
    maven { url = uri("https://maven.vaadin.com/vaadin-addons") }
    maven { url = uri("https://jitpack.io/") }
    maven {
        name = "vaadin-addons"
        url = uri("https://maven.vaadin.com/vaadin-addons")
    }
}
dependencies {
    implementation(project(":OctoCore-common"))
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.security:spring-security-oauth2-jose")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    implementation("com.github.NuVotifier.NuVotifier:nuvotifier-api:2.7.2")
    implementation("com.github.NuVotifier.NuVotifier:nuvotifier-common:2.7.2")

    implementation("com.google.guava:guava:31.1-jre") // Guava for utils and cache

    implementation("io.jsonwebtoken:jjwt:0.9.1")
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("net.badbird5907:Lightning:1.1.3-REL")
    implementation("commons-validator:commons-validator:1.7")
    implementation("org.apache.commons:commons-lang3:3.12.0")

    implementation("com.vaadin:vaadin-spring-boot-starter")
    implementation("net.dv8tion:JDA:5.0.0-alpha.22")
    implementation("com.github.SparklingComet:java-mojang-api:-SNAPSHOT")
    //implementation("com.github.appreciated:apexcharts:23.0.0-LOCAL")

    implementation("org.springframework.security.extensions:spring-security-saml2-core:2.0.0.M31")
}

dependencyManagement {
    imports {
        mavenBom("com.vaadin:vaadin-bom:23.2.2")
    }
}

// The val pnpmEnable: following = true is not needed as pnpm is used by default,
// this is just an example of how to configure the Gradle Vaadin Plugin:
// for more configuration options please see: https://vaadin.com/docs/latest/guide/start/gradle/#all-options
vaadin {
    pnpmEnable = true
}
//tasks.register("prepareKotlinBuildScriptModel"){}
description = "OctoCore Master"
