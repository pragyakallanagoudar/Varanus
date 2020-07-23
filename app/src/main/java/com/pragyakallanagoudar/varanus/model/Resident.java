package com.pragyakallanagoudar.varanus.model;

import java.lang.String;

public class Resident
{
    public String photo; // the name of the photo (equal to the residentID)
    public String name; // the name of the animal
    public String enclosure; // the enclosure that the animal is in
    public String species; // the species of the animal

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
