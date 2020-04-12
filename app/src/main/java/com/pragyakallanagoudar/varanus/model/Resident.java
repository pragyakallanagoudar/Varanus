package com.pragyakallanagoudar.varanus.model;

import java.lang.String;

public class Resident
{
    public String photo;
    public String name;

    public Resident() {}

    public Resident(String photo, String name)
    {
        this.photo = photo;
        this.name = name;
    }
    public String getPhoto() {
        return photo;
    }

    public String getName() {
        return name;
    }
}
