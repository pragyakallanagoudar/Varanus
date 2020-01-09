package com.pragyakallanagoudar.varanus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }

    public void openAnimals(View view)
    {
        Intent intent = new Intent(this, AnimalsPage.class);
        startActivity(intent);
    }

    public void onTasks(View view)
    {
        Intent intent = new Intent(this, Tasks.class);
        startActivity(intent);
    }
}
