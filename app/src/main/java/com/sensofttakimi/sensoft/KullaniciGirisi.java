package com.sensofttakimi.sensoft;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.sensofttakimi.sensoft.databinding.ActivityKullaniciGirisiBinding;

import org.jetbrains.annotations.NotNull;

public class KullaniciGirisi extends AppCompatActivity {

    private ActivityKullaniciGirisiBinding binding;
    private static final int rc_sign_in = 0;
    private GoogleSignInClient googleSignInClient;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth auth;
    private static final String tag = "GOOGLE_SIGN_IN_TAG";


    private FirebaseAuth.AuthStateListener mAuthListener;
    private com.google.android.gms.common.SignInButton signInButton;
    private GoogleApiClient mGoogleApiClient;
    String email, sifre;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityKullaniciGirisiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        auth = FirebaseAuth.getInstance();

        FirebaseUser user2 = auth.getCurrentUser();
        if(user2 != null){
            Intent intent = new Intent(getApplicationContext(), UygulamaSayfasi.class);
            startActivity(intent);
            finish();
        }


        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull @NotNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                user = firebaseAuth.getCurrentUser();
                if (user != null) {
// kullan??c?? giri?? yapt??
                    Log.d(tag, "onAuthStateChanged:signed_in:" + user.getUid());



                } else {
// kullan??c?? ????k???? yapt??
                    Log.d(tag, "onAuthStateChanged:signed_out");
                }
// ...
            }
        };

        binding.signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signIn();
            }
        });

        /*
        *
        *
        *
        *
        *
        *
        * EMA??L VE ????FRE G??R?????? ??LE KAYIT*/


    }

    private void signIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, rc_sign_in);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == rc_sign_in) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                //Google giri??i ba??ar??l?? oldu, Firebase ile haberle??iyoruz
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                Toast.makeText(KullaniciGirisi.this,"Giri?? ba??ar??l??",Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getApplicationContext(),UygulamaSayfasi.class);
                startActivity(intent);
                finish();

            } else {
            // Google giri??i fail oldu
            // ...
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        Log.d(tag, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // giri?? ba??ar??s??z oldu??unda toast mesaj?? g??sterme

                        if (!task.isSuccessful()) {

                            Log.w(tag, "signInWithCredential", task.getException());
                            Toast.makeText(KullaniciGirisi.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void UyeOl(View view) {
        email = binding.emailgiris.getText().toString();
        sifre = binding.sifregiris.getText().toString();

        if(email.equals("") ||sifre.equals("")){
            AlertDialog.Builder alert;
            alert= new AlertDialog.Builder(KullaniciGirisi.this);
            alert.setTitle("HATA");
            alert.setMessage("Email ve ??ifre girin!");
            alert.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alert.show();
        }
        else {
            auth.createUserWithEmailAndPassword(email,sifre).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent intent = new Intent(getApplicationContext(),UygulamaSayfasi.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    AlertDialog.Builder alert;
                    alert= new AlertDialog.Builder(KullaniciGirisi.this);

                    if(e.getMessage().equals("The email address is badly formatted.")){

                        alert.setTitle("HATA");
                        alert.setMessage("Girdi??iniz email adresi yanl???? formatta!");
                        alert.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alert.show();

                    }
                    else if(e.getMessage().equals("The given password is invalid. [ Password should be at least 6 characters ]")){
                        alert.setTitle("HATA");
                        alert.setMessage("??ifreniz en az 6 karakterden olu??mal??!");
                        alert.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alert.show();
                    }
                    else if(e.getMessage().equals("The email address is already in use by another account.")){
                        alert.setTitle("HATA");
                        alert.setMessage("Girdi??iniz email adresi ile kay??tl?? kullan??c?? bulunmaktad??r!");
                        alert.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alert.show();

                    }
                    else{
                        alert.setTitle("HATA");
                        alert.setMessage(e.getMessage());
                        alert.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alert.show();
                    }

                    Log.d(tag, "mesaj"+e.getMessage());


                    }

            });

        }

    }

    public void GirisYap(View view) {
        email = binding.emailgiris.getText().toString();
        sifre = binding.sifregiris.getText().toString();

        if(email.equals("") ||sifre.equals("")){
            AlertDialog.Builder alert;
            alert= new AlertDialog.Builder(KullaniciGirisi.this);
            alert.setTitle("HATA");
            alert.setMessage("Email ve ??ifre girin!");
            alert.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alert.show();
        }
        else {
            auth.signInWithEmailAndPassword(email,sifre).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent intent = new Intent(getApplicationContext(),UygulamaSayfasi.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    AlertDialog.Builder alert;
                    alert= new AlertDialog.Builder(KullaniciGirisi.this);
                    alert.setTitle("HATA");
                    alert.setMessage("L??tfen email ve ??ifrenizi kontrol edip tekrar giriniz.");
                    alert.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            binding.sifregiris.setText("");
                        }
                    });
                    alert.show();
                }
            });

        }
    }

    public void SifremiUnuttum(View view) {

    }
}