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
 * @author Manoel Campos
 */
public class EntityAccessorInstrumentationPlugin implements Plugin {
    private final InstanceFieldMatcher fieldMatcherForFieldRead = new InstanceFieldMatcher(AccessorLookup.GETTER);
    private final InstanceFieldMatcher fieldMatcherForFieldWrite = new InstanceFieldMatcher(AccessorLookup.SETTER);

    @Override
    public DynamicType.Builder<?> apply(
        final DynamicType.Builder<?> builder,
        final TypeDescription typeDescription,
        final ClassFileLocator classFileLocator)
    {
        // TODO: See MemberSubstitution docs for Notes
        // Replaces public instance fields reads by the respective getter call
        final var getterMatcher = new GetterMatcher(typeDescription, fieldMatcherForFieldRead);
        final var visitorForMethodWithFieldRead = MemberSubstitution.relaxed()
                                                                 .failIfNoMatch(false)
                                                                 .field(fieldMatcherForFieldRead)
                                                                 .onRead()
                                                                 .replaceWithMethod(getterMatcher)
                                                                 .on(ElementMatchers.isMethod());

        // Replaces public instance fields writes by the respective setter call
        final var setterMatcher = new SetterMatcher(fieldMatcherForFieldWrite);
        final var visitorForMethodWithFieldWrite = MemberSubstitution.relaxed()
                                                                  .failIfNoMatch(false)
                                                                  .field(fieldMatcherForFieldWrite)
                                                                  .onWrite()
                                                                  .replaceWithMethod(setterMatcher)
                                                                  .on(ElementMatchers.isMethod());

        return builder.visit(visitorForMethodWithFieldRead).visit(visitorForMethodWithFieldWrite);
    }

    /**
     * {@inheritDoc}
     * Defines the classes that will be transformed by this plugin.
     * It intends to transform classes that are directly accessing public fields in JPA Entities,
     * but those entities themselves won't be transformed.
     * This way, if the entity directly access one of its own fields,
     * that access won't be replaced by the respective accessor method call.
     * @param typeDefinitions {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean matches(final TypeDescription typeDefinitions) {
        final var entityMatcher = named("jakarta.persistence.Entity");
        return isClassMatcher().and(not(isAnnotatedWith(entityMatcher))).matches(typeDefinitions);
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
