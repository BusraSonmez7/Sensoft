package com.sensofttakimi.sensoft.Bildirimler;

import com.google.firebase.firestore.FieldValue;

public class Bildirimler {
    public String baslik;
    public String aciklama;
    public String kelime;
    public String kullanici;
    public String resim;
    public String tarih;

    public Bildirimler(String baslik, String resim, String aciklama,String tarih) {
        this.baslik = baslik;
        this.resim = resim;
        this.tarih = tarih;
        this.aciklama = aciklama;
    }
}
