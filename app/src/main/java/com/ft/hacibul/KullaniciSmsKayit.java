package com.ft.hacibul;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class KullaniciSmsKayit extends AppCompatActivity {

    private  String telNo;

    private  EditText ad;
    private   EditText soyad;

    private EditText sifre;
    private HashMap <String, Object> mData;

    String userId;
    private Button kayitOl;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kullanici_sms_kayit);

        telNo = getIntent().getStringExtra("telNo");
        userId = getIntent().getStringExtra("userId");
        ad = findViewById(R.id.editTextAd);
        soyad = findViewById(R.id.editTextSoyad);
        sifre = findViewById(R.id.editTextSifre);
        kayitOl = findViewById(R.id.kullaniciSmsKayitOl);


        kayitOl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(ad.getText().toString()) || TextUtils.isEmpty(soyad.getText().toString()) || TextUtils.isEmpty(sifre.getText().toString()))
                {
                    Toast.makeText(getApplicationContext(),"Boş Alan Bırakılamaz",Toast.LENGTH_SHORT).show();
                }

                else {
                    veritabanınaKayıtEt();
                    Intent intent = new Intent(KullaniciSmsKayit.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

    }


    public void veritabanınaKayıtEt()
    {


        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference(); // mReference'ı başlat

        mData = new HashMap<>();
        mData.put("ad",ad.getText().toString());
        mData.put("soyad",soyad.getText().toString());
        mData.put("sifre",sifre.getText().toString());
        mData.put("telNo",telNo);
        mData.put("konum","");



        mReference.child("kullanıcılar").child(userId)
                .setValue(mData)
                .addOnCompleteListener(KullaniciSmsKayit.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(KullaniciSmsKayit.this,"Kayıt İşlemi Başarılı",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(KullaniciSmsKayit.this,"Kayıt İşlemi Başarısız",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }


}