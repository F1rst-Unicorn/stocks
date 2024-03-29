import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.android.build.gradle.internal.tasks.factory.registerTask
import org.gradle.kotlin.dsl.kotlin
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("org.jlleitschuh.gradle.ktlint")
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

ktlint {
    verbose.set(true)
    outputToConsole.set(true)
    coloredOutput.set(true)
    reporters {
        reporter(ReporterType.HTML)
    }
}