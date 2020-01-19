package com.pragyakallanagoudar.varanus.model;

import java.util.ArrayList;
import java.util.List;

public class Species
{
    public String name; // the name of the species
    public List<String> animals; // the list of animals that are part of this species
    public List<Task> default_tasks; // the default tasks for the species

    public Species(String name, List<String> animals, List<Task> default_tasks)
    {
        this.name = name;
        this.animals = animals;
        this.default_tasks = default_tasks;
    }

    public Species (String name)
    {
        this.name = name;
        animals = new ArrayList<String>();
        default_tasks = new ArrayList<Task>();
    }

    public void addAnimal(String name_animal)
    {
        animals.add(name_animal);
    }

    public void addDefaultTask (Task task)
    {
        default_tasks.add(task);
    }
}
