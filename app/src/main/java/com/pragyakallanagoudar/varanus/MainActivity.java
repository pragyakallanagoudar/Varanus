package com.pragyakallanagoudar.varanus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void onAnimals(View view)
    {
        Intent intent = new Intent(this, AnimalsPage.class);
        startActivity(intent);
    }
    public void onTasks(View view)
    {
        Intent intent = new Intent(this, TabbedTasks.class);
        startActivity(intent);
    }
}
