# Java Auto Class Accessors Maven Plugin

A tiny Maven Plugin that uses [bytecode manipulation](https://github.com/raphw/byte-buddy) to provide automatic implementation of accessors in classes. You can make your class attributes public and the plugin will replace read and write access to them with calls to the corresponding getter and setter method (if existing).

## Usage

Add the plugin inside the `<build><plugins>` tag of your project's `pom.xml` file:

```xml
<plugin>
    <groupId>io.github.manoelcampos</groupId>
    <artifactId>auto-class-accessors-maven-plugin</artifactId>
    <version>1.0.0</version>
    <executions><execution><goals><goal>apply</goal></goals></execution></executions>
</plugin>
```

Create a class (it can be a JPA entity or not) with public instance fields:

```java
public class City {
    public String name;

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

Now, if you access any public instance fields of the `City` class from other class, the plugin will change the class' bytecode during build time
to automatically call the corresponding getter or setter method instead (if existing):

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


