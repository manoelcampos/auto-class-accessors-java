package io.github.manoelcampos.accessors;

import io.github.manoelcampos.accessors.InstanceFieldMatcher.AccessorLookup;
import net.bytebuddy.asm.MemberSubstitution;
import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * Byte Buddy plugin that instruments the bytecode of classes that directly access public fields in JPA Entities,
 * replacing such access by the respective accessor (getter or setter) method calls (if existent).
 * This plugin defines how the transformation will be done, indicating when and how byte code will be changed.
 * @author Manoel Campos
 */
public class EntityAccessorInstrumentationPlugin implements Plugin {
    @Override
    public DynamicType.Builder<?> apply(
        final DynamicType.Builder<?> builder,
        final TypeDescription typeDescription,
        final ClassFileLocator classFileLocator)
    {
        final var fieldMatcherForFieldRead = new InstanceFieldMatcher(AccessorLookup.GETTER, typeDescription);
        final var fieldMatcherForFieldWrite = new InstanceFieldMatcher(AccessorLookup.SETTER, typeDescription);

        // TODO: See MemberSubstitution docs for Notes
        // Replaces public instance fields reads by the respective getter call
        final var getterMatcher = new GetterMatcher(fieldMatcherForFieldRead);
        final var visitorForMethodWithFieldRead = MemberSubstitution.relaxed()
                                                                 .field(fieldMatcherForFieldRead)
                                                                 .onRead()
                                                                 .replaceWithMethod(getterMatcher)
                                                                 .on(ElementMatchers.isMethod());

        // Replaces public instance fields writes by the respective setter call
        final var setterMatcher = new SetterMatcher(fieldMatcherForFieldWrite);
        final var visitorForMethodWithFieldWrite = MemberSubstitution.relaxed()
                                                                  .field(fieldMatcherForFieldWrite)
                                                                  .onWrite()
                                                                  .replaceWithMethod(setterMatcher)
                                                                  .on(ElementMatchers.isMethod());

        return builder.visit(visitorForMethodWithFieldRead).visit(visitorForMethodWithFieldWrite);
    }

    /**
     * {@inheritDoc}
     * Defines if a class (where a field access was intercepted) will be transformed by this plugin.
     * It intends to transform classes that are directly accessing public fields in JPA Entity classes,
     * but those entities themselves won't be transformed.
     * @param typeDefinition {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean matches(final TypeDescription typeDefinition) {
        return !isJpaEntity(typeDefinition);
    }

    /**
     * Checks if a type about to be transformed is an Entity class,
     * a class annotated with JPA {@code @Entity}.
     * @param typeDefinition the type to be checked
     * @return
     */
    static boolean isJpaEntity(final TypeDescription typeDefinition) {
        return isAnnotatedWith(named("jakarta.persistence.Entity")).matches(typeDefinition);
    }

    /**
     * {@return a matcher that checks if an element is not a record, interface or annotation, but a class}
     */
    private static ElementMatcher.Junction<TypeDescription> isClassMatcher() {
        return not(isRecord().or(isInterface()).or(isAnnotation()));
    }

    @Override
    public void close() {
        // TODO: What needs to be closed here?
    }
}
