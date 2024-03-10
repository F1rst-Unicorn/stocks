plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    implementation(libs.android.library)
    implementation(libs.android.application)
    implementation(libs.android.navigation.safeargs)
    implementation(libs.jooq.plugin)
    implementation(libs.jaxb.api)
}