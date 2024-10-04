import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("io.github.goooler.shadow")
}
tasks.withType<ShadowJar>() {
    archiveClassifier.set("")
    archiveVersion.set("")

    relocate("org.reflections", "net.octopvp.octocore.shadow.reflections")
}
