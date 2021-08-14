package com.sensofttakimi.sensoft.Bildirimler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.sensofttakimi.sensoft.R;

import java.util.ArrayList;
import java.util.HashMap;

public class BildirimIcerigi extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    ArrayList<Bildirimler> bildirimlerArrayList;
    EditText baslik, kelime, icerik;
    String baslikID;
    TextView tarih;
    ImageView resim;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bildirim_icerigi);
        baslik = findViewById(R.id.baslik);
        kelime = findViewById(R.id.kelime);
        icerik = findViewById(R.id.icerik);
        tarih = findViewById(R.id.tarih);
        resim = findViewById(R.id.image);


        Intent intent = getIntent();
        baslikID = intent.getStringExtra("baslik");

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        bildirimlerArrayList = new ArrayList<>();


        firebaseFirestore.collection("Bildirimler").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error !=null){
                }

                if(value != null){
                    FirebaseUser user = auth.getCurrentUser();
                    String email = user.getEmail();
                    int i = 0;
                    for(DocumentSnapshot snapshot : value.getDocuments()){
                        HashMap<String,Object> data = (HashMap<String, Object>) snapshot.getData();
                        if(email.equals(data.get("kullanici").toString())){
                            if(baslikID.equals(data.get("baslik"))){
                                String baslik = (String)  data.get("baslik");
                                String resim = (String)  data.get("resim");
                                String tarih = (String)  data.get("tarih");
                                String aciklama = (String)  data.get("aciklama");
                                String kelime = (String) data.get("ses");

                                Bildirimler bildirimler = new Bildirimler(baslik, resim, aciklama, tarih,kelime);
                                bildirimlerArrayList.add(bildirimler);
                            }
                        }

                    }
                    if(!bildirimlerArrayList.isEmpty()){
//                        String resimString = (String) bildirimlerArrayList.get(0).resim;
//                        byte [] encodeByte = Base64.decode(resimString,Base64.DEFAULT);
//                        Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

                        baslik.setText(bildirimlerArrayList.get(0).baslik.toUpperCase());
                        icerik.setText(bildirimlerArrayList.get(0).aciklama);
                        kelime.setText(bildirimlerArrayList.get(0).kelime);
                        tarih.setText(bildirimlerArrayList.get(0).tarih);
                        resim.setImageResource(R.drawable.notification);

                    }else {
                        Toast.makeText(getApplicationContext(),"içerik bulunamadı!",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });




    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public String slm;
    public void getData(){
//        firebaseFirestore.collection("Bildirimler").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if(error !=null){
//                }
//
//                if(value != null){
//                    FirebaseUser user = auth.getCurrentUser();
//                    String email = user.getEmail();
//                    int i = 0;
//                    for(DocumentSnapshot snapshot : value.getDocuments()){
//                        HashMap<String,Object> data = (HashMap<String, Object>) snapshot.getData();
//                        if(email.equals(data.get("kullanici").toString())){
//                            if(baslikID.equals(data.get("baslik"))){
//                                String baslik = (String)  data.get("baslik");
//                                String resim = (String)  data.get("resim");
//                                String tarih = (String)  data.get("tarih");
//                                String aciklama = (String)  data.get("aciklama");
//
//                                Bildirimler bildirimler = new Bildirimler(baslik, resim, aciklama, tarih);
//                                bildirimlerArrayList.add(bildirimler);
//                                slm = "string metin çalıştırıldı";
//
//                                Toast.makeText(getApplicationContext(),"Girdi"+" "+baslikID+ " "+bildirimlerArrayList.isEmpty(),Toast.LENGTH_LONG).show();
//
//                            }
//                        }
//
//                        break;
//
//                    }
//                }
//            }
//        });
//
////        baslik.setText(bildirimlerArrayList.get(0).baslik);
////        icerik.setText(bildirimlerArrayList.get(0).aciklama);
////        kelime.setText(bildirimlerArrayList.get(0).kelime);
////        tarih.setText(bildirimlerArrayList.get(0).tarih);
    }
}