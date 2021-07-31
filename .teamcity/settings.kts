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

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.exec
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2018_2.projectFeatures.youtrack
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.schedule
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2018_2.ui.add

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2018.2"

project {

    buildType(Build)

    features {
        youtrack {
            id = "PROJECT_EXT_1"
            displayName = "YouTrack"
            host = "https://j.njsm.de/youtrack"
            userName = "Jan_Veen"
            password = "credentialsJSON:41891c3f-2007-42a0-ac33-c6281d54301b"
            projectExtIds = "STOCKS"
        }
        feature {
            id = "PROJECT_EXT_2"
            type = "OAuthProvider"
            param("clientId", "133b8d6e9faa4d280817")
            param("secure:clientSecret", "credentialsJSON:90a997d8-4176-441e-8112-3ddee1038ca8")
            param("displayName", "GitHub.com")
            param("gitHubUrl", "https://github.com/")
            param("providerType", "GitHub")
        }
    }
}

object Build : BuildType({
    name = "Full Build"

    artifactRules = """
        deploy-server/stocks-server-*-any.pkg.tar.zst
        deploy-client/stocks-*-any.pkg.tar.zst
        android-client/app/build/outputs/apk/app-release-unsigned.apk
        android-client/app/build/outputs/apk/debug/app-debug.apk
        android-client/app/build/outputs/apk/release/app-release-unsigned.apk
        server/target/server.log
        client/target/client-client.log
        client/target/client-server.log
        android-client/app/build/android-app.log
        android-client/app/build/android-server.log
        android-client/app/build/reports/androidTests/connected/flavors/debugAndroidTest/**/* => android-test-report
    """.trimIndent()
    maxRunningBuilds = 2

    params {
        param("env.CI_SERVER", "1")
        param("env.NO_SIGNATURE", "1")
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            name = "Compile & Unit Test"
            goals = "clean install"
            runnerArgs = "-P teamcity"
            mavenVersion = auto()
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            coverageEngine = idea {
                includeClasses = "de.njsm.stocks.*"
                excludeClasses = """
                    de.njsm.stocks.client.storage.jooq.tables.*
                    de.njsm.stocks.server.v2.db.jooq.tables.*
                    de.njsm.stocks.server.v2.db.jooq.Sequences
                    de.njsm.stocks.server.v2.db.jooq.Keys
                    de.njsm.stocks.server.v2.db.jooq.Public
                    de.njsm.stocks.server.v2.db.jooq.Tables
                    de.njsm.stocks.server.v2.db.jooq.DefaultCatalog
                    de.njsm.stocks.server.v2.db.jooq.Indexes
                    de.njsm.stocks.server.v2.business.data.AutoValue_*

                    de.njsm.stocks.common.api.impl.AutoValue_*

                    de.njsm.*.*Test
                    de.njsm.stocks.server.v2.matchers.*
                """.trimIndent()
            }
        }
        gradle {
            name = "Compile & Unit Test Android Client"
            tasks = "test"
            buildFile = "android-client/build.gradle"
            gradleHome = "/usr/bin/gradle"
            gradleWrapperPath = "android-client"
            enableStacktrace = true
            coverageEngine = idea {
                includeClasses = "de.njsm.*"
                excludeClasses = """
                    *Test
                    de.njsm.stocks.BuildConfig
                    de.njsm.stocks.NavigationGraphDirections
                    de.njsm.stocks.android.Application_MembersInjector
                    de.njsm.stocks.android.dagger.DaggerRootComponent
                    de.njsm.stocks.android.*.*_*Factory
                    de.njsm.stocks.android.*.*_MembersInjector
                    de.njsm.stocks.android.*.*_Impl
                    de.njsm.stocks.android.*.*_Contribute*
                """.trimIndent()
            }
        }
        gradle {
            name = "Android Client Instrumented Test"
            tasks = "connectedDebugAndroidTest"
            buildFile = "android-client/build.gradle"
            gradleHome = "/usr/bin/gradle"
            gradleWrapperPath = "android-client"
            enableStacktrace = true
            // https://developer.android.com/reference/android/support/test/runner/AndroidJUnitRunner.html
            gradleParams = """
                -Pandroid.testInstrumentationRunnerArguments.notPackage=de.njsm.stocks.android.test.system
            """.trimIndent()
        }
        exec {
            name = "Package server"
            workingDir = "deploy-server"
            path = "makepkg"
            arguments = "-cf"
        }
        exec {
            name = "Package client"
            workingDir = "deploy-client"
            path = "makepkg"
            arguments = "-cf"
        }
        gradle {
            name = "Package Android App"
            tasks = "assemble"
            buildFile = "android-client/build.gradle"
            gradleWrapperPath = "android-client"
            coverageEngine = idea {
                includeClasses = "de.njsm.*"
                excludeClasses = "*Test"
            }
        }
        exec {
            name = "Clean server"
            executionMode = BuildStep.ExecutionMode.ALWAYS
            path = "server-test/bin/clean-up.sh"
        }
        exec {
            name = "Server Installation"
            path = "server-test/bin/vm-deployment-test.sh"
        }
        maven {
            name = "Server System Test"
            goals = "test"
            pomLocation = "server-test/pom.xml"
            runnerArgs = "-Dtest=TestSuite"
            mavenVersion = auto()
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
        }
        exec {
            name = "Server Log collection"
            executionMode = BuildStep.ExecutionMode.ALWAYS
            path = "server-test/bin/collect-log.sh"
        }
        exec {
            name = "Clean client"
            executionMode = BuildStep.ExecutionMode.ALWAYS
            path = "client/src/test/system/bin/clean-up.sh"
        }
        exec {
            name = "Client Deployment test"
            path = "client/src/test/system/bin/vm-deployment-test.sh"
        }
        exec {
            name = "Android Deployment test"
            path = "android-client/app/src/test/system/bin/vm-deployment-test.sh"
        }
    }

    triggers {
        vcs {
        }
        schedule {
            schedulingPolicy = cron {
                minutes = "4,14,24,34,44,54"
                dayOfWeek = "*"
            }
            branchFilter = "+:STOCKS-63"
            triggerBuild = always()
            withPendingChangesOnly = false
            enabled = false
            param("hour", "1")
            param("revisionRuleDependsOn", "Stocks_Build")
        }
    }

    requirements {
        add {
            exists("env.POSTGRESQL_DB")
        }
        add {
            exists("env.DEPLOYMENT_VM")
        }
        add {
            exists("env.ANDROID_DEVICE")
        }
    }

    failureConditions {
        executionTimeoutMin = 45
    }

    cleanup {
        artifacts(builds = 100)
    }
})
