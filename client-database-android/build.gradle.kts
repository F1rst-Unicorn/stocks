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
    id ("de.njsm.stocks.android")
    id ("checkstyle")
}

android {
    defaultConfig {
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
        }
    }

    sourceSets {
        getByName("androidTest").assets.srcDir("$projectDir/schemas")
    }

    namespace = "de.njsm.stocks.client.database"
}

dependencies {
    implementation(libs.inject)
    implementation(libs.android.room)
    implementation(libs.android.room.rxjava3)
    implementation(libs.android.sqlite)
    implementation(project(":client-core"))
    implementation(libs.rxjava)
    implementation(libs.autovalue)
    implementation(libs.android.lifecycle.extensions)
    implementation(libs.bundles.dagger.android)
    implementation(libs.slf4j)
    implementation(libs.guava)

    annotationProcessor(libs.autovalue.processor)
    annotationProcessor(libs.dagger.processor)
    annotationProcessor(libs.android.room.processor)
    annotationProcessor(libs.dagger.android.processor)

    androidTestImplementation(libs.android.room.testing)

    androidTestAnnotationProcessor(libs.dagger.processor)
    androidTestAnnotationProcessor(libs.autovalue.processor)
}
