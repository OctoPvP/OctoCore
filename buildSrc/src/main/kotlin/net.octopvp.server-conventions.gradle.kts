import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow")
}
tasks.withType<ShadowJar>() {
    archiveClassifier.set("")
    archiveVersion.set("")

    relocate("org.reflections", "net.octopvp.reflections")
}
