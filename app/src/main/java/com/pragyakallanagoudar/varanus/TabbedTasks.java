package com.pragyakallanagoudar.varanus;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.pragyakallanagoudar.varanus.adapter.ViewPagerAdapter;

import org.conscrypt.Conscrypt;

import java.security.Security;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;


public class TabbedTasks extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    public static final String KEY_RESIDENT_ID = "key_resident_id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Security.insertProviderAt(Conscrypt.newProvider(), 1);
        String residentId = getIntent().getExtras().getString(KEY_RESIDENT_ID);
        setContentView(R.layout.activity_tasks_tabbed);

        tabLayout =  findViewById(R.id.tablayout_id);
        viewPager =  findViewById(R.id.viewpager_id);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        //Add fragment here

        adapter.AddFragment(new FragmentActiveTasks(residentId), "Active");
        adapter.AddFragment(new FragmentCompletedTasks(residentId), "Completed");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}

