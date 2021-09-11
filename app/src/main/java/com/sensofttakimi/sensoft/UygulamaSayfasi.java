package com.sensofttakimi.sensoft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.sensofttakimi.sensoft.FragmentMesaj.FragmentSohbetler;
import com.sensofttakimi.sensoft.FragmentUygulama.MesajFragment;
import com.sensofttakimi.sensoft.FragmentUygulama.UygulamaSayfasiAdapter;

import org.jetbrains.annotations.NotNull;

public class UygulamaSayfasi extends AppCompatActivity{

    private FirebaseAuth auth;
    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    FragmentManager manager;
    Vibrator vb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uygulama_sayfasi);

        auth = FirebaseAuth.getInstance();
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        UygulamaSayfasiAdapter adapter = new UygulamaSayfasiAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.nav_anasayfa).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.nav_bildirim).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.nav_mesaj).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.nav_hakkinda).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {

                    switch (item.getItemId()){
                        case R.id.nav_anasayfa:
                            viewPager.setCurrentItem(0);
                            break;
                        case R.id.nav_bildirim:
                            viewPager.setCurrentItem(1);
                            break;
                        case R.id.nav_mesaj:
                            viewPager.setCurrentItem(2);
                            break;
                        case R.id.nav_hakkinda:
                            viewPager.setCurrentItem(3);
                            break;
                    }

                return true;
            }
        });

        Intent intent = getIntent();
        String fragmentk = (String) intent.getStringExtra("fragment");
        if(fragmentk==null){
        }
        else {
            viewPager.setCurrentItem(2);
        }

        createNotificationChannel();



    }
    private void updateNavigationBarState(int actionId){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.cikis:
                auth.signOut();
                Intent intent = new Intent(getApplicationContext(), UygulamaAcilisEkrani.class);
                startActivity(intent);
                finish();
                break;
            case R.id.gonder:
                Toast.makeText(getApplicationContext(),"Bildirim gönder",Toast.LENGTH_LONG).show();
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"sensoft")
                        .setSmallIcon(R.drawable.ic_baseline_info_24)
                        .setContentTitle("Başlık")
                        .setContentText("Bildirim içeriği yer alacaktır...")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)
                        .setTicker("Sensoft");
                notificationManager.notify(100,builder.build());

                long[] dizi = {500, 500, 300, 100};
                vb.vibrate(dizi,3);
                vb.cancel();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    NotificationManager notificationManager;
    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "studentChannel";
            String description = "Channel for student notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("sensoft",name,importance);
            channel.setDescription(description);

            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}