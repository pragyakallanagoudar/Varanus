package com.pragyakallanagoudar.varanus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class SpeciesPage extends AppCompatActivity {

    public Button[] names; // buttons for every animal of this species
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_species_page);
    }
}
