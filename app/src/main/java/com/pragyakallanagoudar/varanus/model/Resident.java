package com.pragyakallanagoudar.varanus.model;

import java.lang.String;

public class Resident
{
    public String photo;
    public String name;
    public String enclosure;

    public Resident() {}

    public Resident(String photo, String name, String enclosure)
    {
        this.photo = photo;
        this.name = name;
        this.enclosure = enclosure;
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

}
