package io.github.manoelcampos.accessors;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * @author Manoel Campos
 */
public class EntityAccessorAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        final var entityMatcher = named("jakarta.persistence.Entity");

        new AgentBuilder.Default()
                .type(isClassMatcher().and(isAnnotatedWith(entityMatcher)))
                .transform((builder, typeDesc, cl, m, d) ->
                       builder.method(isPublic()).intercept(Advice.to(FieldAccessInterceptor.class))
                ).installOn(inst);
    }

    /**
     * Checks if an element is not a record, interface or annotation, but a class.
     * @return
     */
    private static ElementMatcher.Junction<TypeDescription> isClassMatcher() {
        return not(isRecord().or(isInterface()).or(isAnnotation()));
    }
}
