# Java Auto Class Accessors Maven Plugin ![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/manoelcampos/auto-class-accessors-maven-plugin/build.yml) [![Maven Central](https://img.shields.io/maven-central/v/io.github.manoelcampos/auto-class-accessors-maven-plugin.svg?label=Maven%20Central)](https://central.sonatype.com/search?q=auto-class-accessors-maven-plugin&namespace=io.github.manoelcampos)

A tiny Maven Plugin that uses [bytecode manipulation](https://github.com/raphw/byte-buddy) to provide automatic implementation of accessors in classes. You can make your class attributes public and the plugin will replace read and write access to them with calls to the corresponding getter and setter method (if existing).

## 1. Usage

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

Access any public instance fields of the `City` class from other class:

```java
public class Main {
    public static void main(String[] args){
        var city = new City();
        city.name = "Brasília";
        city.state = "DF";
        
        System.out.printf("City: %s-%s\n", city.name, city.state);
    }
}
```

Now build the project. The plugin will change the bytecode of the class above, during build time, 
to automatically call the corresponding getter or setter method instead (if existing).
This way, the source code corresponding to the resulting bytecode for the `Main` class will be:

```java
public class Main {
    public static void main(String[] args){
        var city = new City();
        city.setName("Brasília");
        city.setState("DF");
        
        System.out.printf("City: %s-%s\n", city.name, city.getState()); // there is no getter for the name field
    }
}
```

You can confirm that by opening the `Main.class` compiled file inside some IDE.

## 2. Motivation

JDK 16 introduced the [record type](https://openjdk.org/jeps/395), which are shallowly-immutable classes providing automatic getters and other utilities. However, [records cannot be fully used as JPA entities](https://thorben-janssen.com/java-records-hibernate-jpa/#records-cant-be-entities).

This way, lots of people rely on [Lombok](https://projectlombok.org) to generate getters and setters for their entities. However, Lombok is an annotation processor that poses some challenges for projects with a more complex setup. For instance:

- annotation processors are not called during JavaDoc execution;
- each new version of the JDK may break Lombok;
- it requires extra configuration when working with newer versions of Spring
Boot;
- and when the project uses multiple annotation processors, usually we need to [ensure that Lombok is executed first](https://github.com/projectlombok/lombok/issues/973#issuecomment-2537613474). 


## 3. How the plugin is different from Lombok

Lombok and this plugin perform bytecode manipulation during your project build.
However, Lombok usually adds new methods that you'll call directly (such as getters and setters).
For instance, you can create a class with no getters and setters and use Lombok annotations to add them:

```java
/** With Lombok we have regular private fields */
@Getter @Setter
public class City {
    private String name;
    private String state;
}
```

Then you'd need to explicitly call the getters and setters yourself. 
Taking the `Main` class as an example, this should be the code you'd need to write:

```java
public class Main {
    public static void main(String[] args){
        var city = new City();
        city.setName("Brasília");
        city.setState("DF");
        
        System.out.printf("City: %s-%s\n", city.getName(), city.getState());
    }
}
```

But in fact, the `City.java` file doesn't have these accessor methods that you are calling directly inside your `Main.java` class.
This way, Lombok needs IDE support to make this work.

Using the Java Auto Class Accessors Maven Plugin, you don't need to choose between referencing the name of the fields or the respective accessor (despite you can). 
You can access the public fields directly, and you'd be assured that the generated bytecode will have such accesses replaced by calls to the corresponding getter or setter method.
This way, even if you remove the plugin, your code still compiles (despite it may not have the same behaviour/results as before).

That simplifies the build process and avoid Lombok issues that always happen when opening the project on some IDE (even those which have default support for it).
Who haven't sometime opened a project on IntelliJ and got a lot of errors because of Lombok?

## 4. How to remove the plugin

In order to the plugin from the pom.xml is enough to delete it from your project's pom.xml file.
Your project will continue building, despite you may not have the same behaviour/results as before.
It's a great idea to have tests. But regardless of that, if you have getters and setters with custom logic,
you'll need to explicitly call them after removing the plugin.

You can automatically do that in your entire project by using the [OpenRewrite tool](https://github.com/openrewrite/rewrite), [which has a Maven Plugin as well](https://docs.openrewrite.org).

## 5. Plans for future

Lombok has some great features. Any feature that just changes existing methods (instead of introducing brand-new ones that you explicitly call) can be implemented.
Some of them are methods from the `Object` class, such as `toString()`, `equals()`, `hashCode()` etc.
Lombok provides a very nice set of annotations to override the implementation of these methods, which can be implemented here in the same way,
so that the plugin will some compatibility with Lombok (despite the package of these annotations here will be different).

Other Lombok annotation such as `@Builder` can be implemented as well, but in a different way: performing source code generation instead of adding a new Builder class inside your own class.
This approach don't require IDE support, since the generated Java file will be found by the IDE.
But this is something that is wonderfully made by the [Immutable Library](http://immutables.github.io).

On the other hand, Lombok features such as `@AllArgsConstructor` won't be implemented here, 
since they introduce new methods just in the .class files, that your IDE won't be aware of. 
Furthermore, this annotation and `@Builder` have their own [issues for years](https://github.com/projectlombok/lombok/issues/2888).

