package com.ft.hacibul;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class KayitSecenekSayfasi extends AppCompatActivity {

    private Button kullaniciKayit;
    private  Button rehberKayit;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kayit_secenek_sayfasi);
        kullaniciKayit = findViewById(R.id.kullaniciKayitOl);
        rehberKayit = findViewById(R.id.rehberKayitOl);

        rehberKayit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(KayitSecenekSayfasi.this,SmsKayit1.class);
                intent.putExtra("flag","0");
                finish();
                startActivity(intent);


            }
        });

        kullaniciKayit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(KayitSecenekSayfasi.this,SmsKayit1.class);
                intent.putExtra("flag","1");
                finish();
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent backIntent = new Intent(KayitSecenekSayfasi.this,MainActivity.class);
        finish();
        startActivity(backIntent);

    }
}