/*
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
 *
 * Copyright (c) 2017 Jeremy Long. All Rights Reserved.
 */
package org.owasp.maven.enforcer.rule;

import java.io.File;
import java.net.URISyntaxException;
import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Assume;

/**
 *
 * @author Jeremy Long
 */
public class ClassFileFormatRuleTest {


    /**
     * Test of execute method, of class BytecodeLevelRule.
     */
    @Test
    public void testExecute() throws Exception {
//        EnforcerRuleHelper helper = null;
//        BytecodeLevelRule instance = new BytecodeLevelRule();
//        instance.execute(helper);
    }

    /**
     * Test of hasInvalidByteCodeLevel method, of class BytecodeLevelRule.
     */
    @Test
    public void testHasInvalidByteCodeLevel() {
        File f = getResourceAsFile(this, "junit-4.13.1.jar");
        DependencyReference dependency = new  DependencyReference("junit", "junit", "4.13.1", f, null, null);
        ClassFileFormatRule instance = new ClassFileFormatRule();
        boolean expResult = false;
        boolean result = instance.hasInvalidClassFileFormat(dependency);
        assertEquals(expResult, result);
        
        instance.setSupportedClassFileFormat(ClassFileFormatRule.JDK_1_1);
        expResult = true;
        result = instance.hasInvalidClassFileFormat(dependency);
        assertEquals(expResult, result);
    }
    /**
     * Test of hasInvalidByteCodeLevel method, of class BytecodeLevelRule.
     */
    @Test
    public void testHasMetaInfVersions() {
        File f = getResourceAsFile(this, "istack-commons-runtime-3.0.10.jar");
        DependencyReference dependency = new  DependencyReference("com.sun.istack", "istack-commons-runtime", "3.0.10", f, null, null);
        ClassFileFormatRule instance = new ClassFileFormatRule();
        instance.setSupportedClassFileFormat(ClassFileFormatRule.JAVA_8);
        boolean expResult = false;
        boolean result = instance.hasInvalidClassFileFormat(dependency);
        assertEquals(expResult, result);
    }
    /**
     * Test of isCacheable method, of class BytecodeLevelRule.
     */
    @Test
    public void testIsCacheable() {
        ClassFileFormatRule instance = new ClassFileFormatRule();
        boolean expResult = false;
        boolean result = instance.isCacheable();
        assertEquals(expResult, result);
    }

    /**
     * Test of isResultValid method, of class BytecodeLevelRule.
     */
    @Test
    public void testIsResultValid() {
        EnforcerRule cachedRule = null;
        ClassFileFormatRule instance = new ClassFileFormatRule();
        boolean expResult = false;
        boolean result = instance.isResultValid(cachedRule);
        assertEquals(expResult, result);
    }

    /**
     * Test of getCacheId method, of class BytecodeLevelRule.
     */
    @Test
    public void testGetCacheId() {
        ClassFileFormatRule instance = new ClassFileFormatRule();
        String result = instance.getCacheId();
        assertNull(result);
    }
    
    
        /**
     * Returns the given resource as a File using the object's class loader. The
     * org.junit.Assume API is used so that test cases are skipped if the
     * resource is not available.
     *
     * @param o the object used to obtain a reference to the class loader
     * @param resource the name of the resource to load
     * @return the resource as an File
     */
    private static File getResourceAsFile(Object o, String resource) {
        try {
            File f = new File(o.getClass().getClassLoader().getResource(resource).toURI().getPath());
            Assume.assumeTrue(String.format("%n%n[SEVERE] Unable to load resource for test case: %s%n%n", resource), f.exists());
            return f;
        } catch (URISyntaxException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
