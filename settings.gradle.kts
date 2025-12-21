pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    }
}

rootProject.name = "OctoCore"
include(":OctoCore-common")
include(":OctoCore-Core")
include(":OctoCore-velocity")
include(":OctoCore-master")
include(":OctoCore-1.8")
include(":OctoCore-1.21")