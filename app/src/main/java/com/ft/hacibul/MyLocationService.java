package com.ft.hacibul;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyLocationService extends Service implements LocationListener {

    private LocationManager locationManager;
    String userId;
    String bilgi;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onCreate() {
        super.onCreate();



        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8 ve üstü sürümler için bildirim kanalı oluştur
            createNotificationChannel();
        }

        // Servisi Foreground moda al
        startForeground(1, createNotification());
    }


    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Log.d("Konum", "x: " + latitude + ", y: " + longitude);
        String source = String.valueOf(latitude) + ", " + String.valueOf(longitude);
        konumGuncelle(source);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        userId = intent.getStringExtra("userId");
        bilgi = intent.getStringExtra("bilgi");
        if (userId != null) {


        } else {
           System.out.println("hata");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 0, this);
        }

        return START_STICKY;
    }




    private Notification createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "LocationChannel")
                .setContentTitle("Konum Güncellemesi")
                .setContentText("Konum güncellemesi alınıyor...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        return builder.build();
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                NotificationChannel channel = new NotificationChannel("LocationChannel",
                        "Location Channel",
                        NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(channel);
            }
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }


    private void konumGuncelle(String source)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference kullaniciRef = null;
        if(bilgi.equals("rehber"))
        {
             kullaniciRef = database.getReference("rehberler").child(userId);
        }

        else if(bilgi.equals("kullanıcı"))
        {
            kullaniciRef = database.getReference("kullanıcılar").child(userId);
        }

        kullaniciRef.child("konum").setValue(source, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    // Güncelleme sırasında hata oluştu
                    Log.d("Firebase", "Konum güncelleme hatası: " + databaseError.getMessage());
                } else {
                    // Konum başarıyla güncellendi
                    Log.d("Firebase", "Konum başarıyla güncellendi.");
                }
            }
        });
    }



}
