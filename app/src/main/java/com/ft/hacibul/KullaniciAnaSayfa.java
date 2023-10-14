package com.ft.hacibul;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class KullaniciAnaSayfa extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private String userId;
    private String rehberKonum;
    private String kullaniciKonum;

    private ProgressBar progressBar;
    private Handler handler;
    private Button rehberBul;
    private  String rehberUserId;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kullanici_ana_sayfa);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        userId = getIntent().getStringExtra("userId");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestBackgroundLocationPermission();
        } else {
            requestLocationPermission();
        }

        rehberBul = findViewById(R.id.rehber_bul);

        rehberBul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rehberiniBul();


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000); // 1000 milisaniye (1 saniye) bekleyin
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // bekleme bittikten sonra burası gerçekleştirilir
                        System.out.println(kullaniciKonum);
                        System.out.println(rehberKonum);
                        if(kullaniciKonum!=null && rehberKonum!=null)
                        {
                            haritadaGoster(kullaniciKonum, rehberKonum);
                        }

                    }
                }).start();

            }
        });





    }

    private void rehberiniBul()
    {


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rehberRef = database.getReference("kullanıcılar").child(userId).child("rehber");

        rehberRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Veri değiştiğinde bu metot çağrılır
                for (DataSnapshot rehberSnapshot : dataSnapshot.getChildren()) {
                    // Rehberin userID'sini alabilirsiniz
                    rehberUserId = rehberSnapshot.getKey();


                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("rehberler");
                    DatabaseReference userReference = databaseReference.child(rehberUserId);

                    userReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Verileri çekmek için bu metodu kullanabilirsiniz
                            if (dataSnapshot.exists()) {
                                // dataSnapshot içindeki verilere erişin
                                rehberKonum = dataSnapshot.child("konum").getValue(String.class);
                                System.out.println(rehberKonum);

                                // Diğer işlemleri burada yapabilirsiniz
                            } else {
                                System.out.println("kullanıcı verileri bulunamadı");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Hata durumunda burada işlemler yapabilirsiniz
                        }
                    });


                    // İşlem yapabilirsiniz
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Hata durumunda çağrılır
            }
        });


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("kullanıcılar");
        DatabaseReference userReference = databaseReference.child(userId);

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Verileri çekmek için bu metodu kullanabilirsiniz
                if (dataSnapshot.exists()) {
                    // dataSnapshot içindeki verilere erişin
                    kullaniciKonum = dataSnapshot.child("konum").getValue(String.class);
                    System.out.println(kullaniciKonum);


                } else {
                    // Kullanıcı verileri bulunamadı
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Hata durumunda burada işlemler yapabilirsiniz
            }
        });

        /*
        rehberBul.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                handler.postDelayed(this, 10000);
                progressBar.setVisibility(View.INVISIBLE);
            }
        }, 10000);
*/


        /*
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                hedefKonumuGuncelle(rehberUserId);
                handler.postDelayed(this, 10000);
                if(kullaniciKonum!=null && rehberKonum!=null)
                {
                    haritadaGoster(kullaniciKonum,rehberKonum);

                }


            }
        }, 10000);
*/

    }

    /*
    private void hedefKonumuGuncelle(String rehberUserId)
    {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("rehberler");
        DatabaseReference userReference = databaseReference.child(rehberUserId);

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Verileri çekmek için bu metodu kullanabilirsiniz
                if (dataSnapshot.exists()) {
                    // dataSnapshot içindeki verilere erişin
                    rehberKonum = dataSnapshot.child("konum").getValue(String.class);

                    // Diğer işlemleri burada yapabilirsiniz
                } else {
                    // Kullanıcı verileri bulunamadı
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Hata durumunda burada işlemler yapabilirsiniz
            }
        });

    }

*/
    private void haritadaGoster(String kullaniciKonum, String rehberKonum)
    {

        /*
        Uri uri = Uri.parse("https://www.google.com/maps/dir/" + kullaniciKonum  + "/" + rehberKonum);
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        intent.setPackage("com.google.android.apps.maps");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        */

        Uri uri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + rehberKonum);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void requestBackgroundLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationService();
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
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



            Intent serviceIntent = new Intent(KullaniciAnaSayfa.this, MyLocationService.class);
            serviceIntent.putExtra("userId", userId); // userId'yi ekleyin
            serviceIntent.putExtra("bilgi","kullanıcı");
            startForegroundService(serviceIntent);
        } else {
            // Android 7 ve altı sürümler için başlatma işlemi

            Intent serviceIntent = new Intent(KullaniciAnaSayfa.this, MyLocationService.class);
            serviceIntent.putExtra("userId", userId); // userId'yi ekleyin
            serviceIntent.putExtra("bilgi","kullanıcı");
            startService(serviceIntent);
        }
    }

}