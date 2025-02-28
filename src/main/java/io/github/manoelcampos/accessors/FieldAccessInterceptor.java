package io.github.manoelcampos.accessors;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

/**
 * Intercepts access (reads and writes) to public fields in classes
 * defined by {@link EntityAccessorAgent}, replacing such access
 * by the respective getter and setter methods (if existent).
 * @author Manoel Campos
 */
public class FieldAccessInterceptor {
    /**
     * Intercepts field read access, replacing it by the respective getter method call (if existing).
     * @param fieldValue
     * @param instance
     * @param methodName
     */
    @Advice.OnMethodEnter
    public static void interceptRead(
         @Advice.FieldValue(value = "name", readOnly = false) Object fieldValue,
         final @Advice.This Object instance,
         final @Advice.Origin("#t.#m") String methodName)
    {
        try {
            final String fieldName = extractFieldName(methodName);
            final Method getter = instance.getClass().getMethod("get" + capitalize(fieldName));
            fieldValue = getter.invoke(instance);
        } catch (Exception ignored) {/**/}
    }

    /**
     * Intercepts field write access, replacing it by the respective setter method call (if existing).
     * @param newValue
     * @param instance
     * @param methodName
     */
    @Advice.OnMethodExit
    public static void interceptWrite(
          final @Advice.Argument(value = 0) Object newValue,
          final @Advice.This Object instance,
          final @Advice.Origin("#t.#m") String methodName)
    {
        try {
            final String fieldName = extractFieldName(methodName);
            final Method setter = instance.getClass().getMethod("set" + capitalize(fieldName), newValue.getClass());
            setter.invoke(instance, newValue);
        } catch (Exception ignored) {/**/}
    }

    private static String extractFieldName(final String methodName) {
        return methodName.replace("get", "").replace("set", "");
    }

    private static String capitalize(final String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
