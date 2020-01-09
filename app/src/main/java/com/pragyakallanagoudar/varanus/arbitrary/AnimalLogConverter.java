package com.pragyakallanagoudar.varanus.arbitrary;

// FIX BUG: LOOK AT THE STACK OVERFLOW LINK AND FIGURE OUT HOW TO READ FROM THE TEXT FILE

// COME BACK TO THIS LATER THOUGH LOL
import android.util.Log;

import com.pragyakallanagoudar.varanus.Animal;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AnimalLogConverter
{
    public final String FILE_NAME = "/Users/pragyakallanagoudar/Desktop/Varanus/app/src/main/java/com/pragyakallanagoudar/varanus/animal_logs.txt";
    public Scanner fileReader;
    private final String LOG_TAG = AnimalLogConverter.class.getSimpleName();

    // returns list of animals from data read in.
    public List<Animal> getAnimals()
    {
        List<Animal> animals = new ArrayList<Animal>();

        try
        {
            fileReader = new Scanner(new File(FILE_NAME));

        }
        catch (FileNotFoundException fred)
        {
            Log.e(LOG_TAG, "cannot find le file");
        }

        int index = 0;
        while (fileReader.hasNext())
        {
            String line = fileReader.nextLine();
            if(line.contains("Name:"))
            {
                index++;
                animals.set(index, new Animal());
                animals.get(index).name = line.substring(line.indexOf(" ") + 1);
            }
            else if (line.contains("Species:"))
            {
                animals.get(index).species = line.substring(line.indexOf(" ") + 1);
            }
            else if(line.equals("Diet:"))
            {

            }
            else if (line.equals("Outside Log:"))
            {

            }
            else if(line.equals("Behavior Log:"))
            {

            }
        }
        return animals;
    }


}
