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

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.exec
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.schedule
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

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

version = "2021.2"

project {
    buildType(Build)
    vcsRoot(HttpsGitlabComVeenjStocksGit)
}

object HttpsGitlabComVeenjStocksGit : GitVcsRoot({
    id("HttpsGitlabComVeenjStocksGit")
    name = "ssh://gitea@j.njsm.de:2222/veenj/stocks.git"
    url = "ssh://gitea@j.njsm.de:2222/veenj/stocks.git"
    branch = "refs/heads/master"
    branchSpec = "refs/heads/*"
    checkoutPolicy = GitVcsRoot.AgentCheckoutPolicy.USE_MIRRORS
    authMethod = uploadedKey {
        userName = "git"
        uploadedKey = "id_rsa"
    }
    param("oauthProviderId", "PROJECT_EXT_3")
    param("secure:password", "")
    param("tokenType", "undefined")
})

object Build : BuildType({
    name = "Full Build"

    artifactRules = """
        deploy-server/stocks-server-*-any.pkg.tar.zst

        server/build/server.log

        client-app-android/build/reports/**/* => client-app-android
        client-core/build/reports/**/* => client-core
        client-crypto/build/reports/**/* => client-crypto
        client-database-android/build/reports/**/* => client-database-android
        client-fakes-android/build/reports/**/* => client-fakes-android
        client-fakes/build/reports/**/* => client-fakes
        client-navigation-android/build/reports/**/* => client-navigation-android
        client-network/build/reports/**/* => client-network
        client-settings-android/build/reports/**/* => client-settings-android
        client-ui-android/build/reports/**/* => client-ui-android
    """.trimIndent()
    maxRunningBuilds = 2

    params {
        param("env.CI_SERVER", "1")
        param("env.ANDROID_SERIAL", "%env.ANDROID_DEVICE%")
    }

    vcs {
        root(HttpsGitlabComVeenjStocksGit)
    }

    steps {
        gradle {
            name = "Assemble artifacts"
            tasks = "-Pprofile=teamcity check test connectedCheck assemble -x :server-test:test"
            buildFile = "build.gradle.kts"
            gradleHome = "/usr/bin/gradle"
            gradleWrapperPath = "."
            enableStacktrace = true
        }
        exec {
            name = "Package server"
            workingDir = "deploy-server"
            path = "makepkg"
            arguments = "-cf"
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
        gradle {
            name = "Server System Test"
            tasks = ":server-test:test --rerun"
            buildFile = "build.gradle.kts"
            gradleHome = "/usr/bin/gradle"
            gradleWrapperPath = "."
            enableStacktrace = true
        }
        exec {
            name = "Server Log collection"
            executionMode = BuildStep.ExecutionMode.ALWAYS
            path = "server-test/bin/collect-log.sh"
        }
    }

    triggers {
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
        vcs {
            id = "vcsTrigger"
            branchFilter = "+:*"
        }
    }

    requirements {
        exists("env.POSTGRESQL_DB")
        exists("env.DEPLOYMENT_VM")
        exists("env.ANDROID_DEVICE")
    }

    failureConditions {
        executionTimeoutMin = 45
    }

    cleanup {
        artifacts(builds = 100)
    }

    features {
        commitStatusPublisher {
            vcsRootExtId = "${DslContext.settingsRoot.id}"
            publisher = github {
                githubUrl = "https://j.njsm.de/git/api/v1"
                authType = personalToken {
                    token = "credentialsJSON:557337ec-b35f-4879-a148-11d578a847a4"
                }
            }
        }
    }
})
