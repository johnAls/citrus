/*
 * Copyright 2006-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.generate.javadsl;

import com.consol.citrus.generate.UnitFramework;
import com.squareup.javapoet.CodeBlock;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Christoph Deppisch
 * @since 2.7.4
 */
public class JavaDslTestGenerator<T extends JavaDslTestGenerator> extends JavaTestGenerator<T> {

    @Override
    protected Properties getTemplateProperties() {
        Properties properties = super.getTemplateProperties();

        if (getFramework().equals(UnitFramework.TESTNG)) {
            properties.put(TEST_BASE_CLASS_IMPORT, "com.consol.citrus.dsl.testng.TestNGCitrusTestRunner");
            properties.put(TEST_BASE_CLASS, "TestNGCitrusTestRunner");
        } else if (getFramework().equals(UnitFramework.JUNIT4)) {
            properties.put(TEST_BASE_CLASS_IMPORT, "com.consol.citrus.dsl.junit.JUnit4CitrusTestRunner");
            properties.put(TEST_BASE_CLASS, "JUnit4CitrusTestRunner");
        } else if (getFramework().equals(UnitFramework.JUNIT5)) {
            properties.put(TEST_BASE_CLASS_IMPORT, "com.consol.citrus.dsl.junit.jupiter.CitrusExtension");
            properties.put(TEST_BASE_CLASS, "CitrusExtension");
        }

        properties.put(TEST_METHOD_BODY, getCodeBlocks().stream()
                                                .map(codeBlock -> Pattern.compile("^", Pattern.MULTILINE).matcher(codeBlock.toString()).replaceAll("        "))
                                                .collect(Collectors.joining("\n\n")));

        return properties;
    }

    @Override
    protected String getTemplateFilePath() {
        if (getFramework().equals(UnitFramework.JUNIT5)) {
            return "classpath:com/consol/citrus/generate/java-dsl-junit5-test-template.txt";
        } else {
            return "classpath:com/consol/citrus/generate/java-dsl-test-template.txt";
        }
    }

    /**
     * List of test actions to be marshalled in the actions section of the test.
     * @return
     */
    protected List<CodeBlock> getCodeBlocks() {
        List<CodeBlock> codeBlocks = new ArrayList<>();
        codeBlocks.add(CodeBlock.builder().add("echo(\"TODO: Code the test $L\");", getName()).build());
        return codeBlocks;
    }
}
