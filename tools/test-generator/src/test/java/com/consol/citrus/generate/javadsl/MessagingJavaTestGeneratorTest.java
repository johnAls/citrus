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

import com.consol.citrus.Citrus;
import com.consol.citrus.generate.UnitFramework;
import com.consol.citrus.message.DefaultMessage;
import com.consol.citrus.util.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author Christoph Deppisch
 */
public class MessagingJavaTestGeneratorTest {

    @Test
    public void testCreateTest() throws IOException {
        MessagingJavaTestGenerator generator = new MessagingJavaTestGenerator();

        generator.withAuthor("Christoph")
                 .withDescription("This is a sample test")
                 .withName("SampleReqResIT")
                 .usePackage("com.consol.citrus")
                 .withFramework(UnitFramework.TESTNG);

        generator.withRequest(new DefaultMessage("<TestRequest><Message>Citrus rocks!</Message></TestRequest>"));
        generator.withResponse(new DefaultMessage("<TestResponse><Message>Hell Ya!</Message></TestResponse>"));

        generator.create();
        
        File javaFile = new File(Citrus.DEFAULT_TEST_SRC_DIRECTORY + "java/com/consol/citrus/SampleReqResIT.java");
        Assert.assertTrue(javaFile.exists());
        
        String javaContent = FileUtils.readToString(new FileSystemResource(javaFile));
        Assert.assertTrue(javaContent.contains("@author Christoph"));
        Assert.assertTrue(javaContent.contains("public class SampleReqResIT"));
        Assert.assertTrue(javaContent.contains("* This is a sample test"));
        Assert.assertTrue(javaContent.contains("package com.consol.citrus;"));
        Assert.assertTrue(javaContent.contains("extends TestNGCitrusTestRunner"));
    }
}
