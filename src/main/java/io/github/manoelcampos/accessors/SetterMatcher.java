package io.github.manoelcampos.accessors;

import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;

import static net.bytebuddy.matcher.ElementMatchers.isSetter;

/**
 * @author Manoel Campos
 */
class SetterMatcher extends AbstractAccessorMatcher {
    public SetterMatcher(final FieldDescription fieldDescription) {
        super(fieldDescription);
    }

    @Override
    public boolean matches(final MethodDescription methodDescription) {
        final boolean matches = isSetter().matches(methodDescription) && isAccessorForField(methodDescription);
        if(matches) {
            System.out.printf(
                    "       Field: %-10s Setter: %s%n",
                    fieldDescription.getName(), methodDescription.getName());
        }

        return matches;
    }

    @Override
    protected boolean isAccessorForField(final MethodDescription methodDescription) {
        return methodDescription.getName().equals("set" + capitalize(fieldDescription.getName()));
    }

}
