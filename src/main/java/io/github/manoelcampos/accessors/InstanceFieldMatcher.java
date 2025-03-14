package io.github.manoelcampos.accessors;

import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static io.github.manoelcampos.accessors.GetterMatcher.getterName;
import static io.github.manoelcampos.accessors.SetterMatcher.setterName;
import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * An {@link ElementMatcher} that matches instance fields that are public and non-static.
 * It's used to intercept reads and writes to such fields in order to replace them by the respective accessor method calls.
 *
 * @author Manoel Campos
 * @see EntityAccessorInstrumentationPlugin
 */
class InstanceFieldMatcher extends ElementMatcher.Junction.AbstractBase<FieldDescription> {
    /**
     * Indicates if this matcher should look up for a getter or setter for the matching field.
     */
    enum AccessorLookup {
        GETTER,
        SETTER;
        boolean forGetter(){ return this == GETTER; }
    }

    private final AccessorLookup accessorLookup;

    /** @see #getFieldDescription() */
    private FieldDescription fieldDescription;

    InstanceFieldMatcher(final AccessorLookup accessorLookup) {
        this.accessorLookup = accessorLookup;
    }

    @Override
    public boolean matches(final FieldDescription fieldDescription) {
        final boolean matches = isPublic().and(not(isStatic())).matches(fieldDescription) && isAccessorMethodFound(fieldDescription);
        if(matches) {
            this.fieldDescription = fieldDescription;
        }
        return matches;
    }

    /**
     * Checks if there is an accessor (getter/setter) for a given field
     * @param field field to check
     * @return
     */
    private boolean isAccessorMethodFound(final FieldDescription field) {
        return field
                 .getDeclaringType()
                 .getDeclaredMethods()
                 .stream()
                 .anyMatch(m -> isAccessorForField(field, m));
    }

    /**
     * Checks if a given method is the accessor for a field
     * @param field field to check
     * @param method method to check
     * @return
     */
    private boolean isAccessorForField(final FieldDescription field, final MethodDescription method) {
        final boolean accessorNameMatchesField = method.getName().equals(accessorLookup.forGetter() ? getterName(field) : setterName(field));
        return accessorNameMatchesField && (accessorLookup.forGetter() ? isGetter().matches(method) : isSetter().matches(method));
    }

    /**
     * {@return the last instance field matched by this matcher}
     * Based on this field, an accessor method will be searched to replace the field access.
     * @see AbstractAccessorMatcher
     */
    public FieldDescription getFieldDescription() {
        return fieldDescription;
    }
}
