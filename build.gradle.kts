import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
}

group = "org.test"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

sourceSets {
    main {
        java.srcDir("src")
    }
    test {
        java.srcDir("test")
    }
}

kotlin {
    sourceSets.all {
        languageSettings.useExperimentalAnnotation("kotlin.time.ExperimentalTime")
        languageSettings.useExperimentalAnnotation("kotlin.io.path.ExperimentalPathApi")
        languageSettings.useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
    }
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
