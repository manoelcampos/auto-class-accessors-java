package io.github.manoelcampos.accessors;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * Byte Buddy plugin that instruments the bytecode of classes that directly access public fields in JPA Entities,
 * replacing such access by the respective getter and setter method calls (if existent).
 * The interception of field access is done by the {@link FieldAccessInterceptor} class.
 * @author Manoel Campos
 */
public class EntityAccessorInstrumentationPlugin implements Plugin {
    @Override
    public DynamicType.Builder<?> apply(
            final DynamicType.Builder<?> builder,
            final TypeDescription typeDescription, final ClassFileLocator classFileLocator) {
        return builder.method(isPublic()).intercept(Advice.to(FieldAccessInterceptor.class));
    }

    @Override
    public boolean matches(final TypeDescription typeDefinitions) {
        final var entityMatcher = named("jakarta.persistence.Entity");
        return isClassMatcher().and(isAnnotatedWith(entityMatcher)).matches(typeDefinitions);
    }

    /**
     * {@return a matcher that checks if an element is not a record, interface or annotation, but a class}
     */
    private static ElementMatcher.Junction<TypeDescription> isClassMatcher() {
        return not(isRecord().or(isInterface()).or(isAnnotation()));
    }

    @Override
    public void close() {

    }
}
