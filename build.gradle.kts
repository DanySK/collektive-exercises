import java.awt.Toolkit
import kotlin.math.floor
import kotlin.math.round

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.collektive)
}

group = "org.danilopianini"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.bundles.collektive)
    implementation(libs.bundles.alchemist)
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

val runInSimulation by tasks.registering(JavaExec::class) {
    group = "Collektive exercises"
    description = "Run the simulation"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("it.unibo.alchemist.Alchemist")
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val minResolution = minOf(screenSize.width, screenSize.height)
    val scaleFactor = floor(minResolution.toDouble() / 1080)
    println(scaleFactor)
    jvmArgs = listOf("-Dsun.java2d.uiScale=2.0")
    args = listOf("run", "simulation-environment-entrypoint.yml")
}

val runBasicExercises by tasks.registering(JavaExec::class) {
    group = "Collektive exercises"
    description = "Run the basic exercises"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("it.unibo.alchemist.Alchemist")
    args = listOf("run", "simulation-environment-basicExercises.yml")
}

val runAdvancedExercises by tasks.registering(JavaExec::class) {
    group = "Collektive exercises"
    description = "Run the advanced exercises"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("it.unibo.alchemist.Alchemist")
    args = listOf("run", "simulation-environment-advancedExercises.yml")
}