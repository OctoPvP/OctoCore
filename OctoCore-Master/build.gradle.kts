plugins {
    id("org.springframework.boot") version "2.7.2"
    id("io.spring.dependency-management") version "1.0.12.RELEASE"
    id("java")
}

description = "OctoCore Master"

configurations {
    compileOnly {
        //extendsFrom(annotationProcessor)
    }
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://build.shibboleth.net/nexus/content/repositories/releases/")
    }
    maven {
        url = uri("https://jitpack.io/")
    }
}

dependencies {
    implementation(project(":OctoCore-common"))

    implementation("com.google.code.gson:gson:2.9.1")
    implementation("net.badbird5907:Lightning:1.1.3-REL")

    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.session:spring-session-core")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity5")
    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:3.1.0")
    implementation("org.springframework.security.extensions:spring-security-saml2-core:1.0.10.RELEASE")
    //implementation("org.springframework.security.extensions:spring-security-saml:1.0.10.RELEASE")
    compileOnly ("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    implementation("com.github.NuVotifier.NuVotifier:nuvotifier-api:2.7.2")
    implementation("com.github.NuVotifier.NuVotifier:nuvotifier-common:2.7.2")

    implementation("com.google.guava:guava:31.1-jre") // Guava for utils and cache
    implementation("com.github.SparklingComet:java-mojang-api:-SNAPSHOT")
    implementation("redis.clients:jedis:4.2.3")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")

    //testImplementation("org.springframework.boot:spring-boot-starter-test")
    //testImplementation("org.springframework.security:spring-security-test")
}

/*
tasks.named("test") {
    useJUnitPlatform()
}
 */
