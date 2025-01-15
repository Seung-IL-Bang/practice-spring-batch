package com.practice.springbatch;

public class Customer {

    private String name;
    private int age;
    private String year;

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", year='" + year + '\'' +
                '}';
    }
}
