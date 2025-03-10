package io.github.manoelcampos.accessors;

import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.isGetter;

/**
 * Aa {@link ElementMatcher} to check if a method is the getter for a given field.
 * @author Manoel Campos
 * @see EntityAccessorInstrumentationPlugin
 */
class GetterMatcher extends AbstractAccessorMatcher {
    /**
     * The type being transformed
     */
    private final TypeDescription typeDescription;

    /**
     * Creates a GetterMatcher.
     * @param typeDescription see {@link #typeDescription}
     * @param fieldDescription see {@link #fieldDescription}
     */
    public GetterMatcher(final TypeDescription typeDescription, final FieldDescription fieldDescription) {
        super(fieldDescription);
        this.typeDescription = typeDescription;
    }

    @Override
    public boolean matches(final MethodDescription methodDescription) {
        final boolean matches = isGetter().matches(methodDescription) && isAccessorForField(methodDescription);
        if(matches) {
            System.out.println("Type being transformed: " + typeDescription.getName());
            System.out.printf(
                    "        Field: %-10s Getter: %s%n",
                    fieldDescription.getName(), methodDescription.getName());
        }

        return matches;
    }

    @Override
    protected boolean isAccessorForField(final MethodDescription methodDescription) {
        final var methodName = methodDescription.getName();
        final var isBoolean = isBooleanField(fieldDescription);
        final var fieldName = capitalize(fieldDescription.getName());
        return (isBoolean && methodName.equals("is"+fieldName)) || (!isBoolean && methodName.equals("get"+fieldName));
    }

    private static boolean isBooleanField(final FieldDescription fieldDescription) {
        final var fieldTypeName = fieldDescription.getType().getTypeName();
        return fieldTypeName.equals("boolean") || fieldTypeName.equals("java.lang.Boolean");
    }
}
