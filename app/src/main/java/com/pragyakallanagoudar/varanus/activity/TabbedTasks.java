package com.pragyakallanagoudar.varanus.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pragyakallanagoudar.varanus.R;
import com.pragyakallanagoudar.varanus.adapter.ViewPagerAdapter;
import com.pragyakallanagoudar.varanus.utilities.Utils;

import org.conscrypt.Conscrypt;

import java.security.Security;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

/**
 * The TabbedTasks class holds both the active tasks and the animal's profile in a tabbed view.
 */
public class TabbedTasks extends AppCompatActivity {

    private TabLayout tabLayout; // Two tabs: currents Active and Completed
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    private String residentID;
    private String residentName;

    private FirebaseFirestore mFirestore;

    // Resident ID retrieved from Intent
    public static final String RESIDENT_ID = "resident_id";
    public static final String RESIDENT_NAME = "resident_name";

    public static final String TAG = TabbedTasks.class.getSimpleName();

    /**
     * Instantiate the field variables.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Security.insertProviderAt(Conscrypt.newProvider(), 1);
        setContentView(R.layout.activity_tasks_tabbed);

        // local variable version of RESIDENT_ID
        residentID = getIntent().getExtras().getString(RESIDENT_ID);
        residentName = getIntent().getExtras().getString(RESIDENT_NAME);

        tabLayout =  findViewById(R.id.tablayout_id);
        viewPager =  findViewById(R.id.viewpager_id);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(residentName);

        // title = findViewById(R.id.title);
        // title.setText(residentName);

        // set up View Pager adapter and add it
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentActiveTasks(residentID, residentName), "Tasks");
        adapter.addFragment(new AnimalDetailGraph(residentID, residentName), "Profile");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        mFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Utils.adminControls) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_animal, menu);
        }
        return true;
    }

    /**
     * When an item is selected from the menu, go in here.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set up the input
        final EditText input = new EditText(this);

        builder.setTitle("Enter the name: " + residentName);

        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e(TAG, "onClick()");
                if(input.getText().toString().equals(residentName)) {
                    Log.e(TAG, "YO 7/5/2020");
                    mFirestore.collection("Guadalupe Residents").document(residentID).delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error deleting document", e);
                                }
                            });

                } else {
                    Log.e(TAG, "NO 7/5/2020");
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                if(input.getText().equals(residentName))
                    ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                else
                    ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }
        });

        builder.show();

        return super.onOptionsItemSelected(item);
    }
}

