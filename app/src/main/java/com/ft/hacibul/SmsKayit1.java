package com.ft.hacibul;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

public class SmsKayit1 extends AppCompatActivity {

    private EditText telNo;
    private Button kayitOl;
    private String telefon;
    private FirebaseAuth firebaseAuth;
    CountryCodePicker countryCodePicker;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_kayit1);

        kayitOl = findViewById(R.id.kodGonder);
        telNo = findViewById(R.id.editTextTelefonNo);
        countryCodePicker = findViewById(R.id.login_countrycode);

        kayitOl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(TextUtils.isEmpty(telNo.getText().toString()))
                {
                    Toast.makeText(getApplicationContext(),"Telefon numarası alanı boş bırakılamaz",Toast.LENGTH_LONG).show();
                }
                countryCodePicker.registerCarrierNumberEditText(telNo);
                telefon = countryCodePicker.getFullNumberWithPlus();


                firebaseAuth = FirebaseAuth.getInstance();
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("kullanıcılar");
                DatabaseReference rehberRef = FirebaseDatabase.getInstance().getReference("rehberler");

                Query queryKullanici = usersRef.orderByChild("telNo").equalTo(telefon);
                queryKullanici.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(getApplicationContext(), "Bu telefon numarası zaten bir hesapla ilişkilendirilmiş. Lütfen başka bir telefon numarası kullanın veya giriş yapın.", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Query queryRehber = rehberRef.orderByChild("telNo").equalTo(telefon); // Aradığınız telefon numarasını kullanın
                            queryRehber.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Toast.makeText(getApplicationContext(), "Bu telefon numarası zaten bir hesapla ilişkilendirilmiş. Lütfen başka bir telefon numarası kullanın veya giriş yapın.", Toast.LENGTH_LONG).show();
                                    }else
                                    {
                                        Intent intent = new Intent(SmsKayit1.this, SmsKayit2.class);
                                        intent.putExtra("telNo",telefon);
                                        intent.putExtra("flag",getIntent().getStringExtra("flag"));
                                        finish();
                                        startActivity(intent);
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Hata işleme kodları burada
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Hata işleme kodları burada
                    }
                });
            }
        });
    }
}