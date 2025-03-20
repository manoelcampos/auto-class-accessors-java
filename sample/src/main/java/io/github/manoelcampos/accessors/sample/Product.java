package io.github.manoelcampos.accessors.sample;

import java.util.Objects;

/// All atributes are public since we are using [Auto Class Accessors](https://github.com/manoelcampos/auto-class-accessors-java)
/// This class can be an JPA Entity or not.
/// @author Manoel Campos
///
public class Product {
    public Long id;
    public String brand;
    public String model;
    public String name;

    /**
     * It just uses {@link Boolean} instead of the primitive type {@code boolean} to allow null values
     * and enable implementing a sample getter with some logic.
     */
    public Boolean available;

    public Product() {
    }

    public Product(final long id) {
        this.id = id;
    }

    public Product(final String name, final String model) {
        this.name = name;
        this.model = model;
    }

    public Product setId(Long id) {
        this.id = id >= 0 ? id : 0;
        System.out.printf("Setting product.id. Given value: %s. Stored value: %s%n", id, this.id);
        return this;
    }

    public Long getId() {
        final var id = Objects.requireNonNullElse(this.id, 0L);
        System.out.printf("Getting product.id. Stored value: %s. Returned value: %s%n", this.id, id);
        return id;
    }

    public boolean isAvailable() {
        final boolean available = this.available != null && this.available;
        System.out.printf("Getting product.available. Stored value: '%s'. Returned value: '%s'%n", this.available, available);
        return available;
    }

    public String getModel() {
        final var model = Objects.requireNonNullElse(this.model, "");
        System.out.printf("Getting product.model. Stored value: '%s'. Returned value: '%s'%n", this.model, model);
        return model;
    }

    public void setModel(final String model) {
        this.model = Objects.requireNonNullElse(model, "").toUpperCase();
        System.out.printf("Setting product.model. Given value: '%s'. Stored value: '%s'%n", model, this.model);
    }

    public void setName(final String name) {
        this.name = Objects.requireNonNullElse(name, "").trim();
        System.out.printf("Setting product.name. Given value: '%s'. Stored value: '%s'%n", name, this.name);
    }

    @Override
    public String toString() {
        return "Product{id=%d, brand='%s', model='%s', name='%s', available=%s}".formatted(id, brand, model, name, available);
    }
}
