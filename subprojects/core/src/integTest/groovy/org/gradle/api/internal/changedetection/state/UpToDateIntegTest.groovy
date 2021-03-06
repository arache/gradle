/*
 * Copyright 2016 the original author or authors.
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

package org.gradle.api.internal.changedetection.state

import org.gradle.integtests.fixtures.AbstractIntegrationSpec
import spock.lang.Issue

class UpToDateIntegTest extends AbstractIntegrationSpec {


    def "empty output directories created automatically are part of up-to-date checking"() {
        given:
        buildFile << '''
apply plugin: 'base'

task checkCreated {
    dependsOn "createEmpty"
    doLast {
        assert file('build/createdDirectory').exists()
        println "Directory 'build/createdDirectory' exists"
    }
}

task("createEmpty", type: CreateEmptyDirectory)

public class CreateEmptyDirectory extends DefaultTask {
    @TaskAction
    public void createDir() {
        println "did nothing: output dir is created automatically"
    }

    @OutputDirectory
    public File getDirectory() {
        return new File(getProject().getBuildDir(), "createdDirectory")
    }
}
'''
        when:
        succeeds("clean", "checkCreated")
        succeeds("clean", "checkCreated")
        then:
        noExceptionThrown()
    }

    @Issue("https://issues.gradle.org/browse/GRADLE-834")
    def "task without actions is reported as up-to-date when it's up-to-date"() {
        file("src/main/java/Main.java") << "public class Main {}"
        buildFile << """
            apply plugin: "java"
        """

        expect:
        succeeds "jar"

        when:
        succeeds "jar"
        then:
        outputContains ":classes UP-TO-DATE"
    }
}
