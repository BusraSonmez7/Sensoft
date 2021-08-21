package com.sensofttakimi.sensoft.FragmentMesaj;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.sensofttakimi.sensoft.Bildirimler.BildirimEklemeActivity;
import com.sensofttakimi.sensoft.KullaniciGirisi;
import com.sensofttakimi.sensoft.Model.Bildirimler;
import com.sensofttakimi.sensoft.Model.SohbetIcerigi;
import com.sensofttakimi.sensoft.R;
import com.sensofttakimi.sensoft.UygulamaSayfasi;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class FragmentMesajlasma extends Fragment {
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private View mview;
    private RecyclerView recyclerView;
    private ImageButton btnmetin, btnses;
    private EditText edtmesaj, edtses;

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;

    SohbetIcerikAdapter sohbetIcerikAdapter;
    List<SohbetIcerigi> sohbetIcerigis;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mview = inflater.inflate(R.layout.fragment_mesajlasma,container,false);
        this.recyclerView = mview.findViewById(R.id.recyler_view);
        this.btnmetin = mview.findViewById(R.id.btn_gonder);
        this.btnses = mview.findViewById(R.id.btn_gonder2);
        this.edtmesaj = mview.findViewById(R.id.mesaj_gonder1);
        this.edtses = mview.findViewById(R.id.mesaj_ses);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        recyclerView = mview.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        btnmetin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mesaj = edtmesaj.getText().toString();
                if(!mesaj.isEmpty()){
                    MesajGonder("kaydedilmedi",mesaj,"metin");
                }
                else {
                    Toast.makeText(getActivity(),"Boş mesaj gönderemezsiniz",Toast.LENGTH_LONG).show();
                }
                edtmesaj.setText("");
            }
        });

        btnses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speek();
                String ses = edtses.getText().toString();
                if(!ses.isEmpty()){
                    MesajGonder("kaydedilmedi",ses,"ses");
                }
                else {
                }
                edtses.setText("");
            }
        });

        MesajGoster("kaydedilmedi");
        return mview;

    }

    private void speek(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Karşınızdakini dinlemek için tıklayın");
        try {
            startActivityForResult(intent,REQUEST_CODE_SPEECH_INPUT);

        }catch (Exception e){

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_SPEECH_INPUT:
                if(resultCode == RESULT_OK && data!=null){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    edtses.setText(result.get(0));
                    String ses = edtses.getText().toString();
                    if(!ses.isEmpty()){
                        MesajGonder("kaydedilmedi",ses,"ses");
                    }
                    else {
                    }
                    edtses.setText("");
                }
                break;
        }
    }

    private void MesajGonder(String baslik, String mesaj, String rol){
        Date simdikiZaman = new Date();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat df2 = new SimpleDateFormat("kk:mm");
        System.out.println(df2.format(simdikiZaman));

        FirebaseUser user = auth.getCurrentUser();
        String email = user.getEmail();

        HashMap<String,Object> mesajVeri = new HashMap<>();
        mesajVeri.put("sohbet_baslik",baslik);
        mesajVeri.put("mesaj",mesaj);
        mesajVeri.put("kullanici",email);
        mesajVeri.put("mesaj_tarih", FieldValue.serverTimestamp());
        mesajVeri.put("rol",rol);


        firebaseFirestore.collection("SohbetIcerigi").add(mesajVeri).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getActivity(),"Mesaj gönderildi",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getActivity(),"Mesaj gönderilemedi",Toast.LENGTH_LONG).show();

            }
        });
    }

    private void SesGonder(String baslik, String mesaj,String rol){
        Date simdikiZaman = new Date();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat df2 = new SimpleDateFormat("kk:mm");
        System.out.println(df2.format(simdikiZaman));

        FirebaseUser user = auth.getCurrentUser();
        String email = user.getEmail();

        HashMap<String,Object> mesajVeri = new HashMap<>();
        mesajVeri.put("sohbet_baslik",baslik);
        mesajVeri.put("mesaj",mesaj);
        mesajVeri.put("kullanici",email);
        mesajVeri.put("mesaj_tarih", FieldValue.serverTimestamp());
        mesajVeri.put("rol",rol);


        firebaseFirestore.collection("SohbetIcerigi").add(mesajVeri).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getActivity(),"Mesaj gönderildi",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getActivity(),"Mesaj gönderilemedi",Toast.LENGTH_LONG).show();

            }
        });
    }

    public void MesajGoster(String baslik){
        sohbetIcerigis = new ArrayList<>();
        firebaseFirestore.collection("SohbetIcerigi").orderBy("mesaj_tarih", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error !=null){
                    Toast.makeText(getActivity(),error.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
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
                            sohbetIcerikAdapter = new SohbetIcerikAdapter(getActivity(),sohbetIcerigis);
                            recyclerView.setAdapter(sohbetIcerikAdapter);
                        }

                    }
                }
            }
        });
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // TODO Add your menu entries here
        inflater.inflate(R.menu.menu_mesajlasma, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.kaydet:
                Intent intent = new Intent(getActivity(),SohbetiKaydetActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return true;

    }
}
