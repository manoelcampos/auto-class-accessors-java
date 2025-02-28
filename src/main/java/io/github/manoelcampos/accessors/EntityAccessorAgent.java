package io.github.manoelcampos.accessors;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * @author Manoel Campos
 */
public class EntityAccessorAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        final var isClassMatcher = not(isRecord().or(isInterface()).or(isAnnotation()));
        final var entityMatcher = named("jakarta.persistence.Entity");

        new AgentBuilder.Default()
                .type(isClassMatcher.and(ElementMatchers.isAnnotatedWith(entityMatcher)))
                .transform((builder, typeDescription, classLoader, module, domain) ->
                       builder.method(ElementMatchers.isPublic()).intercept(Advice.to(FieldAccessInterceptor.class))
                ).installOn(inst);
    }
}
