package io.github.manoelcampos.accessors.sample;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

/**
 * All atributes are public since we are using https://github.com/manoelcampos/auto-class-accessors-java
 * @author Manoel Campos
 */
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String name;

    public String model;

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

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return Objects.requireNonNullElse(id, 0L);
    }

    public String getModel() {
        System.out.println("Getting product.model: " + this.model);
        return Objects.requireNonNullElse(model, "");
    }

    public void setModel(final String model) {
        this.model = Objects.requireNonNullElse(model, "").toUpperCase();
        System.out.println("Setting product.model: " + this.model);
    }

    public void setName(final String name) {
        this.name = Objects.requireNonNullElse(name, "");
        System.out.println("Setting product.name: " + this.name);
    }
}
