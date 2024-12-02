pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

rootProject.name = "OctoCore"
include(":OctoCore-common")
include(":OctoCore-Core")
include(":OctoCore-waterfall")
include(":OctoCore-velocity")
include(":OctoCore-master")
include(":OctoCore-1.8")
include(":OctoCore-1.21")
