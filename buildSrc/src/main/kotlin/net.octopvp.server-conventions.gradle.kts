import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.gradleup.shadow")
}
tasks.withType<ShadowJar>() {
    archiveClassifier.set("")
    archiveVersion.set("")

    relocate("org.reflections", "net.octopvp.octocore.shadow.reflections")
}
