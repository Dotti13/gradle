/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.smalltalk

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.integtests.fixtures.AbstractIntegrationSpec

import javax.inject.Inject

class SmalltalkIntegrationTest extends AbstractIntegrationSpec {

    def setup() {
        // Required, because the Gradle API jar is computed once a day,
        // and the new API might not be visible for tests that require compilation
        // against that API, e.g. the cases like a plugin defined in an included build
        executer.requireOwnGradleUserHomeDir()
    }

    def "can consume build-provided model from setting plugin in a project plugin"() {
        buildFile file("build-logic/build.gradle"), """
            plugins {
                id 'groovy-gradle-plugin'
            }

            gradlePlugin {
                plugins {
                    mySettingsPlugin {
                        id = 'my-settings-plugin'
                        implementationClass = 'my.MySettingsPlugin'
                    }
                    myProjectPlugin {
                        id = 'my-project-plugin'
                        implementationClass = 'my.MyProjectPlugin'
                    }
                }
            }
        """

        groovyFile "build-logic/src/main/groovy/my/MyModel.groovy", """
            class MyModel {
                String value
                MyModel(value) { this.value = value }
            }
        """

        groovyFile "build-logic/src/main/groovy/my/MySettingsPlugin.groovy", """
            package my
            import ${Plugin.name}
            import ${Settings.name}
            import ${SmalltalkBuildModelRegistry.name}
            import ${Inject.name}

            abstract class MySettingsPlugin implements Plugin<Settings> {
                @Inject
                abstract ${SmalltalkBuildModelRegistry.simpleName} getRegistry()

                void apply(Settings s) {
                    registry.registerModel("myValue", MyModel) {
                        println("Computing myValue")
                        return new MyModel("hey")
                    }
                }
            }
        """

        groovyFile "build-logic/src/main/groovy/my/MyProjectPlugin.groovy", """
            package my
            import ${Plugin.name}
            import ${Project.name}
            import ${SmalltalkBuildModelLookup.name}
            import ${Inject.name}

            abstract class MyProjectPlugin implements Plugin<Project> {
                @Inject
                abstract ${SmalltalkBuildModelLookup.name} getRegistry()

                void apply(Project project) {
                    def valueProvider = registry.getModel("myValue", MyModel)
                    MyModel computedValue = valueProvider.get()
                    println("Project '" + project.buildTreePath + "' got value '" + computedValue.value + "'")
                }
            }
        """

        settingsFile """
            pluginManagement {
                includeBuild("build-logic")
            }
            plugins {
                id("my-settings-plugin")
            }
        """

        buildFile """
            plugins {
                id("my-project-plugin")
            }

            tasks.register("something")
        """

        when:
        run "something"

        then:
        outputContains("Computing myValue")
        outputContains("Project ':' got value 'hey'")
    }

    def "can consume build-provided model of shared type from setting script in a build script"() {
        settingsFile """
            settings.buildModels.registerModel("myValue", String) {
                println("Computing myValue")
                "hey"
            }
        """

        buildFile """
            def modelProvider = project.buildModels.getModel("myValue", String)
            def computedValue = modelProvider.get()
            println("Project '" + project.buildTreePath + "' got value '" + computedValue + "'")

            tasks.register("something")
        """

        when:
        run "something"

        then:
        outputContains("Computing myValue")
        outputContains("Project ':' got value 'hey'")
    }

}
