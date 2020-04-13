package com.pragyakallanagoudar.varanus;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
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
    private ViewPager viewPager; // ??? -> swipe view?
    private ViewPagerAdapter adapter;

    // Resident ID retrieved from Intent
    public static final String RESIDENT_ID = "resident_id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Security.insertProviderAt(Conscrypt.newProvider(), 1);
        setContentView(R.layout.activity_tasks_tabbed);

        // local variable version of RESIDENT_ID
        String residentId = getIntent().getExtras().getString(RESIDENT_ID);

        tabLayout =  findViewById(R.id.tablayout_id);
        viewPager =  findViewById(R.id.viewpager_id);

        // set up View Pager adapter and add it
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentActiveTasks(residentId), "Active");
        adapter.addFragment(new FragmentCompletedTasks(residentId), "Completed");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}

