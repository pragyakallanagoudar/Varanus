package com.pragyakallanagoudar.varanus.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.pragyakallanagoudar.varanus.R;
import com.pragyakallanagoudar.varanus.model.Resident;
import com.pragyakallanagoudar.varanus.model.log.TextLog;
import com.pragyakallanagoudar.varanus.utilities.Utils;
import com.pragyakallanagoudar.varanus.model.Task;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
                Log.e(TAG, "the cancel button is being clicked!!");
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
            Log.e(TAG, "we're in here baby boo");
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
        // make and add the feed task
        Task feedTask = new Task("CARE", 0, "Feed", "daily", "");
        String taskId = "feed-" + Utils.getRandomID();
        FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                .collection("Tasks").document(taskId).set(feedTask);

        // make and add the behavior task
        Task behaviorTask = new Task("ALERT", 0, "Behavior", "daily", "");
        taskId = "behavior-" + Utils.getRandomID();
        FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                .collection("Tasks").document(taskId).set(behaviorTask);

        // make and add the clean task
        Task cleanTask = new Task("CARE", 0, "Clean", "daily", "");
        taskId = "clean-" + Utils.getRandomID();
        FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                .collection("Tasks").document(taskId).set(cleanTask);
        // Complete this after consulting Lauren's guide.
        Task enclosureTask = new Task("ENRICH", 0, "Enclosure Enrichment", "daily", "");
        Task exercise = new Task("ENRICH", 0, "Exercise", "daily", "");

    }

    // Code to hide the keyboard.
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
