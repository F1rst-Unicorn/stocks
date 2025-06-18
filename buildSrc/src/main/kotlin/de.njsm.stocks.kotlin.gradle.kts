import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.gradle.kotlin.dsl.kotlin
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("org.jlleitschuh.gradle.ktlint")
    id("de.njsm.stocks.java")
}

dependencies {
    testImplementation(kotlin("stdlib-jdk8"))
}

tasks.check.dependsOn(tasks.ktlintCheck)
tasks.register("format") {
    group = "formatting"
    description = "Format source files"
    dependsOn(tasks.ktlintFormat)
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

ktlint {
    verbose.set(true)
    outputToConsole.set(true)
    coloredOutput.set(true)
    reporters {
        reporter(ReporterType.HTML)
    }
}