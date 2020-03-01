package com.pragyakallanagoudar.varanus.model;

import java.util.List;

public class Species
{
    public String name; // the name of the species
    public List<String> animals; // the list of animals that are part of this species
    public List<String> enclosures; // the list of enclosures

    public Species(String name, List<String> animals, List<String> enclosures)
    {
        this.name = name;
        this.animals = animals;
        this.enclosures = enclosures;
    }

    public Species() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getEnclosures() {
        return enclosures;
    }

    public void setEnclosures(List<String> enclosures) {
        this.enclosures = enclosures;
    }

    public List<String> getAnimals() {
        return animals;
    }

    public void setAnimals(List<String> animals) {
        this.animals = animals;
    }
}
