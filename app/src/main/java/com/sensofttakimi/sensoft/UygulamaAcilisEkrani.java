package com.sensofttakimi.sensoft;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class UygulamaAcilisEkrani extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uygulama_acilis_ekrani);

        Thread logoanimasyon = new Thread(){
            @Override
            public void run() {
                ImageView logo = findViewById(R.id.logo);
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.uygulama_acilis_animasyonu);
                logo.startAnimation(animation);
            }
        };
        logoanimasyon.start();

        Thread son = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(2000);
                    Intent intent = new Intent(getApplicationContext(),KullaniciGirisi.class);
                    startActivity(intent);
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        son.start();
    }
}