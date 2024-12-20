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
    id("de.njsm.stocks.kotlin")
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
    systemProperty("junit.jupiter.testclass.order.default", "org.junit.jupiter.api.ClassOrderer\$OrderAnnotation")
}

dependencies {
    testImplementation(project(":common"))
    testImplementation(project(":client-core"))
    testImplementation(project(":client-crypto"))
    testImplementation(project(":client-network"))
    testImplementation(libs.junit5)
    testImplementation(libs.junit.platform)
    testImplementation(libs.assertj)
    testImplementation(libs.gson)
    testImplementation(libs.guava)
    testImplementation(libs.rest.assured)
    testImplementation(libs.bundles.bouncycastle)
    testImplementation(libs.bundles.jackson)
    testImplementation(libs.jackson.datatype.jdk8)
    testImplementation(libs.jackson.jaxrs)
    testImplementation(libs.dagger)
    testAnnotationProcessor(libs.dagger.processor)
    kaptTest(libs.dagger.processor)
}
