package com.sensofttakimi.sensoft.Model;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FieldValue;

public class Bildirimler {


    public String baslik;
    public String aciklama;
    public String kelime;
    public String kullanici;
    public String resim;
    public String tarih;
    public String saat;

    public Bildirimler(String baslik, String resim, String aciklama,String tarih, String kelime) {
        this.baslik = baslik;
        this.resim = resim;
        this.tarih = tarih;
        this.aciklama = aciklama;
        this.kelime = kelime;
    }

    public Bildirimler(String baslik, String aciklama,String kullanici, String resim, String tarih, String saat) {
        this.baslik = baslik;
        this.aciklama = aciklama;
        this.kelime = kelime;
        this.kullanici = kullanici;
        this.resim = resim;
        this.tarih = tarih;
        this.saat = saat;
    }

    public Bildirimler(String baslik, String resim) {
        this.baslik = baslik;
        this.resim = resim;
    }

    public Bildirimler(String baslik, String aciklama, String kelime, String resim) {
        this.baslik = baslik;
        this.aciklama = aciklama;
        this.kelime = kelime;
        this.resim = resim;
    }
}
