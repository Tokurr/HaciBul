package com.ft.hacibul;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RehberAnaSayfa extends AppCompatActivity {



    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private DatabaseReference reference;
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<String> üyeList = new ArrayList<>();
    private ArrayList<String> kullaniciAdList = new ArrayList<>();
    private ArrayList<String> üyeAdList = new ArrayList<>();
    private String userId;

    private  Button üyeListe;
    private Button liste;

    private Button konum;
    String source;
    private HashMap<String,Object> mData;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rehber_ana_sayfa);


        liste = findViewById(R.id.liste);
        üyeListe = findViewById(R.id.üyeListesi);


        userId = getIntent().getStringExtra("userId");





        liste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                kullaniciListesi();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000); // 1000 milisaniye (1 saniye) bekleyin
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(RehberAnaSayfa.this,RehberKullaniciListesi.class);
                        intent.putExtra("kullaniciAdList",kullaniciAdList);
                        intent.putExtra("list",list);
                        intent.putExtra("üyeList",üyeList);
                        intent.putExtra("userId",userId);
                        startActivity(intent);

                    }
                }).start();



            }
        });

        üyeListe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                üyeListesi();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000); // 1000 milisaniye (1 saniye) bekleyin
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(RehberAnaSayfa.this,RehberUyeListesi.class);
                        intent.putExtra("üyeList",üyeList);
                        intent.putExtra("userId",userId);
                        intent.putExtra("üyeAdList",üyeAdList);

                        startActivity(intent);

                    }
                }).start();



            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestBackgroundLocationPermission();
        } else {
            requestLocationPermission();
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void requestBackgroundLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationService();
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService();
            } else {
                // Kullanıcı izin vermediğinde yapılacak işlemler
            }
        }
    }

    private void startLocationService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8 ve üstü sürümler için başlatma işlemi
            //startForegroundService(new Intent(this, MyLocationService.class));


         //   System.out.println(userId + "     1. yer");
            Intent serviceIntent = new Intent(RehberAnaSayfa.this, MyLocationService.class);
            serviceIntent.putExtra("userId", userId); // userId'yi ekleyin
            serviceIntent.putExtra("bilgi","rehber");
            startForegroundService(serviceIntent);
        } else {
            // Android 7 ve altı sürümler için başlatma işlemi
        //    System.out.println(userId + "     2. yer");
            Intent serviceIntent = new Intent(RehberAnaSayfa.this, MyLocationService.class);
            serviceIntent.putExtra("userId", userId); // userId'yi ekleyin
            serviceIntent.putExtra("bilgi","rehber");
            startService(serviceIntent);
        }
    }


    private void kullaniciListesi()
    {

        reference = FirebaseDatabase.getInstance().getReference().child("kullanıcılar");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                    int bayrak = 0;

                    String userId = userSnapshot.getKey(); // User ID'yi al
                    String ad = userSnapshot.child("ad").getValue(String.class); // "ad" adlı özelliği al
                    String soyad = userSnapshot.child("soyad").getValue(String.class); // "soyad" adlı özelliği al


                    for (int i = 0; i < list.size(); i++) {

                        if(list.get(i).equals(userId))
                        {
                            bayrak = 1;
                            break;
                        }

                    }

                    if(bayrak == 0)
                    {
                        list.add(userId);
                        kullaniciAdList.add("ad: " + ad + "\nsoyad: " + soyad);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void üyeListesi()
    {


        üyeList.clear();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("rehberler");


        String specificUserId = userId; // Örnek bir user ID
        DatabaseReference userRef = ref.child(specificUserId).child("üyeler");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userId1 = userSnapshot.getKey();
                       // String ad = userSnapshot.child("ad").getValue(String.class); // "ad" adlı özelliği al
                       // String soyad = userSnapshot.child("soyad").getValue(String.class); // "soyad" adlı özelliği al
                        int bayrak = 0;
                        for (int i = 0; i < üyeList.size(); i++) {


                            if(üyeList.get(i).equals(userId1))
                            {

                                bayrak = 1;
                                break;
                            }

                        }

                        if(bayrak == 0)
                        {

                            üyeList.add(userId1);

                        }


                    }


                } else {
                    System.out.println("Sonuç bulunamadı.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Veritabanı hatası: " + databaseError.getMessage());
            }
        });

    }






}