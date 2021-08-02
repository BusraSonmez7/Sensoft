package com.sensofttakimi.sensoft;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class SensoftHakkinda extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensoft_hakkinda);

    }
}