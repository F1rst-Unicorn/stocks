import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.exec
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2018_2.projectFeatures.youtrack
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.schedule
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs

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
        deploy-server/target/stocks-server-*-any.pkg.tar.xz
        deploy-client/target/stocks-*-any.pkg.tar.xz
        android-client/app/build/outputs/apk/app-release-unsigned.apk
        server/target/server.log
        client/target/client-client.log
        client/target/client-server.log
        android-client/app/build/android-app.log
        android-client/app/build/android-server.log
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
            coverageEngine = idea {
                includeClasses = "de.njsm.stocks.*"
                excludeClasses = "de.njsm.*.*Test"
            }
        }
        gradle {
            name = "Build android client"
            tasks = "build"
            buildFile = "android-client/build.gradle"
            gradleHome = "/usr/bin/gradle"
            gradleWrapperPath = "android-client"
            coverageEngine = idea {
                includeClasses = "de.njsm.*"
                excludeClasses = "*Test"
            }
        }
        gradle {
            name = "Test Android client local"
            tasks = "test"
            buildFile = "android-client/build.gradle"
            gradleWrapperPath = "android-client"
            enableStacktrace = true
            coverageEngine = idea {
                includeClasses = "de.njsm.*"
                excludeClasses = "*Test"
            }
            param("teamcity.coverage.jacoco.classpath", "+:*")
            param("teamcity.coverage.jacoco.patterns", "+:de.njsm.stocks.*")
        }
        exec {
            name = "Package server"
            path = "deploy-server/bin/package.sh"
        }
        exec {
            name = "Package client"
            path = "deploy-client/bin/package.sh"
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
            enabled = true
            schedulingPolicy = cron {
                minutes = "4,14,24,34,44,54"
                dayOfWeek = "*"
            }
            branchFilter = "+:STOCKS-63"
            triggerBuild = always()
            withPendingChangesOnly = false
            param("hour", "1")
            param("revisionRuleDependsOn", "Stocks_Build")
        }
    }

    failureConditions {
        executionTimeoutMin = 45
    }

    requirements {
        exists("env.ANDROID_HOME")
    }

    cleanup {
        artifacts(builds = 100)
    }
})
