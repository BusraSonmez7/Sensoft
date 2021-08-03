package com.sensofttakimi.sensoft.FragmentUygulama;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.sensofttakimi.sensoft.Bildirimler.BildirimAdapter;
import com.sensofttakimi.sensoft.Bildirimler.BildirimEklemeActivity;
import com.sensofttakimi.sensoft.Bildirimler.Bildirimler;
import com.sensofttakimi.sensoft.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnasayfaFragment extends Fragment {

    private FloatingActionButton btnekle;

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;

    ArrayList<Bildirimler> bildirimlerArrayList;
    private View mview;
    private RecyclerView recylerView;

    BildirimAdapter bildirimAdapter;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mview = inflater.inflate(R.layout.fragment_anasayfa,container,false);
        recylerView = mview.findViewById(R.id.recylerView);
        this.btnekle = mview.findViewById(R.id.eklebuton);

        bildirimlerArrayList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        getData();

        recylerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        bildirimAdapter = new BildirimAdapter(bildirimlerArrayList);
        recylerView.setAdapter(bildirimAdapter);

        btnekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BildirimEklemeActivity.class);
                startActivity(intent);
            }
        });

        return mview;
       // return inflater.inflate(R.layout.fragment_anasayfa,container,false);
    }

    private void getData(){
        firebaseFirestore.collection("Bildirimler").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error !=null){
                    Toast.makeText(getActivity(),error.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                }

                if(value != null){
                    for(DocumentSnapshot snapshot : value.getDocuments()){
                        HashMap<String,Object> data = (HashMap<String, Object>) snapshot.getData();
                        String baslik = (String)  data.get("baslik");
                        String resim = (String)  data.get("resim");
                        String tarih = (String)  data.get("tarih");

                        Bildirimler bildirimler = new Bildirimler(baslik,resim,tarih);
                        bildirimlerArrayList.add(bildirimler);
                    }

                    bildirimAdapter.notifyDataSetChanged();
                }
            }
        });
    }


}
