package com.sensofttakimi.sensoft.FragmentMesaj;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.jetbrains.annotations.NotNull;

public class SimpleFragmentPageMesajAdapter extends FragmentStatePagerAdapter {


    public SimpleFragmentPageMesajAdapter(@NonNull @NotNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new FragmentSohbetler();
            case 1:
                return new FragmentMesajlasma();
            default:
                return new FragmentSohbetler();

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
                return "SOHBETLER";
            case 1:
                return "MESAJLAŞMA";
            default:
                return "SOHBETLER";
        }
    }
}
