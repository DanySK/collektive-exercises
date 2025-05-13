import org.gradle.jvm.toolchain.internal.JavaToolchain
import java.awt.Toolkit
import kotlin.math.floor

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
    val scaleFactor = floor(minResolution.toDouble() / 1080)
    jvmArgs = listOf("-Dsun.java2d.uiScale=$scaleFactor")
    args = listOf("run", "simulation-environment.yml")
    // Use Java 21 via Gradle toolchains
    javaLauncher.set(javaToolchains.launcherFor { java21() })
}
