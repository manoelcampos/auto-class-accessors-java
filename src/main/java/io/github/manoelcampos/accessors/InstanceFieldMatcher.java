package io.github.manoelcampos.accessors;

import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * An {@link ElementMatcher} that matches instance fields that are public and non-static.
 * It's used to intercept reads and writes to such fields in order to replace them by the respective accessor method calls.
 *
 * @author Manoel Campos
 * @see EntityAccessorInstrumentationPlugin
 */
class InstanceFieldMatcher extends ElementMatcher.Junction.AbstractBase<FieldDescription> {
    private FieldDescription fieldDescription;

    @Override
    public boolean matches(final FieldDescription fieldDescription) {
        final boolean matches = isPublic().and(not(isStatic())).matches(fieldDescription);
        if(matches) {
            System.out.printf("Matched Field: %-10s%n", fieldDescription.getName());
            this.fieldDescription = fieldDescription;
        }
        return matches;
    }

    public FieldDescription getFieldDescription() {
        return fieldDescription;
    }
}
