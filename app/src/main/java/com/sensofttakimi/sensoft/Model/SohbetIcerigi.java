package com.sensofttakimi.sensoft.Model;

public class SohbetIcerigi {
    private String kullanici;
    private String sohbet_baslik;
    private String mesaj;
    private String sohbet_tarih;
    private String rol;

    public SohbetIcerigi(String kullanici, String sohbet_baslik, String mesaj, String sohbet_tarih, String rol) {
        this.kullanici = kullanici;
        this.sohbet_baslik = sohbet_baslik;
        this.mesaj = mesaj;
        this.sohbet_tarih = sohbet_tarih;
        this.rol = rol;
    }

    public SohbetIcerigi(){

    }

    public SohbetIcerigi(String kullanici, String sohbet_baslik, String mesaj, String rol) {
        this.kullanici = kullanici;
        this.sohbet_baslik = sohbet_baslik;
        this.mesaj = mesaj;
        this.rol = rol;
    }

    public String getKullanici() {
        return kullanici;
    }

    public void setKullanici(String kullanici) {
        this.kullanici = kullanici;
    }

    public String getSohbet_baslik() {
        return sohbet_baslik;
    }

    public void setSohbet_baslik(String sohbet_baslik) {
        this.sohbet_baslik = sohbet_baslik;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public String getSohbet_tarih() {
        return sohbet_tarih;
    }

    public void setSohbet_tarih(String sohbet_tarih) {
        this.sohbet_tarih = sohbet_tarih;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
