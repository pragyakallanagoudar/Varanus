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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Security.insertProviderAt(Conscrypt.newProvider(), 1);

        setContentView(R.layout.activity_tasks_tabbed);

        tabLayout =  findViewById(R.id.tablayout_id);
        viewPager =  findViewById(R.id.viewpager_id);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        //Add fragment here

        adapter.AddFragment(new FragmentActiveTasks(), "Active");
        adapter.AddFragment(new FragmentCompletedTasks(), "Completed");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}

