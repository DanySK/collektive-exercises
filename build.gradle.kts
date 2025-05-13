import org.gradle.jvm.toolchain.internal.JavaToolchain
import java.awt.Toolkit
import kotlin.math.ceil
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

fun JavaToolchainSpec.java21() = languageVersion.set(JavaLanguageVersion.of(21))

kotlin {
    jvmToolchain { java21() }
}

val runInSimulation by tasks.registering(JavaExec::class) {
    group = "Collektive exercises"
    description = "Run the simulation"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("it.unibo.alchemist.Alchemist")
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val minResolution = minOf(screenSize.width, screenSize.height)
    val scaleFactor = round(minResolution.toDouble() / 1080)
    println(scaleFactor)
    jvmArgs = listOf("-Dsun.java2d.uiScale=$scaleFactor")
    args = listOf("run", "simulation-environment.yml")
    // Use Java 21 via Gradle toolchains
    javaLauncher.set(javaToolchains.launcherFor { java21() })
}
