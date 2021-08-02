package com.sensofttakimi.sensoft;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.jetbrains.annotations.NotNull;

public class SimpleFragmentPageAdapter extends FragmentStatePagerAdapter {


    public SimpleFragmentPageAdapter(@NonNull @NotNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new FragmentHakkindaSensoftNedir();
            case 1:
                return new FragmentHakkindaTakim();
            default:
                return new FragmentHakkindaSensoftNedir();

        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return "HAKKINDA";
            case 1:
                return "TAKIM";
            default:
                return "HAKKINDA";
        }
    }
}
