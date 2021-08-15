package com.sensofttakimi.sensoft.Model;

public class Sohbetler {
    private String sohbetId;
    private String sohbet_baslik;
    private String sohbet_resmi;
    private String sohbet_tarihi;
    private String kullanici;

    public Sohbetler(String id, String sohbet_baslik, String sohbet_resmi, String sohbet_tarihi, String kullanici) {
        this.sohbetId = id;
        this.sohbet_baslik = sohbet_baslik;
        this.sohbet_resmi = sohbet_resmi;
        this.sohbet_tarihi = sohbet_tarihi;
        this.kullanici = kullanici;
    }

    public Sohbetler(String sohbet_baslik, String sohbet_resmi, String sohbet_tarihi) {
        this.sohbet_baslik = sohbet_baslik;
        this.sohbet_resmi = sohbet_resmi;
        this.sohbet_tarihi = sohbet_tarihi;
    }

    public Sohbetler(){

    }

    public String getSohbetId() {
        return sohbetId;
    }

    public void setSohbetId(String id) {
        this.sohbetId = id;
    }

    public String getSohbet_baslik() {
        return sohbet_baslik;
    }

    public void setSohbet_baslik(String sohbet_baslik) {
        this.sohbet_baslik = sohbet_baslik;
    }

    public String getSohbet_resmi() {
        return sohbet_resmi;
    }

    public void setSohbet_resmi(String sohbet_resmi) {
        this.sohbet_resmi = sohbet_resmi;
    }

    public String getSohbet_tarihi() {
        return sohbet_tarihi;
    }

    public void setSohbet_tarihi(String sohbet_tarihi) {
        this.sohbet_tarihi = sohbet_tarihi;
    }

    public String getKullanici() {
        return kullanici;
    }

    public void setKullanici(String kullanici) {
        this.kullanici = kullanici;
    }
}
