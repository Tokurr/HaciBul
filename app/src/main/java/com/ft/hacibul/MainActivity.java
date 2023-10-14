package com.ft.hacibul;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private EditText sifre1;
    private EditText tel;
    private String telNo;
    private CountryCodePicker countryCodePicker;
    private String girisSifre;
    private int eslesme;


    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        tel = findViewById(R.id.girisTelNo);
        sifre1 = findViewById(R.id.girisSifre);
        Button butonGiris = findViewById(R.id.butonGiris);
        TextView butonKayitOl = findViewById(R.id.butonKayitOl);
        countryCodePicker = findViewById(R.id.giris_countrycode);

        butonGiris.setOnClickListener(this::girisButon);

        butonKayitOl.setOnClickListener(view -> {
            Intent backIntent = new Intent(MainActivity.this, KayitSecenekSayfasi.class);
            finish();
            startActivity(backIntent);
        });
    }

    private void girisButon(View view) {
        eslesme = 1;
        String telefon = tel.getText().toString();
        girisSifre = sifre1.getText().toString();

        if (telefon.isEmpty() && girisSifre.isEmpty()) {
            Toast.makeText(MainActivity.this, "Telefon numarası alanı ve şifre alanı boş bırakılamaz. Lütfen telefon numaranızı ve şifrenizi girin.", Toast.LENGTH_LONG).show();
            return;
        } else if (telefon.isEmpty()) {
            Toast.makeText(MainActivity.this, "Telefon numarası alanı boş bırakılamaz. Lütfen telefon numaranızı girin.", Toast.LENGTH_LONG).show();
            return;
        } else if (girisSifre.isEmpty()) {
            Toast.makeText(MainActivity.this, "Şifre alanı boş bırakılamaz. Lütfen şifrenizi girin.", Toast.LENGTH_LONG).show();
            return;
        }

        countryCodePicker.registerCarrierNumberEditText(tel);
        telNo = countryCodePicker.getFullNumberWithPlus();


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(); // Firebase referansı alınıyor.

        DatabaseReference kullaniciRef = ref.child("kullanıcılar");
        DatabaseReference rehberRef = ref.child("rehberler");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String telefonNoVeritabani;
                    telefonNoVeritabani = ds.child("telNo").getValue(String.class);
                    String sifreVeritabani = ds.child("sifre").getValue(String.class);

                    if (telNo.equals(telefonNoVeritabani)) {
                        String ID = ds.getKey();
                        String tablo = dataSnapshot.getRef().getKey(); // Tablonun adını al
                        System.out.println(ID);
                        if (girisSifre.equals(sifreVeritabani)) {
                            Toast.makeText(getApplicationContext(), "Giriş Başarılı", Toast.LENGTH_SHORT).show();
                            assert tablo != null;
                            if (tablo.equals("kullanıcılar")) {

                                Intent intent = new Intent(MainActivity.this,KullaniciAnaSayfa.class);
                                intent.putExtra("userId",ID);
                                startActivity(intent);

                            } else if (tablo.equals("rehberler")) {
                                Toast.makeText(getApplication(), "rehber giriş yaptı", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, RehberAnaSayfa.class);
                                intent.putExtra("userId", ID);
                                startActivity(intent);
                            }
                            break;
                        } else {
                            Toast.makeText(getApplicationContext(), "Şifre yanlış. Lütfen doğru şifreyi girin.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        eslesme++;
                        System.out.println(eslesme);
                    }
                }
                if (eslesme > 2) {
                //    Toast.makeText(getApplicationContext(), "Eşleşen kullanıcı/rehber bulunamadı.", Toast.LENGTH_LONG).show();

                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Veri okuma hatası: " + databaseError.getCode());
            }
        };

        kullaniciRef.addListenerForSingleValueEvent(eventListener);
        rehberRef.addListenerForSingleValueEvent(eventListener);


    }
}
