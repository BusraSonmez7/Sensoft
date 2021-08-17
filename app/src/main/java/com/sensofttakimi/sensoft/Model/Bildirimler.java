package com.sensofttakimi.sensoft.Model;

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
}
