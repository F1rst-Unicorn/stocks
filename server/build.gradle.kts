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
    war
    id ("checkstyle")
    id("org.jooq.jooq-codegen-gradle")
    id("org.liquibase.gradle")
}

group = "de.njsm.stocks"
version = "5.7.0.4"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile> {
    options.encoding = "utf-8"
}

dependencies {
    implementation(project(":common"))
    implementation(libs.postgresql)
    implementation(libs.c3p0)
    implementation(libs.commons.io)
    implementation(libs.autovalue)
    implementation(libs.guava)
    implementation(libs.bouncycastle.pkix)
    implementation(libs.functionaljava)
    implementation(libs.functionaljava.java8)
    implementation(libs.prometheus)
    implementation(libs.prometheus.servlet)
    implementation(libs.quartz)
    implementation(libs.liquibase) {
        exclude(group = "ch.qos.logback", module = "logback-classic")
    }
    implementation(libs.liquibase.slf4j)
    implementation(libs.jakarta.servlet.api)
    implementation(libs.bundles.jersey)
    implementation(libs.bundles.jackson)
    implementation(libs.jackson.datatype.jdk8)
    implementation(libs.jackson.jaxrs)
    implementation(libs.bundles.jooq)
    implementation(libs.bundles.spring)
    implementation(libs.bundles.log4j)

    annotationProcessor(libs.autovalue.processor)

    jooqCodegen(libs.postgresql)
    liquibaseRuntime(libs.liquibase) {
        exclude(group = "ch.qos.logback", module = "logback-classic")
    }
    liquibaseRuntime(libs.postgresql)
    liquibaseRuntime(libs.picocli)
    liquibaseRuntime(libs.jaxb.api)

    testImplementation(libs.junit5)
    testImplementation(libs.hamcrest)
    testImplementation(libs.mockito)
}

val profile = if (project.properties["profile"] == "teamcity") {
    val postgresDb = System.getenv("POSTGRESQL_DB")
    val userHome = System.getProperty("user.home")
    mapOf(
        "de.njsm.stocks.server.v2.db.host" to "eregion.m.njsm.de",
        "de.njsm.stocks.server.v2.db.port" to "5432",
        "de.njsm.stocks.server.v2.db.name" to postgresDb,
        "de.njsm.stocks.server.v2.db.postgres.user" to postgresDb,
        "de.njsm.stocks.server.v2.db.postgres.password" to "linux",
        "de.njsm.stocks.server.v2.db.postgres.ssl" to "true",
        "de.njsm.stocks.server.v2.db.postgres.sslmode" to "verify-ca",
        "de.njsm.stocks.server.v2.db.postgres.sslcert" to "${userHome}/ssl/db.j.njsm.de.crt",
        "de.njsm.stocks.server.v2.db.postgres.sslkey" to "${userHome}/ssl/db.j.njsm.de.pk8",
        "de.njsm.stocks.server.v2.db.postgres.sslrootcert" to "${userHome}/ssl/ca.crt",
        "de.njsm.stocks.internal.ticketValidityTimeInMinutes" to "10",
        "de.njsm.stocks.server.v2.db.history.maxPeriod" to "P3Y",
        "liquibase.promptOnNonLocalDatabase" to "false",
    )
} else {
    mapOf(
        "de.njsm.stocks.server.v2.db.host" to "localhost",
        "de.njsm.stocks.server.v2.db.port" to "5432",
        "de.njsm.stocks.server.v2.db.name" to "stocks",
        "de.njsm.stocks.server.v2.db.postgres.user" to "stocks",
        "de.njsm.stocks.server.v2.db.postgres.password" to "linux",
        "de.njsm.stocks.server.v2.db.postgres.ssl" to "false",
        "de.njsm.stocks.server.v2.db.postgres.sslmode" to "disable",
        "de.njsm.stocks.server.v2.db.postgres.sslcert" to "dummy",
        "de.njsm.stocks.server.v2.db.postgres.sslkey" to "dummy",
        "de.njsm.stocks.server.v2.db.postgres.sslrootcert" to "dummy",
        "de.njsm.stocks.internal.ticketValidityTimeInMinutes" to "10",
        "de.njsm.stocks.server.v2.db.history.maxPeriod" to "P3Y",
    )
}

tasks.test {
    useJUnitPlatform()
    profile.forEach {
        systemProperty(it.key, it.value)
    }
    systemProperty("org.jooq.no-logo", "true")
    systemProperty("org.jooq.no-tips", "true")
}

sourceSets {
    main {
        java {
            srcDir(layout.buildDirectory.file("generated/jooq"))
        }
    }
}

liquibase {
    activities {
        register("main") {
            arguments = mapOf(
                "searchPath" to "$projectDir",
                "changeLogFile" to "src/main/resources/migrations/master.xml",
                "url" to "jdbc:postgresql://${profile.get("de.njsm.stocks.server.v2.db.host")}:${profile.get("de.njsm.stocks.server.v2.db.port")}/${profile.get("de.njsm.stocks.server.v2.db.name")}?user=${profile.get("de.njsm.stocks.server.v2.db.postgres.user")}&password=${profile.get("de.njsm.stocks.server.v2.db.postgres.password")}&ssl=${profile.get("de.njsm.stocks.server.v2.db.postgres.ssl")}&sslmode=${profile.get("de.njsm.stocks.server.v2.db.postgres.sslmode")}&sslcert=${profile.get("de.njsm.stocks.server.v2.db.postgres.sslcert")}&sslkey=${profile.get("de.njsm.stocks.server.v2.db.postgres.sslkey")}&sslrootcert=${profile.get("de.njsm.stocks.server.v2.db.postgres.sslrootcert")}&",
                "driver" to "org.postgresql.Driver",
            )
        }
    }
}

jooq {
    configuration {
        jdbc {
            driver = "org.postgresql.Driver"
            url = "jdbc:postgresql://${profile.get("de.njsm.stocks.server.v2.db.host")}:${profile.get("de.njsm.stocks.server.v2.db.port")}/${profile.get("de.njsm.stocks.server.v2.db.name")}"
            withProperties(listOf(
                Property().withKey("user").withValue("${profile.get("de.njsm.stocks.server.v2.db.postgres.user")}"),
                Property().withKey("password").withValue("${profile.get("de.njsm.stocks.server.v2.db.postgres.password")}"),
                Property().withKey("ssl").withValue("${profile.get("de.njsm.stocks.server.v2.db.postgres.ssl")}"),
                Property().withKey("sslmode").withValue("${profile.get("de.njsm.stocks.server.v2.db.postgres.sslmode")}"),
                Property().withKey("sslcert").withValue("${profile.get("de.njsm.stocks.server.v2.db.postgres.sslcert")}"),
                Property().withKey("sslkey").withValue("${profile.get("de.njsm.stocks.server.v2.db.postgres.sslkey")}"),
                Property().withKey("sslrootcert").withValue("${profile.get("de.njsm.stocks.server.v2.db.postgres.sslrootcert")}"),
            ))
        }
        generator {
            name = "org.jooq.codegen.JavaGenerator"
            database {
                name = "org.jooq.meta.postgres.PostgresDatabase"
                inputSchema = "public"
                includes = ".*"
                withProperties(listOf(
                    Property().withKey("dialect").withValue("POSTGRES")
                ))
                forcedTypes {
                    forcedType {
                        userType = "java.time.Duration"
                        converter = "de.njsm.stocks.server.v2.db.jooq.IntervalToDuration"
                        includeExpression = "public.recipe.duration"
                        includeTypes = "INTERVAL"
                        this.isAutoConverter
                    }
                    forcedType {
                        userType = "java.time.Period"
                        converter = "de.njsm.stocks.server.v2.db.jooq.IntervalToPeriod"
                        includeExpression = "public.food.expiration_offset"
                        includeTypes = "INTERVAL"
                    }
                    forcedType {
                        userType = "java.time.OffsetDateTime"
                        binding = "org.jooq.impl.OffsetDateTimeBinding"
                        includeExpression = "valid_time_start|valid_time_end|transaction_time_start|transaction_time_end"
                        includeTypes = ".*timestamp.*"
                    }
                }
            }
            target {
                packageName = "de.njsm.stocks.server.v2.db.jooq"
                directory = layout.buildDirectory.file("generated/jooq").get().asFile.absolutePath
            }
        }
    }
}

val resetDb = tasks.register("resetDb") {
    dependsOn(tasks.getByName("dropAll"))
    dependsOn(tasks.getByName("update"))
}
tasks.jooqCodegen.dependsOn(resetDb)
tasks.withType<JavaCompile>() {
    dependsOn(tasks.jooqCodegen)
}