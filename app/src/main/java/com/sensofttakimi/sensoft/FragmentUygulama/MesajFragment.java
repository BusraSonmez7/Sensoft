package com.sensofttakimi.sensoft.FragmentUygulama;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.sensofttakimi.sensoft.FragmentHakkinda.SimpleFragmentPageAdapter;
import com.sensofttakimi.sensoft.FragmentMesaj.SimpleFragmentPageMesajAdapter;
import com.sensofttakimi.sensoft.R;

import org.jetbrains.annotations.NotNull;

public class MesajFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private View mview;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mview = inflater.inflate(R.layout.fragment_mesaj,container,false);
        tabLayout = mview.findViewById(R.id.tabLayout);
        viewPager = mview.findViewById(R.id.view_pager);

        SimpleFragmentPageMesajAdapter adapter = new SimpleFragmentPageMesajAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        return mview;
    }
}
