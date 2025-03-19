package io.github.manoelcampos.accessors.sample;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Checks if the IDE is configured to use Maven to run you project/tests.
 * Only using maven the Auto Class Accessors plugin can be executed.
 * @author Manoel Campos
 */
class AutoAccessorsMavenPluginExecutionTest {
    private static class Sample {
        public Long id;
        public String name;

        public void setName(String name) {
            this.name = Objects.requireNonNullElse(name, "").trim();
        }

        public String getName() {
            return name == null ? null : name.toUpperCase();
        }

        public void setId(Long id) {
            this.id = Objects.requireNonNullElse(id, 0L);
        }
    }

    private final Sample sample = new Sample();

    /**
     * Tests the default values for the object fields
     * (which are null for the {@link Sample} class),
     * since no setters are called because no field write is performed.
     */
    @Test
    void objectInstantiationDefaultValues() {
        assertNull(sample.id);

        /*
        If a name is not set, the setter is not called and the default value is null
        Even if the name is set with null, that will be replaced by an empty string.
        */
        assertNull(sample.name);
    }

    @Test
    void accessorsAreBeingCalledOutsideLambdas() {
        final var msg =
                """
                ################################################ ERROR ########################################
                Accessors (getters/setters) for the %s class were not executed.
                Check the project running configuration on your IDE to make it delegate the execution to Maven.
                ###############################################################################################
                """.formatted(Sample.class.getName());

        /* The setter removes spaces and the getter returns the name in uppercase.
        * This way, we can assert if the setter and getter were called. */
        sample.name = "     Name      ";
        assertEquals("NAME", sample.name, msg);
    }

    @Test
    void accessorsAreBeingCalledInsideLambdas() {
        final var msg =
                """
                ################################################ ERROR ########################################
                Getters/setters for the %s class were not called when accessing the class fields from a lambda.
                ###############################################################################################
                """.formatted(Sample.class.getName());
        assertAll(msg,
                  () -> {
                      sample.name = null;
                      assertEquals("", sample.name);
                  },
                  () -> {
                      sample.name = "     Name      ";
                      assertEquals("NAME", sample.name);
                  }
        );
    }
}
