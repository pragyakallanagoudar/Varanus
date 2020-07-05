package com.pragyakallanagoudar.varanus.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.pragyakallanagoudar.varanus.R;
import com.pragyakallanagoudar.varanus.adapter.ViewPagerAdapter;

import org.conscrypt.Conscrypt;

import java.security.Security;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

/**
 * Class description to come here.
 */
public class TabbedTasks extends AppCompatActivity {

    private TabLayout tabLayout; // Two tabs: currents Active and Completed
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private TextView title;

    // Resident ID retrieved from Intent
    public static final String RESIDENT_ID = "resident_id";
    public static final String RESIDENT_NAME = "resident_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Security.insertProviderAt(Conscrypt.newProvider(), 1);
        setContentView(R.layout.activity_tasks_tabbed);

        // local variable version of RESIDENT_ID
        String residentID = getIntent().getExtras().getString(RESIDENT_ID);
        String residentName = getIntent().getExtras().getString(RESIDENT_NAME);

        tabLayout =  findViewById(R.id.tablayout_id);
        viewPager =  findViewById(R.id.viewpager_id);
        title = findViewById(R.id.title);
        title.setText(residentName);

        // set up View Pager adapter and add it
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentActiveTasks(residentID, residentName), "Tasks");
        adapter.addFragment(new AnimalDetailGraph(residentID, residentName), "Profile");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}

