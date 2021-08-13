package com.sensofttakimi.sensoft.Bildirimler;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sensofttakimi.sensoft.R;
import com.sensofttakimi.sensoft.UygulamaSayfasi;
import com.sensofttakimi.sensoft.databinding.ActivityBildirimEklemeBinding;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class BildirimEklemeActivity extends AppCompatActivity {

    private FirebaseStorage firebaseStorage;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    Uri imageData;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    private ActivityBildirimEklemeBinding binding;
    Bitmap selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBildirimEklemeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();

        RegisterLauncher();


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
                        String aciklama = binding.edtaciklama.getText().toString();
                        String ses = binding.edtkelime.getText().toString();

                        FirebaseUser user = auth.getCurrentUser();
                        String email = user.getEmail();


                        HashMap<String,Object> bildirimVeri = new HashMap<>();
                        bildirimVeri.put("baslik",baslik);
                        bildirimVeri.put("aciklama",aciklama);
                        bildirimVeri.put("ses",ses);
                        bildirimVeri.put("resim",downloadUrl);
                        bildirimVeri.put("kullanici",email);
                        bildirimVeri.put("tarih", "dddd");

                        firebaseFirestore.collection("Bildirimler").add(bildirimVeri).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Intent intent = new Intent(getApplicationContext(), UygulamaSayfasi.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Toast.makeText(BildirimEklemeActivity.this,"Bildirim kaydedilemedi",Toast.LENGTH_LONG).show();

                            }
                        });

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(BildirimEklemeActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void Kaydet(View view){
        if(imageData !=null){

            VerileriEkle(imageData);

//            UUID uuid = UUID.randomUUID();
//            String imageName = "images/"+uuid+".jpg";
//
//            storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    StorageReference newRefrence = firebaseStorage.getReference(imageName);
//                    newRefrence.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            String downloadUrl = uri.toString();
//
//                            String baslik = binding.edtbaslik.getText().toString();
//                            String aciklama = binding.edtaciklama.getText().toString();
//                            String ses = binding.edtkelime.getText().toString();
//
//                            FirebaseUser user = auth.getCurrentUser();
//                            String email = user.getEmail();
//
//
//                            HashMap<String,Object> bildirimVeri = new HashMap<>();
//                            bildirimVeri.put("baslik",baslik);
//                            bildirimVeri.put("aciklama",aciklama);
//                            bildirimVeri.put("ses",ses);
//                            bildirimVeri.put("resim",downloadUrl);
//                            bildirimVeri.put("kullanici",email);
//                            bildirimVeri.put("tarih", "dddd");
//
//                            firebaseFirestore.collection("Bildirimler").add(bildirimVeri).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                @Override
//                                public void onSuccess(DocumentReference documentReference) {
//                                    Intent intent = new Intent(getApplicationContext(), UygulamaSayfasi.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    startActivity(intent);
//                                    finish();
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull @NotNull Exception e) {
//                                    Toast.makeText(BildirimEklemeActivity.this,"Bildirim kaydedilemedi",Toast.LENGTH_LONG).show();
//
//                                }
//                            });
//
//                        }
//                    });
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull @NotNull Exception e) {
//                    Toast.makeText(BildirimEklemeActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
//                }
//            });
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
                        binding.resimekle.setImageURI(imageData);

                        /*try{

                            if(Build.VERSION.SDK_INT>=28){
                                ImageDecoder.Source source = ImageDecoder.createSource(BildirimEklemeActivity.this.getContentResolver(),imageData);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                                binding.resimekle.setImageBitmap(selectedImage);
                            }else{
                                selectedImage = MediaStore.Images.Media.getBitmap(BildirimEklemeActivity.this.getContentResolver(),imageData);
                                binding.resimekle.setImageBitmap(selectedImage);
                            }

                        } catch (Exception e){
                            e.printStackTrace();
                        }*/
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
                    Toast.makeText(BildirimEklemeActivity.this,"Permission needed!",Toast.LENGTH_LONG).show();

                }
            }
        });
    }
}