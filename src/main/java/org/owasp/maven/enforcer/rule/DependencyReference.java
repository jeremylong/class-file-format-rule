/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.owasp.maven.enforcer.rule;

import java.io.File;
import java.util.List;
import org.apache.maven.artifact.versioning.ArtifactVersion;

/**
 * The information about a dependency object needed to perform the class file
 * format validation.
 *
 * @author Jeremy Long
 */
public class DependencyReference {

    /**
     * The group id of the dependency.
     */
    private String groupId;

    /**
     * The artifact id of the dependency.
     */
    private String artifactId;
    /**
     * The version of the dependency.
     */
    private String version;

    /**
     * The path to the dependency on disk.
     */
    private File path;

    /**
     * The list of available versions.
     */
    private List<ArtifactVersion> availableVersions;

    /**
     * The trail.
     */
    private List<String> dependencyTrail;

    /**
     * Constructs a new dependency reference.
     *
     * @param groupId the group id
     * @param artifactId the artifact id
     * @param version the version
     * @param path the path to the dependency on disk
     * @param availableVersions the available versions for the dependency
     * @param dependencyTrail the trail to the dependency
     */
    public DependencyReference(final String groupId, final String artifactId,
            final String version, final File path, final List<ArtifactVersion> availableVersions,
            final List<String> dependencyTrail) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.path = path;
        this.availableVersions = availableVersions;
        this.dependencyTrail = dependencyTrail;
    }

    /**
     * Get the value of path.
     *
     * @return the value of path
     */
    public File getPath() {
        return path;
    }

    /**
     * Set the value of path.
     *
     * @param path new value of path
     */
    public void setPath(final File path) {
        this.path = path;
    }

    /**
     * Get the value of availableVersions.
     *
     * @return the value of availableVersions
     */
    public List<ArtifactVersion> getAvailableVersions() {
        return availableVersions;
    }

    /**
     * Set the value of availableVersions.
     *
     * @param availableVersions new value of availableVersions
     */
    public void setAvailableVersions(final List<ArtifactVersion> availableVersions) {
        this.availableVersions = availableVersions;
    }

    /**
     * Get the value of version.
     *
     * @return the value of version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Set the value of version.
     *
     * @param version new value of version
     */
    public void setVersion(final String version) {
        this.version = version;
    }

    /**
     * Get the value of artifactId.
     *
     * @return the value of artifactId
     */
    public String getArtifactId() {
        return artifactId;
    }

    /**
     * Set the value of artifactId.
     *
     * @param artifactId new value of artifactId
     */
    public void setArtifactId(final String artifactId) {
        this.artifactId = artifactId;
    }

    /**
     * Get the value of groupId.
     *
     * @return the value of groupId
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Set the value of groupId.
     *
     * @param groupId new value of groupId
     */
    public void setGroupId(final String groupId) {
        this.groupId = groupId;
    }

    /**
     * Get the value of dependencyTrail.
     *
     * @return the value of dependencyTrail
     */
    public List<String> getDependencyTrail() {
        return dependencyTrail;
    }

    /**
     * Set the value of dependencyTrail.
     *
     * @param dependencyTrail new value of dependencyTrail
     */
    public void setDependencyTrail(final List<String> dependencyTrail) {
        this.dependencyTrail = dependencyTrail;
    }

    /**
     * Standard to string implementation.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return String.format("%s:%s:%s - %s", groupId,
                artifactId, version, path);
    }
}
