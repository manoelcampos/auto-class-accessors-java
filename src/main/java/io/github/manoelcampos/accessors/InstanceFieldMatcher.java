package io.github.manoelcampos.accessors;

import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * @author Manoel Campos
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
