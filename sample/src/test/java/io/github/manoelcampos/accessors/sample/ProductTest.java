package io.github.manoelcampos.accessors.sample;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProductTest {
    private final Product product = new Product();

    @Test
    void readingIdCallsGetterAndDoesNotReturnNull() {
        assertNotNull(product.id);
    }

    @Test
    void writingIdCallsSetterAndStoresZeroInsteadOfNegative() {
        product.id = -1L;
        assertEquals(0, product.id);
    }

    @Test
    void readingModelCallsGetterAndDoesNotReturnNull() {
        assertNotNull(product.model);
    }

    @Test
    void writingModelCallsSetterAndConvertsToUpperCase() {
        product.model = "abc123";
        assertEquals("ABC123", product.model);
    }

    @Test
    void writingNameCallsSetterAndTrimGivenString() {
        product.name = "   TV   ";
        assertEquals("TV", product.name);
    }

    @Test
    void writingNameCallsSetterAndStoresEmptyInsteadOfNull() {
        product.name = null;
        assertEquals("", product.name);
    }
}
