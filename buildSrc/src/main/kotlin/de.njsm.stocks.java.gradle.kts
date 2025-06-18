plugins {
    id ("java-library")
    id ("checkstyle")
}

group = "de.njsm.stocks"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile> {
    options.encoding = "utf-8"
}

tasks.test {
    useJUnitPlatform()
}

// https://github.com/gradle/gradle/issues/25737
val Project.libs: VersionCatalog
    get() = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

dependencies {
    addProvider("testImplementation", provider { libs.findLibrary("junit5").get().get() })
    addProvider("testImplementation", provider { libs.findLibrary("hamcrest").get().get() })
    addProvider("testImplementation", provider { libs.findLibrary("mockito").get().get() })
    addProvider("testImplementation", provider { libs.findLibrary("mockito-junit5").get().get() })
    libs.findBundle("log4j").get().get().forEach {
        addProvider("testImplementation", provider { it })
    }
}
