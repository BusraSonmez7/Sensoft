package com.sensofttakimi.sensoft.FragmentMesaj;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

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
import com.sensofttakimi.sensoft.Bildirimler.BildirimEklemeActivity;
import com.sensofttakimi.sensoft.Model.Bildirimler;
import com.sensofttakimi.sensoft.R;
import com.sensofttakimi.sensoft.UygulamaSayfasi;
import com.sensofttakimi.sensoft.databinding.ActivityBildirimEklemeBinding;
import com.sensofttakimi.sensoft.databinding.ActivitySohbetiKaydetBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class SohbetiKaydetActivity extends AppCompatActivity {

    private ActivitySohbetiKaydetBinding binding;
    Uri imageData;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    ProgressDialog dialog;

    private FirebaseStorage firebaseStorage;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySohbetiKaydetBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();

        RegisterLauncher();

    }
    public void Kaydet(View view){
        dialog = new ProgressDialog(this);
        dialog.setMessage("Kaydediliyor...");
        dialog.setCancelable(false);
        dialog.show();

        String baslik = binding.edtbaslik.getText().toString();
        if(this.imageData !=null){

            //VerileriEkle(this.imageData);
            getData(imageData,baslik);

        }
        else{
            this.imageData = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + getResources().getResourcePackageName(R.drawable.notification)
                    + '/' + getResources().getResourceTypeName(R.drawable.notification) + '/' + getResources().getResourceEntryName(R.drawable.notification) );
            //VerileriEkle(imageData);
            getData(imageData,baslik);
            dialog.dismiss();
        }
    }

    public void ResimEkle(View view){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
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
                        binding.cImage.setImageURI(imageData);
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
                    Toast.makeText(SohbetiKaydetActivity.this,"Permission needed!",Toast.LENGTH_LONG).show();

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

                        String baslik = binding.edtbaslik.getText().toString();

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
                                Toast.makeText(SohbetiKaydetActivity.this,"Sohbet kaydedildi",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), UygulamaSayfasi.class);
                                intent.putExtra("fragment","sohbetler");
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Toast.makeText(SohbetiKaydetActivity.this,"Bildirim kaydedilemedi",Toast.LENGTH_LONG).show();

                            }
                        });



                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(SohbetiKaydetActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
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
                                                //Toast.makeText(SohbetiKaydetActivity.this,"mesaj başlığı güncellendi",Toast.LENGTH_LONG).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                            }
                                        });
                                sayac++;

                                Toast.makeText(getApplicationContext(),dsayisi+" : "+sayac,Toast.LENGTH_LONG).show();
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

    ArrayList<Bildirimler> deneme;
    boolean yok = false;
    private void getData(Uri imageData, String baslik){
        deneme = new ArrayList<>();
        FirebaseUser user = auth.getCurrentUser();
        String email = user.getEmail();
        firebaseFirestore.collection("SohbetIcerigi").whereEqualTo("sohbet_baslik", baslik).whereEqualTo("kullanici",email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int sayac = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DocumentReference docRef = firebaseFirestore.collection("Sohbetler").document(document.getId());
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        HashMap<String,Object> data = (HashMap<String, Object>) document.getData();
                                        String baslik = (String)  data.get("baslik");
                                        String resim = (String)  data.get("resim");

                                        Bildirimler bildirimler = new Bildirimler(baslik, resim);
                                        deneme.add(bildirimler);
                                    } else {
                                    }
                                } else {
                                }
                            }
                        });
                    }
                    if(!deneme.isEmpty()){
                        dialog.cancel();
                        if(!yok){
                            Toast.makeText(getApplicationContext(),"Bu başlıkta bir bildiriminiz var. Lütfen başlığı değiştirerek tekrar deneyiniz!",Toast.LENGTH_LONG).show();
                        }
                        deneme.clear();
                    }
                    else {
                        yok = true;
                        VerileriEkle(imageData);

                    }
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

    }
}