package io.github.manoelcampos.accessors.sample;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

/**
 * All atributes are public since we are using https://github.com/manoelcampos/auto-class-accessors-java
 * @author Manoel Campos
 */
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    public Long id;

    @NotNull @NotBlank
    public String name;

    @NotNull @NotBlank
    public String model;

    public Product() {
    }

    public Product(final long id) {
        this.id = id;
    }

    public Product(final String name) {
        this.name = name;
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

    public Long getId() {
        return Objects.requireNonNullElse(id, 0L);
    }

}
