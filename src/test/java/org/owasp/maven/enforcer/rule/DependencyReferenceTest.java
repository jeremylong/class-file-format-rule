/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.owasp.maven.enforcer.rule;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jeremy
 */
public class DependencyReferenceTest {

    private DependencyReference buildReference() {
        File f = new File(".");
        List<ArtifactVersion> av = new ArrayList<>();
        List<String> dt = new ArrayList<>();
        return new DependencyReference("groupId", "artifactId", "version", f, av, dt);
    }

    /**
     * Test of getPath method, of class DependencyReference.
     */
    @Test
    public void testGetPath() {
        DependencyReference instance = buildReference();
        File expResult = new File(".");
        File result = instance.getPath();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPath method, of class DependencyReference.
     */
    @Test
    public void testSetPath() {
        File path = null;
        DependencyReference instance = buildReference();
        instance.setPath(path);
        assertNull(instance.getPath());
    }

    /**
     * Test of getAvailableVersions method, of class DependencyReference.
     */
    @Test
    public void testGetAvailableVersions() {
        DependencyReference instance = buildReference();;
        List<ArtifactVersion> result = instance.getAvailableVersions();
        assertTrue(result.isEmpty());
    }

    /**
     * Test of setAvailableVersions method, of class DependencyReference.
     */
    @Test
    public void testSetAvailableVersions() {
        List<ArtifactVersion> availableVersions = null;
        DependencyReference instance = buildReference();
        instance.setAvailableVersions(availableVersions);
        assertNull(instance.getAvailableVersions());
    }

    /**
     * Test of getVersion method, of class DependencyReference.
     */
    @Test
    public void testGetVersion() {
        DependencyReference instance = buildReference();
        String expResult = "version";
        String result = instance.getVersion();
        assertEquals(expResult, result);
    }

    /**
     * Test of setVersion method, of class DependencyReference.
     */
    @Test
    public void testSetVersion() {
        String version = null;
        DependencyReference instance = buildReference();
        instance.setVersion(version);
        assertNull(instance.getVersion());
    }

    /**
     * Test of getArtifactId method, of class DependencyReference.
     */
    @Test
    public void testGetArtifactId() {
        DependencyReference instance = buildReference();
        String expResult = "artifactId";
        String result = instance.getArtifactId();
        assertEquals(expResult, result);
    }

    /**
     * Test of setArtifactId method, of class DependencyReference.
     */
    @Test
    public void testSetArtifactId() {
        String artifactId = null;
        DependencyReference instance = buildReference();
        instance.setArtifactId(artifactId);
        assertNull(instance.getArtifactId());
    }

    /**
     * Test of getGroupId method, of class DependencyReference.
     */
    @Test
    public void testGetGroupId() {
        DependencyReference instance = buildReference();
        String expResult = "groupId";
        String result = instance.getGroupId();
        assertEquals(expResult, result);
    }

    /**
     * Test of setGroupId method, of class DependencyReference.
     */
    @Test
    public void testSetGroupId() {
        String groupId = null;
        DependencyReference instance = buildReference();
        instance.setGroupId(groupId);
        assertNull(instance.getGroupId());
    }

    /**
     * Test of getDependencyTrail method, of class DependencyReference.
     */
    @Test
    public void testGetDependencyTrail() {
        DependencyReference instance = buildReference();
        List<String> result = instance.getDependencyTrail();
        assertTrue(result.isEmpty());
    }

    /**
     * Test of setDependencyTrail method, of class DependencyReference.
     */
    @Test
    public void testSetDependencyTrail() {
        List<String> dependencyTrail = null;
        DependencyReference instance = buildReference();
        instance.setDependencyTrail(dependencyTrail);
        assertNull(instance.getDependencyTrail());
    }

    /**
     * Test of toString method, of class DependencyReference.
     */
    @Test
    public void testToString() {
        DependencyReference instance = buildReference();
        String expResult = "groupId:artifactId:version - .";
        String result = instance.toString();
        assertEquals(expResult, result);
    }

}
