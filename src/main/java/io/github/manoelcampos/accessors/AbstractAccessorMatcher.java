package io.github.manoelcampos.accessors;

import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * A base classe to implement {@link ElementMatcher}s to check if a method is the accessor (getter/setter) for a given field.
 * @author Manoel Campos
 */
abstract class AbstractAccessorMatcher extends ElementMatcher.Junction.AbstractBase<MethodDescription> {
    /**
     * The field to find the accessor for
     */
    protected final FieldDescription fieldDescription;

    /**
     * Creates an Accessor Matcher for a given field.
     * @param fieldDescription see {@link #fieldDescription}
     */
    AbstractAccessorMatcher(final FieldDescription fieldDescription) {
        this.fieldDescription = fieldDescription;
    }

    protected String capitalize(final String name) {
        final String end = name.length() > 1 ? name.substring(1) : "";
        return name.substring(0, 1).toUpperCase() + end;
    }

    /**
     * Checks if a given method is the accessor (getter/setter) for a given field.
     *
     * @param methodDescription a method being checked
     * @return true if the method is the accessor for the given field, false otherwise
     */
    protected abstract boolean isAccessorForField(final MethodDescription methodDescription);

    @Override
    public String toString() {
        return "%s{field=%s}".formatted(getClass().getSimpleName(), fieldDescription.getName());
    }
}
