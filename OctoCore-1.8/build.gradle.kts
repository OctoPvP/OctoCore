plugins {
	id("net.octopvp.java-conventions")
	id("net.octopvp.server-conventions")
	id("io.freefair.lombok") version "6.5.1"
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

	compileOnly("net.octopvp.octospigot:octospigot-api:1.8.8-R0.1-SNAPSHOT")
	compileOnly("net.octopvp.octospigot:octospigot-server:1.8.8-R0.1-SNAPSHOT")

	implementation("net.kyori:adventure-text-minimessage:4.14.0")
	implementation("net.kyori:adventure-api:4.14.0")
	implementation("net.kyori:adventure-text-serializer-legacy:4.14.0")
	implementation("net.kyori:adventure-platform-bukkit:4.3.0")
}
tasks {
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
