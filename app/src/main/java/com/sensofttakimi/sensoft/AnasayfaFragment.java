package com.sensofttakimi.sensoft;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sensofttakimi.sensoft.R;

import org.jetbrains.annotations.NotNull;

public class AnasayfaFragment extends Fragment {

    private ListView listView;
    private View view2;
    private FloatingActionButton btnekle;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        this.view2 = inflater.inflate(R.layout.fragment_anasayfa,container,false);
        this.btnekle = view2.findViewById(R.id.eklebuton);
        btnekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),BildirimEklemeActivity.class);
                startActivity(intent);
            }
        });

        return view2;
       // return inflater.inflate(R.layout.fragment_anasayfa,container,false);
    }


}
