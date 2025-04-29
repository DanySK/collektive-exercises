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
    args = listOf("run", "simulation-environment.yml")
}
