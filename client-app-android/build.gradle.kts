/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

plugins {
    id("com.android.application")
    id("checkstyle")
}

android {
    compileSdk = 34
    defaultConfig {
        minSdk = 24
        targetSdk = 34
        multiDexEnabled = true
        testInstrumentationRunner = "de.njsm.stocks.client.TestRunner"

        applicationId = "de.njsm.stocks"
        versionCode = 1
        versionName = "4.9.0.1"
    }

    sourceSets {
        create("uimock") {
            java.srcDir("src/uimock/java")
        }
        create("prod") {
            java.srcDir("src/prod/java")
        }
        create("demo") {
            java.srcDir("src/demo/java")
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    testOptions {
        animationsDisabled = true
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    packaging {
        resources {
            excludes += listOf(
                "META-INF/DEPENDENCIES.txt",
                "META-INF/LICENSE.txt",
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md",
                "META-INF/NOTICE.txt",
                "META-INF/NOTICE",
                "META-INF/LICENSE",
                "META-INF/DEPENDENCIES",
                "META-INF/notice.txt",
                "META-INF/license.txt",
                "META-INF/dependencies.txt",
                "META-INF/LGPL2.1"
            )
        }
    }

    flavorDimensions += "type"

    productFlavors {
        create("uimock") {
            dimension = "type"
            applicationIdSuffix = ".uimock"
            versionNameSuffix = "-uimock"
            manifestPlaceholders["appName"] = "@string/app_name_ui"
        }

        create("demo") {
            dimension = "type"
            applicationIdSuffix = ".demo"
            versionNameSuffix = "-demo"
            manifestPlaceholders["appName"] = "@string/app_name_demo"
        }

        create("prod") {
            dimension = "type"
            manifestPlaceholders["appName"] = "@string/app_name"
        }
    }

    lint {
        abortOnError = false
        checkAllWarnings = true
        baseline = project.rootProject.file("config/android-lint/lint.xml")
    }

    namespace = "de.njsm.stocks"
}

dependencies {
    implementation(project(":client-core"))
    implementation(project(":client-ui-android"))
    implementation(project(":client-navigation-android"))
    "uimockImplementation"(project(":client-fakes"))
    "prodImplementation"(project(":client-settings-android"))
    "prodImplementation"(project(":client-database-android"))
    "prodImplementation"(project(":client-network"))
    "prodImplementation"(project(":client-crypto"))
    "demoImplementation"(project(":client-settings-android"))
    "demoImplementation"(project(":client-database-android"))
    "demoImplementation"(project(":client-network"))
    "demoImplementation"(project(":client-crypto"))
    implementation(libs.bundles.dagger.android)
    implementation(libs.slf4j)
    implementation(libs.android.material)
    implementation(libs.bundles.android.navigation)
    implementation(libs.android.preference)
    implementation(libs.rxjava)
    implementation(libs.android.lifecycle.reactivestreams)
    implementation(libs.slf4j)
    implementation(libs.slf4j.android)
    implementation(libs.bundles.zxing)

    annotationProcessor(libs.dagger.processor)
    annotationProcessor(libs.dagger.android.processor)

    androidTestImplementation(libs.junit4)
    androidTestImplementation(libs.mockito)
    androidTestImplementation(libs.mockito.android)
    androidTestImplementation(libs.bundles.android.test)
    androidTestImplementation(libs.android.espresso)
    androidTestImplementation(libs.android.espresso.intent)
    androidTestImplementation(libs.android.espresso.contrib) {
        exclude(group = "org.checkerframework", module = "checker")
    }
    androidTestImplementation(libs.android.test.rules)
    androidTestImplementation(libs.android.test.fragment)
    androidTestImplementation(project(":client-fakes"))
    androidTestImplementation(project(":client-fakes-android"))

    annotationProcessor(libs.dagger.android.processor)
    androidTestAnnotationProcessor(libs.dagger.processor)

    coreLibraryDesugaring(libs.android.desugar)

    constraints {
        implementation(libs.kotlin.jdk7)
        implementation(libs.kotlin.jdk8)
    }
}
