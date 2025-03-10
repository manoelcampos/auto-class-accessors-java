package io.github.manoelcampos.accessors;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * A base classe to implement {@link ElementMatcher}s to check if a method is the accessor (getter/setter) for a given field.
 * @author Manoel Campos
 */
abstract class AbstractAccessorMatcher extends ElementMatcher.Junction.AbstractBase<MethodDescription> {
    /**
     * A matcher that contains the last instance field matched,
     * which its accessor method will be lookup up by subclasses of this class.
     */
    private final InstanceFieldMatcher fieldMatcher;

    /**
     * Creates an Accessor Matcher for a given field.
     * @param fieldMatcher see {@link #fieldMatcher}
     */
    AbstractAccessorMatcher(final InstanceFieldMatcher fieldMatcher) {
        this.fieldMatcher = fieldMatcher;
    }

    protected boolean isMatchedFieldBoolean() {
        final var fieldTypeName = fieldMatcher.getFieldDescription().getType().getTypeName();
        return fieldTypeName.equals("boolean") || fieldTypeName.equals("java.lang.Boolean");
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
        return "%s{field=%s}".formatted(getClass().getSimpleName(), getFieldName());
    }

    /**
     * {@return the name of the matched instance field}
     * @see #fieldMatcher
     */
    public String getFieldName(){
        return fieldMatcher.getFieldDescription().getName();
    }
}
