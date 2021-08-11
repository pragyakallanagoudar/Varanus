package com.pragyakallanagoudar.varanus.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.pragyakallanagoudar.varanus.R;
import com.pragyakallanagoudar.varanus.model.Resident;
import com.pragyakallanagoudar.varanus.model.TaskType;
import com.pragyakallanagoudar.varanus.model.log.TextLog;
import com.pragyakallanagoudar.varanus.model.task.CleanTask;
import com.pragyakallanagoudar.varanus.utilities.Utils;
import com.pragyakallanagoudar.varanus.model.task.Task;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.storage.StorageReference;

/**
 * The activity to add a new animal to the database.
 */

// Comment Level: (1) method headers & complex code

public class AddAnimalActivity extends AppCompatActivity implements View.OnClickListener  {

    public static final String TAG = AddAnimalActivity.class.getSimpleName(); // tag for log messages

    final ArrayList<TextLog> logs = new ArrayList<TextLog>(); // the list of general text logs

    public EditText nameEditText; // the edit text for the user to add to the general logs
    public FloatingActionButton uploadButton; // the button to upload an image
    public Spinner locationSpinner; // the spinner to select the location in the Center
    public Spinner speciesSpinner; // the spinner to select the species of the animal

    public FirebaseFirestore mFirestore; // Firebase database instance

    public String newResidentID; // the document ID for the new animal being added

    public ImageView imageView; // the image view to display the prospective animal image

    public static final int PICK_IMAGE = 1; // the integer code for picking the image

    // public String url;

    public Uri imageUri; // the Uri of the image selected from the phone gallery

    /**
     * Instantiate the field variables.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_new);

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        nameEditText = findViewById(R.id.name_edit_text);

        //uploadButton = findViewById(R.id.upload_button);

        locationSpinner = findViewById(R.id.location_spinner);
        speciesSpinner = findViewById(R.id.species_spinner);

        findViewById(R.id.submit_button).setOnClickListener(this);
        findViewById(R.id.cancel_button).setOnClickListener(this);
        findViewById(R.id.image_button).setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Add New Animal");

        imageView = findViewById(R.id.image_view);

        Resources res = getResources();
        String user = FirebaseAuth.getInstance().getCurrentUser().getDisplayName().toString();
        TextView description = findViewById(R.id.description);
        String descriptionText = String.format(res.getString(R.string.add_animal_description), user);
        description.setText(descriptionText);
    }

    /**
     * When either the submit button or the cancel buttons are clicked...
     * @param v
     */
    @Override
    public void onClick (View v) {
        switch (v.getId())
        {
            case R.id.submit_button:
                addAnimal();
                break;
            case R.id.cancel_button:
                finish();
                break;
            case R.id.image_button:
                getImage();
                break;
        }
    }

    /**
     * Open the phone gallery to retrieve an image for the animal.
     */
    public void getImage()
    {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    /**
     * When the selecting image activity is completed, go in here and execute this code.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            try {
                /**final Uri */ imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(selectedImage);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this.getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this.getApplicationContext(), "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Code to upload the selected image to the Firebase database.
     * @param imageName             the name of the location to save the image at (equal to residentID)
     */
    public void uploadImage (String imageName)
    {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child(imageName);

        if (imageUri != null) {
            UploadTask uploadTask = imageRef.putFile(imageUri);
            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                }
            });
        }
        else
        {
            
        }


    }

    /**
     * Add a new animal to the database by retrieving the information from the EditText components.
     */
    public void addAnimal()
    {
        boolean emptyFields = false;
        String name = nameEditText.getText().toString();
        emptyFields = name.trim().equals("");
        String enclosure = locationSpinner.getSelectedItem().toString();
        emptyFields = emptyFields || enclosure.equals("Select enclosure");
        String species = speciesSpinner.getSelectedItem().toString();
        emptyFields = emptyFields || enclosure.equals("Select species");
        Resident resident = new Resident();
        resident.setName(name);
        resident.setEnclosure(enclosure);
        resident.setSpecies(species);

        newResidentID = species.toLowerCase() + "-" + Utils.getRandomID();

        resident.setPhoto(newResidentID);

        // testing this new thing
        uploadImage(newResidentID);

        // if any of the fields are empty, show a snackbar telling the user to fill them in.
        if (emptyFields) {
            Snackbar empty = Snackbar.make(findViewById(android.R.id.content),
                    "Please fill in all information.",
                    Snackbar.LENGTH_SHORT);
            empty.show();
        } else {
            FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID).set(resident);
            addTasks(species);
            hideKeyboard();
            finish();
        }
    }

    /**
     * Autopopulate the tasks according to...
     * @param species               the species of the new animal being added
     */
    public void addTasks(String species)
    {
        /**
        // make and add the feed task
        Task feedTask = new Task("CARE", 0, "Feed", 1, "");
        String taskId = "feed-" + Utils.getRandomID();
        FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                .collection("Tasks").document(taskId).set(feedTask);

        // make and add the behavior task
        Task behaviorTask = new Task("ALERT", 0, "Behavior", 1, "");
        taskId = "behavior-" + Utils.getRandomID();
        FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                .collection("Tasks").document(taskId).set(behaviorTask);
                **/

        String taskId = "";

        // Add the report abnormal behavior tab for everyone.

        Task behaviorTask = new Task("CARE", 0, "Behavior", 1, "", "Report abnormal behavior.");
        taskId = "behavior-" + Utils.getRandomID();
        FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                .collection("Tasks").document(taskId).set(behaviorTask);

        switch (species) {
            case "Rat":
                /**
                 * ​Rats, Jake and Marcus:
                 * CARE
                 * Daily: feed pellets, feed fruit/veggies (see “safe list”), tidy house, wipe shelves, check water (and refill with filtered water if needed)
                 * Every 4-5 days: replace bedding
                 * Every 9-10 days: wash out house, wash out water bottle (and it's obviously good to replace the bedding at this time)
                 * ENRICHMENT
                 * Daily: play, exercise, hold
                 */
                Task feedPelletsRat = new Task("CARE", 0, "Feed", 1, "", "Feed pellets.");
                taskId = "feed-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(feedPelletsRat);

                Task feedFruitRat = new Task("CARE", 0, "Feed", 1, "", "Feed fruit/veggies.");
                taskId = "feed-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(feedFruitRat);

                /**
                 * Changes to use the CleanTask class.
                 */

                Task cleanTaskRat1 = new CleanTask("CARE", 0, "Clean", 4, "", "Tidy house, wipe shelves, check water.", "enclosure");
                taskId = "clean-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(cleanTaskRat1);

                Task cleanTaskRat2 = new CleanTask("CARE", 0, "Clean", 4, "", "Replace bedding.", "enclosure");
                taskId = "clean-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(cleanTaskRat2);

                Task cleanTaskRat3 = new CleanTask("CARE", 0, "Clean", 9, "", "Wash out house & water bottle.", "enclosure");
                taskId = "clean-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(cleanTaskRat3);

                /**
                 *
                 */

                Task exerciseTaskRat = new Task("ENRICH", 0, "Exercise", 1, "", "Play, exercise, hold.");
                taskId = "exercise-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(exerciseTaskRat);
                break;
            case "Lizard":
                /**
                 * Alligator Lizard, Diego:
                 * CARE
                 * Daily: wash water dish and refill with filtered water, mist with water, feed crickets (dusted with calcium​ containing d3​)
                 * Every 2-3 months: full clean
                 * ENRICHMENT
                 * every 2-3 days: rearrange enclosure
                 */
                Task feedCricketsLizard = new Task("CARE", 0, "Feed", 1, "", "Feed crickets.");
                taskId = "feed-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(feedCricketsLizard);

                /** Change this to use CleanTask class. */

                Task cleanTaskLizard = new CleanTask("CARE", 0, "Clean", 1, "", "Clean (water dish).", "enclosure");
                taskId = "clean-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(cleanTaskLizard);

                Task cleanTaskLizard2 = new CleanTask("CARE", 0, "Clean", 1, "", "Humid hide.", "hide");
                taskId = "clean-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(cleanTaskLizard2);

                /**
                 *
                 */

                Task deepTaskLizard = new Task("CARE", 0, "Clean", 60, "", "Deep clean.");
                taskId = "clean-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(deepTaskLizard);

                Task enclosureTaskLizard = new Task("ENRICH", 0, "Enclosure Enrichment", 2, "", "Rearrange enclosure.");
                taskId = "enrich-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(enclosureTaskLizard);
                break;

            case "Toad":
                /**
                 * Western Toad, Rocky:
                 * CARE
                 * Daily:  wash water dish and refill with filtered water, mist with water, feed crickets (dusted with calcium containing d3)
                 * Every 2-3 months: full clean
                 * ENRICHMENT
                 * Daily: outside time (limit to 15-20 minutes max. Make sure to have a water source available such as a squirt bottle to ensure toad does not dry out)
                 * Note: Always check for predators while outside
                 * every 2-3 days: rearrange enclosure
                 */
                Task feedCricketsToad = new Task("CARE", 0, "Feed", 1, "", "Feed crickets.");
                taskId = "feed-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(feedCricketsToad);

                Task cleanTaskToad = new Task("CARE", 0, "Clean", 1, "", "Clean (water dish, mist).");
                taskId = "clean-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(cleanTaskToad);

                Task deepTaskToad = new Task("CARE", 0, "Clean", 60, "", "Deep clean.");
                taskId = "clean-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(deepTaskToad);

                Task exerciseTaskToad = new Task("ENRICH", 0, "Exercise", 1, "", "Outside for exercise (15-20 min max).");
                taskId = "exercise-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(exerciseTaskToad);

                Task enclosureTaskToad = new Task("ENRICH", 0, "Enclosure Enrichment", 2, "", "Rearrange enclosure.");
                taskId = "enrich-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(enclosureTaskToad);
                break;

            case "Turtle":
                /**
                 * Mississippi Map Turtle, Jackson & Mississippi Mud Turtle, Madison:
                 * CARE
                 * Daily: feed veggies and note which, feed pellets, remove any detritus/uneaten food from water
                 * Every 2-3 days: feed treat (meal worm (gutloaded), cricket, etc.)
                 * Every 2-3 months: full clean
                 * ENRICHMENT
                 * Daily: outside time Note: Always check for predators while outside
                 */
                Task feedPelletsTurtle = new Task("CARE", 0, "Feed", 1, "", "Feed pellets.");
                taskId = "feed-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(feedPelletsTurtle);

                Task feedFruitTurtle = new Task("CARE", 0, "Feed", 1, "", "Feed fruit/veggies.");
                taskId = "feed-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(feedFruitTurtle);

                Task feedTreatTurtle = new Task("CARE", 0, "Feed", 2, "", "Feed treat (mealworm, cricket, etc.)");
                taskId = "feed-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(feedTreatTurtle);

                Task cleanTaskTurtle = new Task("CARE", 0, "Clean", 1, "", "Clean (remove detritus, food from water).");
                taskId = "clean-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(cleanTaskTurtle);

                Task deepTaskTurtle = new Task("CARE", 0, "Clean", 60, "", "Deep clean.");
                taskId = "clean-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(deepTaskTurtle);
                break;

            case "Snake":
                /**
                 * King Snake, Capitan:
                 * CARE
                 * Daily: remove feces/urates/shed, wash water dish and refill with filtered water, mist with water
                 * Weekly on Fridays: feed mouse
                 * Every 2-3 months: full clean
                 * ENRICHMENT:
                 * Daily: outside time Note: Always check for predators while outside
                 * IMPORTANT: Friday must be BEFORE eating, DO NOT take out on Sat/Sun
                 * every 2-3 days: rearrange enclosure
                 * Note: If in preshed, put damp humidity box into enclosure and velcro ‘snake in preshed’ sign on outside
                 */
                Task cleanTaskSnake = new Task("CARE", 0, "Clean", 1, "", "Clean (remove feces, water dish, mist).");
                taskId = "clean-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(cleanTaskSnake);

                Task feedMouseSnake = new Task("CARE", 0, "Feed", 7, "", "Feed mouse.");
                taskId = "feed-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(feedMouseSnake);

                Task deepTaskSnake = new Task("CARE", 0, "Clean", 60, "", "Deep clean.");
                taskId = "clean-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(deepTaskSnake);

                Task exerciseTaskSnake = new Task("ENRICH", 0, "Exercise", 1, "", "Outside for exercise (check for predators).");
                taskId = "exercise-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(exerciseTaskSnake);

                Task enclosureTaskSnake = new Task("ENRICH", 0, "Enclosure Enrichment", 2, "", "Rearrange enclosure.");
                taskId = "enrich-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(enclosureTaskSnake);
                break;
            case "Crayfish":
                /**
                 * Crayfish
                 * Front, Cordelia:
                 * CARE
                 * Daily: feed 2-3 pellets (can occasionally give worms, lettuce, etc. as a treat; remove uneaten food)
                 * every 2 weeks: gravel vac, 25% water change
                 * Back, no names (11):
                 * CARE
                 * Daily: feed ~20 pellets  (can occasionally give worms, lettuce, etc. as a treat; remove uneaten food)
                 * Every other day: check filter & replace when needed
                 * weekly: gravel vac, 25%  water change
                 */
                Task feedPelletsCrayfish = new Task("CARE", 0, "Feed", 1, "", "Feed 20 pellets.");
                taskId = "feed-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(feedPelletsCrayfish);

                Task feedTreatCrayfish = new Task("CARE", 0, "Feed", 7, "", "Feed treat (mealworm, cricket, etc.)");
                taskId = "feed-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(feedTreatCrayfish);

                Task cleanTaskCrayfish = new Task("CARE", 0, "Clean", 2, "", "Clean (check filter & replace).");
                taskId = "clean-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(cleanTaskCrayfish);

                Task deepTaskCrayfish = new Task("CARE", 0, "Clean", 60, "", "Deep clean (gravel vac, 25% water change).");
                taskId = "clean-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(deepTaskCrayfish);
                break;
            case "Goldfish":
                /**
                 * Gold fish:
                 * CARE
                 * Daily: feed pinch of flakes (NOTE no worms)
                 * When needed: scrape algae
                 */
                Task feedFlakesFish = new Task("CARE", 0, "Feed", 1, "", "Feed pinch of flakes.");
                taskId = "feed-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(feedFlakesFish);

                Task cleanTaskFish = new Task("CARE", 0, "Clean", 1, "", "Clean (scrape algae).");
                taskId = "clean-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(cleanTaskFish);
                break;
            case "Roach Fish":
                /**
                 * California Roach fish:
                 * CARE
                 * Daily: feed large pinch of flakes  (can occasionally give worms as a treat)
                 */
                Task feedFatFlakesFish = new Task("CARE", 0, "Feed", 1, "", "Feed large pinch of flakes.");
                taskId = "feed-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(feedFatFlakesFish);

                Task feedTreatFish = new Task("CARE", 0, "Feed", 7, "", "Feed treat (worms)");
                taskId = "feed-" + Utils.getRandomID();
                FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                        .collection("Tasks").document(taskId).set(feedTreatFish);
                break;
        }

    }

    // Code to hide the keyboard.
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}
