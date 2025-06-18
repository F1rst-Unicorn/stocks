plugins {
    id("com.android.library")
    id ("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 35
    defaultConfig {
        minSdk = 24
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    testOptions {
        targetSdk = 35
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
                "META-INF/NOTICE.md",
                "META-INF/NOTICE",
                "META-INF/LICENSE",
                "META-INF/DEPENDENCIES",
                "META-INF/notice.txt",
                "META-INF/license.txt",
                "META-INF/dependencies.txt",
                "META-INF/versions/9/OSGI-INF/MANIFEST.MF",
                "META-INF/LGPL2.1"
            )
        }
    }

    lint {
        targetSdk = 35
        abortOnError = false
        checkAllWarnings = true
        baseline = project.rootProject.file("config/android-lint/lint.xml")
    }
}

// https://github.com/gradle/gradle/issues/25737
val Project.libs: VersionCatalog
    get() = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

dependencies {
    addProvider("testImplementation", provider { libs.findLibrary("junit4").get().get() })
    addProvider("testImplementation", provider { libs.findLibrary("hamcrest").get().get() })
    addProvider("testImplementation", provider { libs.findLibrary("mockito").get().get() })

    addProvider("androidTestImplementation", provider { libs.findLibrary("junit4").get().get() })
    addProvider("androidTestImplementation", provider { libs.findLibrary("mockito-android").get().get() })
    libs.findBundle("hamcrest").get().get().forEach {
        addProvider("androidTestImplementation", provider { it })
    }
    libs.findBundle("android-test").get().get().forEach {
        addProvider("androidTestImplementation", provider { it })
    }

    addProvider("coreLibraryDesugaring", provider { libs.findLibrary("android-desugar").get().get() })

    constraints {
        addProvider("implementation", provider { libs.findLibrary("kotlin-jdk7").get().get() })
        addProvider("implementation", provider { libs.findLibrary("kotlin-jdk8").get().get() })
    }
}
