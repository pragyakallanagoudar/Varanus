package com.pragyakallanagoudar.varanus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class AnimalsPage extends AppCompatActivity {

    public Button[] species_buttons; // a button for every single species of animal
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animals_page);
    }

    //when you click on one of the buttons send in the identifying value with the intent

}
