package com.sensofttakimi.sensoft.FragmentMesaj;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sensofttakimi.sensoft.Bildirimler.BildirimAdapter;
import com.sensofttakimi.sensoft.Model.SohbetIcerigi;
import com.sensofttakimi.sensoft.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SohbetMesajlariActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;

    SohbetIcerikAdapter sohbetIcerikAdapter;
    List<SohbetIcerigi> sohbetIcerigis;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sohbet_mesajlari);


        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        recyclerView = (RecyclerView) findViewById(R.id.r1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        Intent intent = getIntent();
        String sohbetID = (String) intent.getStringExtra("sohbetID");
        String baslik = (String) intent.getStringExtra("baslik");

        if(sohbetID != null){
            MesajGoster(baslik);
        }
    }

    public void MesajGoster(String baslik){
        sohbetIcerigis = new ArrayList<>();
        firebaseFirestore.collection("SohbetIcerigi").orderBy("mesaj_tarih", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error !=null){
                    Toast.makeText(getApplicationContext(),error.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                }

                if(value != null){
                    FirebaseUser user = auth.getCurrentUser();
                    String email = user.getEmail();
                    sohbetIcerigis.clear();
                    for(DocumentSnapshot snapshot : value.getDocuments()){
                        HashMap<String,Object> data = (HashMap<String, Object>) snapshot.getData();
                        if(email.equals(data.get("kullanici").toString()) && baslik.equals(data.get("sohbet_baslik").toString())){
                            String mesaj = (String)  data.get("mesaj");
                            String rol = (String)  data.get("rol");

                            SohbetIcerigi sohbetIcerigi = new SohbetIcerigi(email,baslik,mesaj,rol);
                            sohbetIcerigis.add(sohbetIcerigi);
                        }
                        if(!sohbetIcerigis.isEmpty()){
                            sohbetIcerikAdapter = new SohbetIcerikAdapter(SohbetMesajlariActivity.this,sohbetIcerigis);
                            recyclerView.setAdapter(sohbetIcerikAdapter);
                        }

                    }
                }
            }
        });
    }
}