package com.sensofttakimi.sensoft.FragmentMesaj;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
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
import com.sensofttakimi.sensoft.Bildirimler.BildirimAdapter;
import com.sensofttakimi.sensoft.Bildirimler.BildirimIcerigi;
import com.sensofttakimi.sensoft.Model.Bildirimler;
import com.sensofttakimi.sensoft.Model.Sohbetler;
import com.sensofttakimi.sensoft.R;
import com.sensofttakimi.sensoft.UygulamaSayfasi;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class FragmentSohbetler extends Fragment {
    private RecyclerView recyclerView;

    SohbetAdapter sohbetAdapter;
    private List<Sohbetler> mSohbetler;

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sohbet,container,false);
        recyclerView = view.findViewById(R.id.recyler_view);

        mSohbetler = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        SohbetleriListele();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        sohbetAdapter = new SohbetAdapter(getActivity(),mSohbetler);
        recyclerView.setAdapter(sohbetAdapter);

//        recyclerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        return view;
    }

    private void SohbetleriListele(){
        firebaseFirestore.collection("Sohbetler").orderBy("sohbet_tarih", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error !=null){
                    Toast.makeText(getActivity(),error.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                }

                if(value != null){
                    FirebaseUser user = auth.getCurrentUser();
                    String email = user.getEmail();
                    mSohbetler.clear();
                    for(DocumentSnapshot snapshot : value.getDocuments()){
                        HashMap<String,Object> data = (HashMap<String, Object>) snapshot.getData();

                        if(email.equals(data.get("kullanici").toString())){
                            String baslik = (String)  data.get("sohbet_baslik");
                            String resim = (String)  data.get("sohbet_resim");
                            if(data.get("sohbet_tarih") != null){
                                Timestamp time = (Timestamp) data.get("sohbet_tarih");
                                Date date = time.toDate();
                                String tarih = date.toString();
                                String pattern = "dd/MM/yyyy kk:mm";
                                SimpleDateFormat format = new SimpleDateFormat(pattern);
                                Sohbetler sohbetler = new Sohbetler(baslik, resim, format.format(date));
                                mSohbetler.add(sohbetler);
                            }

                        }

                    }
                    if(!mSohbetler.isEmpty()){
                        sohbetAdapter.notifyDataSetChanged();
                    }
                    else{

                    }
                }
            }
        });
    }
}
