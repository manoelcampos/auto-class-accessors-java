# Java Auto Class Accessors

A tiny library that uses [bytecode manipulation](https://github.com/raphw/byte-buddy) to provide automatic implementation of accessors in JPA entity classes (classes annotated with `@jakarta.persistence.Entity`).

You can make your entity attributes public and the library will replace read and write access to those attributes with calls to the corresponding getter and setter methods (if existing).

## Usage

Add the dependency to your project:

```xml
<dependency>
    <groupId>io.github.manoelcampos</groupId>
    <artifactId>auto-class-accessors</artifactId>
    <version>LATEST</version> <!-- You can set a specific version here -->
</dependency>
```

Now create you JPA Entity classes, such as:

```java
@Entity
public class City {
    @NotNull @NotBlank
    public String name;

    @NotNull @NotBlank
    public String state;

    public String getState() {
        System.out.println("Getting state: " + this.state);
        return state;
    }

    public void setState(final String state) {
        this.state = Objects.requireNonNullElse(sigla, "").toUpperCase();
        System.out.println("Setting state: " + this.state);
    }

    public void setName(final String name) {
        this.name = Objects.requireNonNullElse(name, "");
        System.out.println("Setting name: " + this.name);
    }
}
```

Now, if you access any public attribute of the `City` class anywhere, the library will automatically call the corresponding getter or setter method (if existing):

```java
var city = new City();
city.name = "Brasília";
city.state = "DF";

System.out.printf("City: %s-%s\n", city.name, city.state);
```

## Motivation

JDK 16 introduced the [record type](https://openjdk.org/jeps/395), which are shallowly-immutable classes providing automatic getters and other utilities. However, [records cannot be fully used as JPA entities](https://thorben-janssen.com/java-records-hibernate-jpa/#records-cant-be-entities).

This way, lots of people rely on [Lombok](https://projectlombok.org) to generate getters and setters for their entities. However, Lombok is an annotation processor that poses some challenges for projects with a more complex setup. For instance:

- annotation processors are not called during JavaDoc execution;
- each new version of the JDK may break Lombok;
- it requires extra configuration when working with newer versions of Spring
Boot;
- and when the project uses multiple annotation processors, usually we need to [ensure that Lombok is executed first](https://github.com/projectlombok/lombok/issues/973#issuecomment-2537613474). 


