package com.pragyakallanagoudar.varanus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

/**
 * AnimalProfile is a generic class that represents the data for a single Animal
 * instance (taken in as a field variable). It is created and displayed when the
 * button corresponding to that animal in the AnimalsPage is clicked.
 */

public class AnimalProfile extends AppCompatActivity {

    //public Animal animal;
    public TextView animal_name;
    /*
    public AnimalProfile(Animal animalIn)
    {
        animal = animalIn;
    }


    //methods that display visualizations of the animal's data

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_profile);

        // code to set the title to the animal's Name (SPECIES)
        animal_name = findViewById(R.id.animal_name);
        String displayName = animal.name + "(" + animal.species + ")";
        animal_name.setText(displayName);
    }
    */
}
