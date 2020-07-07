package com.pragyakallanagoudar.varanus.model;

import java.lang.String;

public class Resident
{
    public String photo;
    public String name;
    public String enclosure;
    public String species;

    public Resident() {}

    public Resident(String photo, String name, String enclosure, String species)
    {
        this.photo = photo;
        this.name = name;
        this.enclosure = enclosure;
        this.species = species;
    }
    public String getPhoto() {
        return photo;
    }

    public String getName() {
        return name;
    }

    public String getEnclosure() {
        return enclosure;
    }

    public String getSpecies() { return species; }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEnclosure(String enclosure) {
        this.enclosure = enclosure;
    }

    public void setSpecies(String species) {
        this.species = species;
    }
}
