package io.github.manoelcampos.accessors.sample;

public class Main {
    public static void main(final String[] args) {
        final var product = new Product();
        product.id = 1L;
        product.available = true;
        product.name = "   TV   ";
        product.model = "xy123";

        System.out.printf("Product id: %d name: %s model: %s available: %s%n", product.id, product.name, product.model, product.available);
    }
}
