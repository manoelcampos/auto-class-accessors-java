package io.github.manoelcampos.accessors;

import io.github.manoelcampos.accessors.InstanceFieldMatcher.AccessorLookup;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.asm.MemberSubstitution;
import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
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
        // TODO: See MemberSubstitution docs for Notes
        final var visitorForMethodsWithFieldRead = newFieldReadVisitor(typeDescription);
        final var visitorForMethodsWithFieldWrite = newFieldWriteVisitor(typeDescription);
        return builder.visit(visitorForMethodsWithFieldRead).visit(visitorForMethodsWithFieldWrite);
    }

    /**
     * Replaces public instance fields reads by the respective getter call.
     * @param typeDescription the type being transformed, where the method writing a field is defined
     * @return a visitor for methods which have a field read operation
     */
    private static AsmVisitorWrapper.ForDeclaredMethods newFieldReadVisitor(TypeDescription typeDescription) {
        final var fieldMatcherForFieldRead = new InstanceFieldMatcher(AccessorLookup.GETTER, typeDescription);
        final var getterMatcher = new GetterMatcher(fieldMatcherForFieldRead);
        return MemberSubstitution.relaxed()
                                 .field(fieldMatcherForFieldRead)
                                 .onRead()
                                 .replaceWithMethod(getterMatcher)
                                 .on(ElementMatchers.isMethod());
    }

    /**
     * Replaces public instance fields writes by the respective setter call.
     * @param typeDescription the type being transformed, where the method reading a field is defined
     * @return a visitor for methods which have a field write operation
     */
    private static AsmVisitorWrapper.ForDeclaredMethods newFieldWriteVisitor(TypeDescription typeDescription) {
        final var fieldMatcherForFieldWrite = new InstanceFieldMatcher(AccessorLookup.SETTER, typeDescription);
        final var setterMatcher = new SetterMatcher(fieldMatcherForFieldWrite);
        return MemberSubstitution.relaxed()
                                 .field(fieldMatcherForFieldWrite)
                                 .onWrite()
                                 .replaceWithMethod(setterMatcher)
                                 .on(ElementMatchers.isMethod());
    }

    /**
     * {@inheritDoc}
     * Indicates that only classes (where a field access was intercepted) will be transformed by this plugin.
     * This no, interfaces and records are not transformed since there is no need for them.
     * @param typeDefinition {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean matches(final TypeDescription typeDefinition) {
        return not(isInterface().or(isRecord())).matches(typeDefinition);
    }

    @Override
    public void close() {
        // TODO: What needs to be closed here?
    }
}
