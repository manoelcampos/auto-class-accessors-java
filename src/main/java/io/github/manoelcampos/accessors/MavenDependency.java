package io.github.manoelcampos.accessors;

/**
 * Represents a Maven Dependency, containing the atributes required to download it.
 * @author Manoel Campos
 */
public record MavenDependency(String groupId, String artifactId, String version) {
}
