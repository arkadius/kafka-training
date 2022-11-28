package pl.touk.training.kafka.transaction;

import java.util.Objects;

public class Foo {

    private String name;
    private int age;

    public Foo() {
    }

    public Foo(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "Foo{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Foo foo = (Foo) o;
        return age == foo.age && Objects.equals(name, foo.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
}
