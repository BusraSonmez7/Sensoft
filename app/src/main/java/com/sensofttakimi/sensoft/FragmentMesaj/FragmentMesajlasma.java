package com.sensofttakimi.sensoft.FragmentMesaj;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sensofttakimi.sensoft.Model.Bildirimler;
import com.sensofttakimi.sensoft.Model.SohbetIcerigi;
import com.sensofttakimi.sensoft.Model.Sohbetler;
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
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class FragmentMesajlasma extends Fragment {
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private View mview;
    private RecyclerView recyclerView;
    private ImageButton btnmetin, btnses;
    private EditText edtmesaj, edtses;
    EditText edtbaslik;
    CircleImageView resimekle;

    Uri imageData;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;

    Dialog dialog2;
    ProgressDialog dialog;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

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

        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();
        deneme = new ArrayList<>();


        recyclerView = mview.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        RegisterLauncher();


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
                //Toast.makeText(getActivity(),"Mesaj gönderildi",Toast.LENGTH_LONG).show();
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
                dialog2 = new Dialog(getActivity());
                dialog2.setContentView(R.layout.sohbet_ekle);
                resimekle = dialog2.findViewById(R.id.c_image);
                edtbaslik = dialog2.findViewById(R.id.edtbaslik);
                TextView btnkaydet = dialog2.findViewById(R.id.btnkaydet);
                //


                resimekle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ResimEkle(v);
                    }
                });

                btnkaydet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Kaydet(v);

                    }
                });

                dialog2.show();
                break;
        }
        return true;

    }

    @Override
    public boolean onContextItemSelected(@NonNull @NotNull MenuItem item) {
        String mesaj;
        switch (item.getItemId()){
            case 121:
                mesaj = sohbetIcerikAdapter.BaslikListele(item.getGroupId());
                BildirimSil(mesaj);
                sohbetIcerikAdapter.removeItem(item.getGroupId());
                BildirimSil(mesaj);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void BildirimSil(String baslik){
        FirebaseUser user = auth.getCurrentUser();
        String email = user.getEmail();
        firebaseFirestore.collection("SohbetIcerigi").whereEqualTo("mesaj", baslik).whereEqualTo("kullanici",email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DocumentReference washingtonRef = firebaseFirestore.collection("SohbetIcerigi").document(document.getId());
                        washingtonRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Toast.makeText(getActivity(),"mesaj silindi",Toast.LENGTH_LONG).show();

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

    public void Kaydet(View view){
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Kaydediliyor...");
        dialog.show();

        String baslik = edtbaslik.getText().toString();

        if(this.imageData !=null){
            getData(imageData,baslik);
            VerileriEkle(this.imageData);

        }
        else{
            this.imageData = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + getResources().getResourcePackageName(R.drawable.notification)
                    + '/' + getResources().getResourceTypeName(R.drawable.notification) + '/' + getResources().getResourceEntryName(R.drawable.notification) );
            getData(imageData,baslik);
            VerileriEkle(imageData);
            dialog.dismiss();
        }
    }

    public void ResimEkle(View view){
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //izin istenecek
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                    }
                }).show();
            }else{
                //izin istenecek
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        else{
            //izin verildiyse
            Intent intentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentGallery);

        }
    }

    private void RegisterLauncher(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK){
                    Intent intentFromResult = result.getData();
                    if(intentFromResult != null){
                        imageData = intentFromResult.getData();
                        resimekle.setImageURI(imageData);
                    }
                    else{
                        imageData = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+
                                "://"+getResources().getResourcePackageName(R.drawable.image_install1)+
                                '/'+getResources().getResourceTypeName(R.drawable.image_install1)+'/'+
                                getResources().getResourceEntryName(R.drawable.image_install1));
                    }
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    Intent intentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentGallery);
                }else{
                    Toast.makeText(getActivity(),"Permission needed!",Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    public void VerileriEkle(Uri imageData){
        UUID uuid = UUID.randomUUID();
        String imageName = "images/"+uuid+".jpg";

        storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                StorageReference newRefrence = firebaseStorage.getReference(imageName);
                newRefrence.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadUrl = uri.toString();

                        String baslik = edtbaslik.getText().toString();

                        FirebaseUser user = auth.getCurrentUser();
                        String email = user.getEmail();

                        HashMap<String,Object> bildirimVeri = new HashMap<>();
                        bildirimVeri.put("sohbet_baslik",baslik);
                        bildirimVeri.put("sohbet_resim",downloadUrl);
                        bildirimVeri.put("kullanici",email);
                        bildirimVeri.put("sohbet_tarih", FieldValue.serverTimestamp());


                        firebaseFirestore.collection("Sohbetler").add(bildirimVeri).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                VerileriGuncelle(baslik);
                                Toast.makeText(getActivity(),"Sohbet kaydedildi",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getActivity(), UygulamaSayfasi.class);
                                intent.putExtra("fragment","sohbetler");
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                dialog2.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Toast.makeText(getActivity(),"Bildirim kaydedilemedi",Toast.LENGTH_LONG).show();

                            }
                        });



                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                //Toast.makeText(getActivity(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void VerileriGuncelle(String baslik){
        FirebaseUser user = auth.getCurrentUser();
        String email = user.getEmail();
        firebaseFirestore.collection("SohbetIcerigi").whereEqualTo("sohbet_baslik", "kaydedilmedi").whereEqualTo("kullanici",email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int sayac = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        int dsayisi = task.getResult().size();
                        DocumentReference washingtonRef = firebaseFirestore.collection("SohbetIcerigi").document(document.getId());
                        washingtonRef.update("sohbet_baslik", baslik).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(),"mesaj başlığı güncellendi",Toast.LENGTH_LONG).show();
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                        sayac++;

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

    ArrayList<Sohbetler> deneme;
    boolean yok = false;
    private void getData(Uri imageData, String baslik){
        FirebaseUser user = auth.getCurrentUser();
        String email = user.getEmail();
        firebaseFirestore.collection("Sohbetler").whereEqualTo("sohbet_baslik", baslik).whereEqualTo("kullanici",email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DocumentReference docRef = firebaseFirestore.collection("Sohbetler").document(document.getId());
                        Toast.makeText(getActivity(),document.getId().toString(),Toast.LENGTH_LONG).show();
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        HashMap<String,Object> data = (HashMap<String, Object>) document.getData();
                                        String baslik = (String)  data.get("sohbet_baslik");
                                        String resim = (String)  data.get("sohbet_resim");

                                        Sohbetler sohbetler = new Sohbetler(baslik, resim);
                                        deneme.add(sohbetler);
                                        if(!deneme.isEmpty()){
                                            dialog.cancel();
                                            Toast.makeText(getActivity(),"Bu başlıkta bir bildiriminiz var. Lütfen başlığı değiştirerek tekrar deneyiniz!",Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        Toast.makeText(getActivity(),deneme.isEmpty()+" ",Toast.LENGTH_LONG).show();

                                    }
                                    else {

                                        VerileriEkle(imageData);
                                    }
                                }
                            }
                        });
                    }
//                    if(!deneme.isEmpty()){
//                        dialog.cancel();
//                        if(!yok){
//                            Toast.makeText(getActivity(),"Bu başlıkta bir bildiriminiz var. Lütfen başlığı değiştirerek tekrar deneyiniz!",Toast.LENGTH_LONG).show();
//                        }
//                    }
//                    else {
//                        yok = true;
//                        VerileriEkle(imageData);
//
//                    }
                } else {
                }
            }
        });


//        firebaseFirestore.collection("Sohbetler").whereEqualTo("sohbet_baslik",baslik).whereEqualTo("kullanici",email).addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if(error !=null){
//                    Toast.makeText(getApplicationContext(),error.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
//                }
//
//                if(value != null){
//                    for(DocumentSnapshot snapshot : value.getDocuments()){
//                        HashMap<String,Object> data = (HashMap<String, Object>) snapshot.getData();
//                        String baslik = (String)  data.get("baslik");
//                        String resim = (String)  data.get("resim");
//
//                        Bildirimler bildirimler = new Bildirimler(baslik, resim);
//                        deneme.add(bildirimler);
//
//                    }
//                    if(!deneme.isEmpty()){
//                        dialog.cancel();
//                        if(!yok){
//                            Toast.makeText(getApplicationContext(),"Bu başlıkta bir bildiriminiz var. Lütfen başlığı değiştirerek tekrar deneyiniz!",Toast.LENGTH_LONG).show();
//                        }
//                        deneme.clear();
//                    }
//                    else {
//                        yok = true;
//                        VerileriEkle(imageData);
//
//                    }
//                }
//
//
//            }
//        });
//
    }
}
