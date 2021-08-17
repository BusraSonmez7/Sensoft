package com.sensofttakimi.sensoft.FragmentUygulama;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sensofttakimi.sensoft.Bildirimler.BildirimAdapter;
import com.sensofttakimi.sensoft.Bildirimler.BildirimEklemeActivity;
import com.sensofttakimi.sensoft.Bildirimler.BildirimIcerigi;
import com.sensofttakimi.sensoft.Model.Bildirimler;
import com.sensofttakimi.sensoft.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class AnasayfaFragment extends Fragment {

    private FloatingActionButton btnekle;

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    ArrayList<Bildirimler> bildirimlerArrayList;
    private View mview;
    private View view;
    private RecyclerView recylerView;

    BildirimAdapter bildirimAdapter;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mview = inflater.inflate(R.layout.fragment_anasayfa,container,false);
        this.recylerView = mview.findViewById(R.id.recylerView);
        this.btnekle = mview.findViewById(R.id.eklebuton);

        bildirimlerArrayList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        getData();

        recylerView.setLayoutManager(new GridLayoutManager(getActivity(),2,GridLayoutManager.VERTICAL,false));
        bildirimAdapter = new BildirimAdapter(bildirimlerArrayList,getActivity());
        recylerView.setAdapter(bildirimAdapter);

        btnekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BildirimEklemeActivity.class);
                startActivity(intent);
            }
        });
        recylerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),BildirimIcerigi.class);
                startActivity(intent);
            }
        });

        return mview;
       // return inflater.inflate(R.layout.fragment_anasayfa,container,false);
    }

    private void getData(){
        firebaseFirestore.collection("Bildirimler").orderBy("tarih", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error !=null){
                    Toast.makeText(getActivity(),error.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                }

                if(value != null){
                    FirebaseUser user = auth.getCurrentUser();
                    String email = user.getEmail();
                    bildirimlerArrayList.clear();
                    for(DocumentSnapshot snapshot : value.getDocuments()){
                        HashMap<String,Object> data = (HashMap<String, Object>) snapshot.getData();
                        if(email.equals(data.get("kullanici").toString())){
                            String baslik = (String)  data.get("baslik");
                            String resim = (String)  data.get("resim");

                            Bildirimler bildirimler = new Bildirimler(baslik, resim);
                            bildirimlerArrayList.add(bildirimler);
                        }

                    }

                    bildirimAdapter.notifyDataSetChanged();
                }
            }
        });
    }


}
