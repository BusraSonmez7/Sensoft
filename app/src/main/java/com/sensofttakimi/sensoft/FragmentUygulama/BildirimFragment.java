package com.sensofttakimi.sensoft.FragmentUygulama;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
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
import com.sensofttakimi.sensoft.Bildirimler.GelenBildirimAdapter;
import com.sensofttakimi.sensoft.FragmentMesaj.SohbetiKaydetActivity;
import com.sensofttakimi.sensoft.Model.Bildirimler;
import com.sensofttakimi.sensoft.Model.Sohbetler;
import com.sensofttakimi.sensoft.R;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class BildirimFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    ArrayList<Bildirimler> bildirimlerArrayList;
    private View mview;
    private View view;
    private RecyclerView recylerView;

    GelenBildirimAdapter bildirimAdapter;


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        mview = inflater.inflate(R.layout.fragment_bildirim,container,false);
        this.recylerView = mview.findViewById(R.id.recylerView);

        bildirimlerArrayList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recylerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        bildirimAdapter = new GelenBildirimAdapter(bildirimlerArrayList,getActivity());
        recylerView.setAdapter(bildirimAdapter);
        getData();

        return mview;

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
                            String icerik = (String) data.get("aciklama");
                            if(data.get("tarih") != null){
                                Timestamp time = (Timestamp) data.get("tarih");
                                Date date = time.toDate();
                                String tarih = "dd/MM/yyyy";
                                String saat = "kk:mm";
                                SimpleDateFormat format = new SimpleDateFormat(tarih);
                                SimpleDateFormat format2 = new SimpleDateFormat(saat);
                                Bildirimler bildirimler = new Bildirimler(baslik, icerik, email, resim, format.format(date), format2.format(date));
                                bildirimlerArrayList.add(bildirimler);
                            }
                        }

                    }

                    bildirimAdapter.notifyDataSetChanged();
                }
            }
        });
    }





}
