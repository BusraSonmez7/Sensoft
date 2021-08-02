package com.sensofttakimi.sensoft;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.jetbrains.annotations.NotNull;

public class UygulamaSayfasiAdapter extends FragmentStatePagerAdapter {


    public UygulamaSayfasiAdapter(@NonNull @NotNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new AnasayfaFragment();
            case 1:
                return new BildirimFragment();
            case 2:
                return new MesajFragment();
            case 3:
                return new HakkindaFragment();

            default:
                return new AnasayfaFragment();



        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
