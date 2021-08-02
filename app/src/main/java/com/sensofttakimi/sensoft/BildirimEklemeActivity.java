package com.sensofttakimi.sensoft;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class BildirimEklemeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bildirim_ekleme);
    }

    public void Kaydet(View view){
        Intent intent = new Intent(getApplicationContext(),UygulamaSayfasi.class);
        startActivity(intent);
        finish();
    }
}