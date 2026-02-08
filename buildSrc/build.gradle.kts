plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    implementation(libs.kotlin.plugin)
    implementation(libs.kotlin.android)
    implementation(libs.android.library)
    implementation(libs.android.application)
    implementation(libs.android.navigation.safeargs)
    implementation(libs.jooq.plugin)
    implementation(libs.liquibase)
    implementation(libs.liquibase.plugin)
    implementation(libs.spring.dependency.management)
    implementation(libs.spring.boot.plugin)
    implementation(libs.jaxb.api)
    implementation(libs.ktlint)
}