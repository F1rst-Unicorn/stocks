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
        testInstrumentationRunner = "de.njsm.stocks.client.TestRunner"
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

plugins {
    id("de.njsm.stocks.android")
    id("checkstyle")
}

android {
    namespace = "de.njsm.stocks.client.ui"
    defaultConfig {
        testInstrumentationRunner = "de.njsm.stocks.client.TestRunner"
    }
}

dependencies {
    implementation(project(":client-core"))
    implementation(libs.inject)
    implementation(libs.rxjava)
    implementation(libs.autovalue)
    implementation(libs.android.core.common)
    implementation(libs.android.lifecycle.reactivestreams)
    implementation(libs.android.lifecycle.extensions)
    implementation(libs.android.paging.runtime)
    implementation(libs.android.paging.rxjava)
    implementation(libs.android.coordinatorlayout)
    implementation(libs.android.swiperefreshlayout)
    implementation(libs.android.recyclerview)
    implementation(libs.android.lifecycle.viewmodel)
    implementation(libs.android.preference)
    implementation(libs.android.material)
    implementation(libs.dagger)
    implementation(libs.bundles.dagger.android)
    implementation(libs.slf4j)
    implementation(libs.rxjava.android)
    implementation(libs.bundles.zxing)
    implementation(libs.mpandroidchart)
    implementation(libs.guava)

    annotationProcessor(libs.autovalue.processor)
    annotationProcessor(libs.dagger.processor)
    annotationProcessor(libs.dagger.android.processor)

    androidTestImplementation(project(":client-fakes"))
    androidTestImplementation(project(":client-fakes-android"))
    androidTestImplementation(libs.android.espresso)
    androidTestImplementation(libs.android.espresso.intent)
    androidTestImplementation(libs.android.espresso.contrib) {
        exclude(group = "org.checkerframework", module = "checker")
    }
    androidTestImplementation(libs.android.test.rules)
    androidTestImplementation(libs.android.test.fragment)

    androidTestAnnotationProcessor(libs.dagger.android.processor)
    androidTestAnnotationProcessor(libs.dagger.processor)
}
