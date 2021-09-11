package com.sensofttakimi.sensoft.FragmentUygulama;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.sensofttakimi.sensoft.Bildirimler.BildirimAdapter;
import com.sensofttakimi.sensoft.Bildirimler.BildirimEklemeActivity;
import com.sensofttakimi.sensoft.Bildirimler.BildirimIcerigi;
import com.sensofttakimi.sensoft.FragmentMesaj.SohbetiKaydetActivity;
import com.sensofttakimi.sensoft.Model.Bildirimler;
import com.sensofttakimi.sensoft.Model.Sohbetler;
import com.sensofttakimi.sensoft.R;
import com.sensofttakimi.sensoft.UygulamaSayfasi;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class AnasayfaFragment extends Fragment {

    private FloatingActionButton btnekle;
    TextView txtguncelle;

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    ArrayList<Bildirimler> bildirimlerArrayList;
    Uri imageData;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    private View mview;
    private View view;
    private RecyclerView recylerView;
    ProgressDialog dialog;
    EditText edtbaslik;
    EditText edtaciklama;
    EditText edtkelime;
    ImageView resimekle;

    BildirimAdapter bildirimAdapter;
    Dialog dialog2;
    ArrayList<Bildirimler> bildirimIcerigi;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mview = inflater.inflate(R.layout.fragment_anasayfa,container,false);
        this.recylerView = mview.findViewById(R.id.recylerView);
        this.btnekle = mview.findViewById(R.id.eklebuton);
        bildirimIcerigi = new ArrayList<>();

        firebaseStorage = FirebaseStorage.getInstance();
        bildirimlerArrayList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();

        getData();

        recylerView.setLayoutManager(new GridLayoutManager(getActivity(),3,RecyclerView.VERTICAL,false));
        bildirimAdapter = new BildirimAdapter(bildirimlerArrayList,getActivity());
        recylerView.setAdapter(bildirimAdapter);

        RegisterLauncher();

        btnekle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog2 = new Dialog(getActivity());
                dialog2.setContentView(R.layout.bildirim_ekle);
                resimekle = dialog2.findViewById(R.id.resimekle);
                edtbaslik = dialog2.findViewById(R.id.edtbaslik);
                edtaciklama = dialog2.findViewById(R.id.edtaciklama);
                edtkelime = dialog2.findViewById(R.id.edtkelime);
                txtguncelle = dialog2.findViewById(R.id.txtEkle);
                txtguncelle.setText("BİLDİRİM EKLE");
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
                        imageData = null;

                    }
                });

                dialog2.show();
            }

        });


        return mview;
       // return inflater.inflate(R.layout.fragment_anasayfa,container,false);
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

    public void RegisterLauncher(){
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

    public void Kaydet(View view){


        int bosmu = 0;
        if(edtbaslik.length()==0)
        {
            bosmu++;
            edtbaslik.requestFocus();
            edtbaslik.setError("Bu alan boş geçilemez");
        }

        if(edtaciklama.length()==0)
        {
            bosmu++;
            edtaciklama.requestFocus();
            edtaciklama.setError("Bu alan boş geçilemez");
        }

        if(edtkelime.length()==0)
        {
            bosmu++;
            edtkelime.requestFocus();
            edtkelime.setError("Bu alan boş geçilemez");
        }

        if(bosmu<1){

            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Kaydediliyor...");
            dialog.show();
            boolean yok1;
            String baslik = edtbaslik.getText().toString();
            if(this.imageData !=null){

                //VerileriEkle(this.imageData);
                BaslikKontrol(imageData,baslik,0);
                //dialog.dismiss();

            }
            else{
                this.imageData = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                        "://" + getResources().getResourcePackageName(R.drawable.notification)
                        + '/' + getResources().getResourceTypeName(R.drawable.notification) + '/' + getResources().getResourceEntryName(R.drawable.notification) );
                //VerileriEkle(imageData);
                BaslikKontrol(imageData,baslik,1);
                dialog.dismiss();
            }

        }

    }


    boolean yok = false;
    public void BaslikKontrol(Uri imageData, String baslik,int resim){
        FirebaseUser user = auth.getCurrentUser();
        String email = user.getEmail();
        firebaseFirestore.collection("Bildirimler").whereEqualTo("baslik", baslik).whereEqualTo("kullanici",email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    String documentt = null;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ArrayList<Bildirimler> deneme = new ArrayList<>();
                        DocumentReference docRef = firebaseFirestore.collection("Bildirimler").document(document.getId());
                        documentt = docRef.toString();

                        dialog.cancel();
                        Toast.makeText(getActivity(),"Bu başlıkta bir bildiriminiz var. Lütfen başlığı değiştirerek tekrar deneyiniz!",Toast.LENGTH_LONG).show();
                        return;


                    }

                    if(documentt == null){
//
                        if(resim == 0){
                                VerileriEkle(imageData);
                            }
                            else {
                                VerileriEkle("default");
                            }
                            dialog2.dismiss();
                            dialog.cancel();
                    }
                    else{
                        dialog.cancel();
                    }

                }
            }
        });
    }

    public void VerileriEkle(String imageData){
        String downloadUrl = "default";

        String baslik = edtbaslik.getText().toString();
        String aciklama = edtaciklama.getText().toString();
        String ses = edtkelime.getText().toString();

        FirebaseUser user = auth.getCurrentUser();
        String email = user.getEmail();

        HashMap<String,Object> bildirimVeri = new HashMap<>();
        bildirimVeri.put("baslik",baslik);
        bildirimVeri.put("aciklama",aciklama);
        bildirimVeri.put("ses",ses);
        bildirimVeri.put("resim",downloadUrl);
        bildirimVeri.put("kullanici",email);
        bildirimVeri.put("tarih", FieldValue.serverTimestamp());


        firebaseFirestore.collection("Bildirimler").add(bildirimVeri).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getActivity(),"Bildirim kaydedildi",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getActivity(),"Bildirim kaydedilemedi",Toast.LENGTH_LONG).show();

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
                        String aciklama = edtaciklama.getText().toString();
                        String ses = edtkelime.getText().toString();

                        FirebaseUser user = auth.getCurrentUser();
                        String email = user.getEmail();

                        HashMap<String,Object> bildirimVeri = new HashMap<>();
                        bildirimVeri.put("baslik",baslik);
                        bildirimVeri.put("aciklama",aciklama);
                        bildirimVeri.put("ses",ses);
                        bildirimVeri.put("resim",downloadUrl);
                        bildirimVeri.put("kullanici",email);
                        bildirimVeri.put("tarih", FieldValue.serverTimestamp());


                        firebaseFirestore.collection("Bildirimler").add(bildirimVeri).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getActivity(),"Bildirim kaydedildi",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Toast.makeText(getActivity(),"Bildirim kaydedilemedi",Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getActivity(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
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
                        //bildirimlerArrayList.clear();
                        HashMap<String,Object> data = (HashMap<String, Object>) snapshot.getData();
                        if(email.equals(data.get("kullanici").toString())){
                            String baslik = (String)  data.get("baslik");
                            String resim = (String)  data.get("resim");
                            if(data.get("resim")=="default"){
                                resim = "Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE" +
                                        "://" + "getResources().getResourcePackageName(R.drawable.notification)"
                                        + '/' + "getResources().getResourceTypeName(R.drawable.notification)" + '/' + "getResources().getResourceEntryName(R.drawable.notification) )";
                            }
                            else{

                            }

                            
                            Bildirimler bildirimler = new Bildirimler(baslik, resim);
                            bildirimlerArrayList.add(bildirimler);
                        }

                    }
                    if(!bildirimlerArrayList.isEmpty()){
                        bildirimAdapter.notifyDataSetChanged();
                       // bildirimlerArrayList.clear();

                    }

                }
            }
        });
    }
    int sayac = 0;
    private void VeriIcerigi(String baslik){
        ArrayList<Bildirimler> bildirimIcerigii = new ArrayList<>();
        FirebaseUser user = auth.getCurrentUser();
        String email = user.getEmail();
        firebaseFirestore.collection("Bildirimler").whereEqualTo("baslik",baslik).whereEqualTo("kullanici",email).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error !=null){
                    Toast.makeText(getActivity(),error.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                }
                if(value != null){
                    bildirimIcerigii.clear();
                    for(DocumentSnapshot snapshot : value.getDocuments()){
                        HashMap<String,Object> data = (HashMap<String, Object>) snapshot.getData();
                            String baslik = (String)  data.get("baslik");
                            String resim = (String)  data.get("resim");
                            String aciklama = (String) data.get("aciklama");
                            String kelime = (String) data.get("ses");

                            Bildirimler bildirimler = new Bildirimler(baslik,aciklama,kelime, resim);
                            bildirimIcerigii.add(bildirimler);
                    }
                    if(bildirimIcerigii.size()>0 && sayac == 0){
                        sayac++;
                            edtbaslik.setText(bildirimIcerigii.get(0).baslik);
                            edtaciklama.setText(bildirimIcerigii.get(0).aciklama);
                            edtkelime.setText(bildirimIcerigii.get(0).kelime);
                            String imageURL = bildirimIcerigii.get(0).resim;
                            Glide.with(getActivity()).load(imageURL).placeholder(R.drawable.notification).into(resimekle);
                    }
                }
            }
        });
    }
    @Override
    public boolean onContextItemSelected(@NonNull @NotNull MenuItem item) {
        String eskibaslik;
        switch (item.getItemId()){
            case 121:
                eskibaslik = bildirimAdapter.BaslikListele(item.getGroupId());
                BildirimSil(eskibaslik);
                bildirimAdapter.removeItem(item.getGroupId());
                BildirimSil(eskibaslik);
                bildirimAdapter.notifyItemRemoved(item.getGroupId());

                return true;
            case 122:
                sayac = 0;
                dialog2 = new Dialog(getActivity());
                dialog2.setContentView(R.layout.bildirim_ekle);
                resimekle = dialog2.findViewById(R.id.resimekle);
                edtbaslik = dialog2.findViewById(R.id.edtbaslik);
                edtaciklama = dialog2.findViewById(R.id.edtaciklama);
                edtkelime = dialog2.findViewById(R.id.edtkelime);
                txtguncelle = dialog2.findViewById(R.id.txtEkle);
                txtguncelle.setText("BİLDİRİM GÜNCELLE");
                TextView btnkaydet = dialog2.findViewById(R.id.btnkaydet);
                btnkaydet.setText("GÜNCELLE");
                eskibaslik = bildirimAdapter.BaslikListele(item.getGroupId());
                VeriIcerigi(eskibaslik);

                resimekle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ResimEkle(v);
                    }
                });

                btnkaydet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String baslik = edtbaslik.getText().toString();
                        String aciklama = edtaciklama.getText().toString();
                        String kelime = edtkelime.getText().toString();

                        BaslikGuncellemeKontrol(eskibaslik);

//                        BildirimiGuncelle(baslik,aciklama,kelime,eskibaslik,"default");
//                        dialog2.dismiss();

                    }
                });

                dialog2.show();
                Toast.makeText(getActivity(),"Bildirim güncellendi",Toast.LENGTH_SHORT);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void BaslikGuncellemeKontrol(String eskibaslik){
        String baslik = edtbaslik.getText().toString();
        FirebaseUser user = auth.getCurrentUser();
        String email = user.getEmail();
        firebaseFirestore.collection("Bildirimler").whereEqualTo("baslik",baslik).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                int sayac = 0;
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        sayac++;
                    }
                }

                if(sayac>0 && !baslik.equals(eskibaslik)){
                    Toast.makeText(getActivity(),"Aynı başlıkta bildirim var",Toast.LENGTH_SHORT).show();
                }
                else{
                    String baslik = edtbaslik.getText().toString();
                    String aciklama = edtaciklama.getText().toString();
                    String kelime = edtkelime.getText().toString();


                    BildirimiGuncelle(baslik,aciklama,kelime,eskibaslik,"default");
                    dialog2.dismiss();
                }
            }
        });
    }
    public void BildirimSil(String baslik){
            FirebaseUser user = auth.getCurrentUser();
            String email = user.getEmail();
            firebaseFirestore.collection("Bildirimler").whereEqualTo("baslik", baslik).whereEqualTo("kullanici",email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            DocumentReference washingtonRef = firebaseFirestore.collection("Bildirimler").document(document.getId());
                            washingtonRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getActivity(),"Bildirim silindi",Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });
                        }
                    } else {
                    }
                }
            });

        }

    private void BildirimiGuncelle(String baslik, String aciklama, String kelime, String eskiBaslik, String resim){
        FirebaseUser user = auth.getCurrentUser();
        String email = user.getEmail();
        firebaseFirestore.collection("Bildirimler").whereEqualTo("baslik", eskiBaslik).whereEqualTo("kullanici",email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DocumentReference washingtonRef = firebaseFirestore.collection("Bildirimler").document(document.getId());
                        washingtonRef.update("baslik", baslik,"aciklama",aciklama,"ses",kelime).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(),"Bildirim güncellendi",Toast.LENGTH_LONG).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                    }
                } else {
                }
            }
        });

    }
    }

