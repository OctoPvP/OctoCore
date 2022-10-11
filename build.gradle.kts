plugins {
    id("net.octopvp.java-conventions")
}

dependencies {
    implementation(project(":OctoCore-paper", "shadow"))
    implementation(project(":OctoCore-waterfall", "shadow"))
    implementation(project(":OctoCore-common", "shadow"))
}

description = "OctoCore Parent"
