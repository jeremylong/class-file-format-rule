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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.artifact.ArtifactCoordinate;
import org.apache.maven.shared.artifact.TransferUtils;
import org.apache.maven.shared.artifact.resolve.ArtifactResolver;
import org.apache.maven.shared.artifact.resolve.ArtifactResolverException;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/**
 * A maven enforcer rule to validate that the dependencies of a project use a
 * specific class file format or lower. This ensures that your dependencies will
 * work on a specific JVM.
 *
 * @author Jeremy Long
 */
public class ClassFileFormatRule implements EnforcerRule {

    /**
     * Class file format for Java 9.
     */
    public static final int JAVA_9 = 53; //(0x35 hex)
    /**
     * Class file format for Java 8.
     */
    public static final int JAVA_8 = 52; //(0x34 hex)
    /**
     * Class file format for Java 7.
     */
    public static final int JAVA_7 = 51; //(0x33 hex)
    /**
     * Class file format for Java 6.
     */
    public static final int JAVA_6 = 50; //(0x32 hex)
    /**
     * Class file format for Java 5.
     */
    public static final int JAVA_5 = 49; //(0x31 hex)
    /**
     * Class file format for Java 4.
     */
    public static final int JDK_1_4 = 48; //(0x30 hex)
    /**
     * Class file format for Java 3.
     */
    public static final int JDK_1_3 = 47; //(0x2F hex)
    /**
     * Class file format for Java 2.
     */
    public static final int JDK_1_2 = 46; //(0x2E hex)
    /**
     * Class file format for Java 1.
     */
    public static final int JDK_1_1 = 45; //(0x2D hex)
    /**
     * The java class file header.
     */
    private static final int JAVA_CLASS_HEADER = 0xCAFEBABE;
    /**
     * The supported class file format; defaults to Java 7.
     */
    private int supportedClassFileFormat = JAVA_7;
    /**
     * Whether or not to exclude the test scope.
     */
    private boolean excludeScopeTest = true;
    /**
     * Whether or not to exclude the provided scope.
     */
    private boolean excludeScopeProvided = true;
    /**
     * The logger.
     */
    private Log log;

    /**
     * Executes the class file format rule. Examines the class files contained
     * in the projects dependency tree.
     *
     * @param helper the enforcer rule helper
     * @throws EnforcerRuleException thrown if a class file is identified that
     * has a newer format then expected
     */
    @Override
    public void execute(final EnforcerRuleHelper helper) throws EnforcerRuleException {
        log = helper.getLog();
        try {
            MavenProject project = (MavenProject) helper.evaluate("${project}");
            List<MavenProject> reactorProjects = (List<MavenProject>) helper.evaluate("${reactorProjects}");
            MavenSession session = (MavenSession) helper.evaluate("${session}");
            List<ArtifactRepository> remoteRepositories = (List<ArtifactRepository>) helper.evaluate("${project.remoteArtifactRepositories}");
            ArtifactResolver artifactResolver = (ArtifactResolver) helper.getComponent(ArtifactResolver.class);
            DependencyGraphBuilder dependencyGraphBuilder = (DependencyGraphBuilder) helper.getComponent(DependencyGraphBuilder.class);

            Set<DependencyReference> dependencies = getProjectDependencies(project, session, dependencyGraphBuilder, reactorProjects, remoteRepositories, artifactResolver);
            boolean failBuild = false;
            StringBuilder sb = new StringBuilder();

            for (DependencyReference d : dependencies) {
                final boolean result = hasInvalidClassFileFormat(d);
                failBuild |= result;
                if (result) {
                    sb.append(String.format("%n%s:%s:%s", d.getGroupId(), d.getArtifactId(), d.getVersion()));
                    if (d.getDependencyTrail() != null && !d.getDependencyTrail().isEmpty()) {
                        if (d.getDependencyTrail().size() == 1) {
                            sb.append(String.format("%n - project path: %s", d.getDependencyTrail().get(0)));
                        } else {
                            sb.append(String.format("%n - project paths:"));
                            for (int x = 0; x < d.getDependencyTrail().size(); x++) {
                                sb.append(String.format(" %s,", d.getDependencyTrail().get(x)));
                            }
                            sb.setLength(sb.length() - 1);
                        }
                    }
//                  if (d.getAvailableVersions()!=null && !d.getAvailableVersions().isEmpty()) {
//                      List<ArtifactVersion> versions = d.getAvailableVersions();
//                      Collections.sort(versions);
//                      //TODO only display the max top 5?  some deps have huge lists - go look it up...
//                  }
                }
            }

            if (failBuild) {
                sb.insert(0, "The following dependencies exceed the maximum supported JVM class file format (i.e. they were compiled for a newer JVM then this project supports):");
                throw new EnforcerRuleException(sb.toString());
            }

        } catch (ExpressionEvaluationException e) {
            throw new EnforcerRuleException("Unable to lookup an expression " + e.getLocalizedMessage(), e);
        } catch (ComponentLookupException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Determines if the class file format. The class files are inspected to
     * validate the class file format version.
     *
     * @param dependency the dependency to test
     * @return true if the class file format is greater then expected
     */
    protected boolean hasInvalidClassFileFormat(final DependencyReference dependency) {

        try (FileInputStream fis = new FileInputStream(dependency.getPath());
                BufferedInputStream bis = new BufferedInputStream(fis);
                JarInputStream jarInput = new JarInputStream(bis);
                DataInputStream in = new DataInputStream(jarInput)) {

            JarEntry entry = jarInput.getNextJarEntry();
            while (entry != null) {
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    int magic = in.readInt();
                    if (magic != JAVA_CLASS_HEADER) {
                        log.debug(String.format("%s contains an invalid class", dependency.toString()));
                    } else {
                        int minor = in.readUnsignedShort();
                        int major = in.readUnsignedShort();
                        if (major > supportedClassFileFormat) {
                            return true;
                        }
                    }
                }
                entry = jarInput.getNextJarEntry();
            }

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return false;
    }

    /**
     * Scans the project's artifacts and adds them to the engine's dependency
     * list.
     *
     * @param project the project to scan the dependencies
     * @param session the maven session
     * @param dependencyGraphBuilder the maven dependency graph builder
     * @param reactorProjects the list of maven projects
     * @param remoteRepositories the list of remote repositories
     * @param artifactResolver the maven artifact resolver
     * @return a collection of exceptions that may have occurred while resolving
     * and scanning the dependencies
     * @throws EnforcerRuleException thrown if there is an exception resolving the dependency tree
     */
    private Set<DependencyReference> getProjectDependencies(final MavenProject project, final MavenSession session,
            final DependencyGraphBuilder dependencyGraphBuilder, final List<MavenProject> reactorProjects,
            final List<ArtifactRepository> remoteRepositories, final ArtifactResolver artifactResolver)
            throws EnforcerRuleException {
        final Set<DependencyReference> references = new HashSet<>();
        try {
            final DependencyNode dn = dependencyGraphBuilder.buildDependencyGraph(project, null, reactorProjects);
            final ProjectBuildingRequest buildingRequest = newResolveArtifactProjectBuildingRequest(session, remoteRepositories);
            if (collectDependencies(references, project, dn.getChildren(), buildingRequest, artifactResolver)) {
                throw new EnforcerRuleException("Unable to resolve the projects dependencies");
            }
        } catch (DependencyGraphBuilderException ex) {
            final String msg = String.format("Unable to build dependency graph on project %s", project.getName());
            throw new EnforcerRuleException(msg, ex);
        }
        return references;
    }

    /**
     * Resolves the projects artifacts using Aether and scans the resulting
     * dependencies.
     *
     * @param references the set to which dependencies will be added
     * @param project the project being scanned
     * @param nodes the list of dependency nodes, generally obtained via the
     * DependencyGraphBuilder
     * @param buildingRequest the Maven project building request
     * @param artifactResolver the maven artifact resolver
     * @return true if the collection of dependencies failed
     */
    private boolean collectDependencies(final Set<DependencyReference> references,
            final MavenProject project, final List<DependencyNode> nodes,
            final ProjectBuildingRequest buildingRequest, final ArtifactResolver artifactResolver) {
        boolean collectionFailed = false;
        for (DependencyNode dependencyNode : nodes) {
            if ((excludeScopeTest
                    && org.apache.maven.artifact.Artifact.SCOPE_TEST.equals(dependencyNode.getArtifact().getScope()))
                    || (excludeScopeProvided
                    && org.apache.maven.artifact.Artifact.SCOPE_PROVIDED.equals(dependencyNode.getArtifact().getScope()))) {
                continue;
            }
            collectionFailed |= collectDependencies(references, project, dependencyNode.getChildren(), buildingRequest, artifactResolver);

            boolean isResolved = false;
            File artifactFile = null;
            String artifactId = null;
            String groupId = null;
            String version = null;
            List<ArtifactVersion> availableVersions = null;
            if (org.apache.maven.artifact.Artifact.SCOPE_SYSTEM.equals(dependencyNode.getArtifact().getScope())) {
                List<Dependency> dependencies = (List<Dependency>) project.getDependencies();
                for (Dependency d : dependencies) {
                    final Artifact a = dependencyNode.getArtifact();
                    if (d.getSystemPath() != null && artifactsMatch(d, a)) {
                        artifactFile = new File(d.getSystemPath());
                        isResolved = artifactFile.isFile();
                        groupId = a.getGroupId();
                        artifactId = a.getArtifactId();
                        version = a.getVersion();
                        availableVersions = a.getAvailableVersions();
                        break;
                    }
                }
                if (!isResolved) {
                    log.error("Unable to resolve system scoped dependency: " + dependencyNode.toNodeString());
                    collectionFailed = true;
                    continue;
                }
            } else {
                final ArtifactCoordinate coordinate = TransferUtils.toArtifactCoordinate(dependencyNode.getArtifact());
                final Artifact result;
                try {
                    result = artifactResolver.resolveArtifact(buildingRequest, coordinate).getArtifact();
                } catch (ArtifactResolverException ex) {
                    log.debug("Collection failed", ex);
                    final String msg = String.format("Error resolving '%s' in project %s",
                            dependencyNode.getArtifact().getId(), project.getName());
                    log.error(msg);
                    collectionFailed = true;
                    continue;
                }
                isResolved = result.isResolved();
                artifactFile = result.getFile();
                groupId = result.getGroupId();
                artifactId = result.getArtifactId();
                version = result.getVersion();
                availableVersions = result.getAvailableVersions();
            }
            if (isResolved && artifactFile != null && artifactFile.isFile()) {
                DependencyReference dep = new DependencyReference(groupId, artifactId, version, artifactFile,
                        availableVersions, dependencyNode.getArtifact().getDependencyTrail());
                references.add(dep);
            } else {
                final String msg = String.format("Unable to resolve '%s' in project %s",
                        dependencyNode.getArtifact().getId(), project.getName());
                log.error(msg);
                collectionFailed = true;
            }

        }
        return collectionFailed;
    }

    /**
     * Determines if the groupId, artifactId, and version of the Maven
     * dependency and artifact match.
     *
     * @param d the Maven dependency
     * @param a the Maven artifact
     * @return true if the groupId, artifactId, and version match
     */
    private static boolean artifactsMatch(final Dependency d, final Artifact a) {
        return (isEqualOrNull(a.getArtifactId(), d.getArtifactId()))
                && (isEqualOrNull(a.getGroupId(), d.getGroupId()))
                && (isEqualOrNull(a.getVersion(), d.getVersion()));
    }

    /**
     * Compares two strings for equality; if both strings are null they are
     * considered equal.
     *
     * @param left the first string to compare
     * @param right the second string to compare
     * @return true if the strings are equal or if they are both null; otherwise
     * false.
     */
    private static boolean isEqualOrNull(final String left, final String right) {
        return (left != null && left.equals(right)) || (left == null && right == null);
    }

    /**
     * Builds a Project Building Request.
     *
     * @param session the Maven session
     * @param remoteRepositories the remote repository
     * @return Returns a new ProjectBuildingRequest populated from the current
     * session and the current project remote repositories, used to resolve
     * artifacts.
     */
    private ProjectBuildingRequest newResolveArtifactProjectBuildingRequest(final MavenSession session,
            final List<ArtifactRepository> remoteRepositories) {
        final ProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest(session.getProjectBuildingRequest());
        buildingRequest.setRemoteRepositories(remoteRepositories);
        return buildingRequest;
    }

    /**
     * Returns <code>false</code> as the results are not cacheable.
     *
     * @return <code>false</code>
     */
    @Override
    public boolean isCacheable() {
        return false;
    }

    /**
     * As the results are not cacheable this returns <code>false</code>.
     *
     * @param cachedRule the cached rule
     * @return <code>false</code>
     */
    @Override
    public boolean isResultValid(final EnforcerRule cachedRule) {
        return false;
    }

    /**
     * Returns the cache id; since we do not have cacheable results this returns
     * <code>null</code>.
     *
     * @return <code>null</code>
     */
    @Override
    public String getCacheId() {
        return null;
    }

    /**
     * Set the value of supportedClassFileFormat.
     *
     * @param supportedClassFileFormat new value of supportedClassFileFormat
     */
    public void setSupportedClassFileFormat(final int supportedClassFileFormat) {
        this.supportedClassFileFormat = supportedClassFileFormat;
    }

    /**
     * Get the value of supportedClassFileFormat.
     *
     * @return the value of supportedClassFileFormat
     */
    public int getSupportedClassFileFormat() {
        return supportedClassFileFormat;
    }

    /**
     * Set the value of excludeScopeTest.
     *
     * @param excludeScopeTest new value of excludeScopeTest
     */
    public void setExcludeScopeTest(final boolean excludeScopeTest) {
        this.excludeScopeTest = excludeScopeTest;
    }

    /**
     * Get the value of excludeScopeTest.
     *
     * @return the value of excludeScopeTest
     */
    public boolean getExcludeScopeTest() {
        return excludeScopeTest;
    }

    /**
     * Get the value of excludeScopeProvided.
     *
     * @return the value of excludeScopeProvided
     */
    public boolean isExcludeScopeProvided() {
        return excludeScopeProvided;
    }

    /**
     * Set the value of excludeScopeProvided.
     *
     * @param excludeScopeProvided new value of excludeScopeProvided
     */
    public void setExcludeScopeProvided(boolean excludeScopeProvided) {
        this.excludeScopeProvided = excludeScopeProvided;
    }
}
