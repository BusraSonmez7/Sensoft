package com.sensofttakimi.sensoft.FragmentMesaj;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

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
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

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

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            String eskibaslik;
            switch (direction){
                case ItemTouchHelper.LEFT:
                    eskibaslik = sohbetAdapter.BaslikListele(position);
                    BildirimSil(eskibaslik);
                    sohbetAdapter.removeItem(position);
                    BildirimSil(eskibaslik);
                    sohbetAdapter.notifyItemRemoved(position);
                    break;
                case ItemTouchHelper.RIGHT:
                    eskibaslik = sohbetAdapter.BaslikListele(position);
                    BildirimSil(eskibaslik);
                    sohbetAdapter.removeItem(position);
                    BildirimSil(eskibaslik);
                    sohbetAdapter.notifyItemRemoved(position);
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(getActivity(),c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getActivity(),R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getActivity(),R.color.red))
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_delete_24)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
    public void BildirimSil(String baslik){
        FirebaseUser user = auth.getCurrentUser();
        String email = user.getEmail();
        firebaseFirestore.collection("Sohbetler").whereEqualTo("sohbet_baslik", baslik).whereEqualTo("kullanici",email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DocumentReference washingtonRef = firebaseFirestore.collection("Sohbetler").document(document.getId());
                        washingtonRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(),"Sohbet silindi",Toast.LENGTH_LONG).show();

                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                    }
                } else {
                }
            }
        });
//        firebaseFirestore.collection("SohbetIcerigi").whereEqualTo("sohbet_baslik","kaydedilmedi").whereEqualTo("kullanici",email).addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if(error !=null){
//                    Toast.makeText(getApplicationContext(),error.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
//                }
//
//                if(value != null){
//                    for(DocumentSnapshot snapshot : value.getDocuments()){
//                        HashMap<String,Object> data = (HashMap<String, Object>) snapshot.getData();
//                        Toast.makeText(getApplicationContext(),snapshot.getId().toString(),Toast.LENGTH_LONG).show();
//                        DocumentReference washingtonRef = firebaseFirestore.collection("SohbetIcerigi").document(snapshot.getId());
//                        washingtonRef.update("sohbet_baslik", baslik).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//                                        Toast.makeText(SohbetiKaydetActivity.this,"mesaj başlığı güncellendi",Toast.LENGTH_LONG).show();
//
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                    }
//                                });
//
//                    }
//                }
//            }
//        });
    }
}
