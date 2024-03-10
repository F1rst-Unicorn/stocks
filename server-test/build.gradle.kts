import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.jooq.meta.jaxb.Property
import org.liquibase.gradle.Activity

/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

plugins {
    java
    id("checkstyle")
}

group = "de.njsm.stocks"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<JavaCompile> {
    options.encoding = "utf-8"
}

tasks.test {
    include("**/TestSuite.class")
}

dependencies {
    testImplementation(project(":common"))
    testImplementation(libs.junit4)
    testImplementation(libs.gson)
    testImplementation(libs.guava)
    testImplementation(libs.rest.assured)
    testImplementation(libs.bundles.bouncycastle)
    testImplementation(libs.bundles.jackson)
    testImplementation(libs.jackson.datatype.jdk8)
    testImplementation(libs.jackson.jaxrs)
}

