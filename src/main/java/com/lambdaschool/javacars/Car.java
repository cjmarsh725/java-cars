package com.lambdaschool.javacars;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class Car
{
    private @Id @GeneratedValue Long id;
    private int year;
    private String brand;
    private String model;

    public Car() {}

    public Car(int year, String brand, String model)
    {
        this.year = year;
        this.brand = brand;
        this.model = model;
    }
}
