package com.ft.hacibul;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class RehberKullaniciListesi extends AppCompatActivity {

    private ListView mListView;
    private ArrayAdapter<String> adapter;

    private DatabaseReference reference;
    private ArrayList<String> list = new ArrayList<>();


    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rehber_kullanici_listesi);


        mListView = findViewById(R.id.liste);

        list = getIntent().getStringArrayListExtra("list");
        userId = getIntent().getStringExtra("userId");


        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                showAlertDialog(list.get(i).toString());

            }
        });

    }


    private void showAlertDialog(String selectedItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ekleme İşlemi");
        builder.setMessage(selectedItem + " seçildi. Ekleme işlemini yapmak istiyor musunuz?");
        builder.setPositiveButton("Ekle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String[] parcalar = selectedItem.split("\n");


                rehberUyeEkle(parcalar[1]);
                kullaniciRehberEkle(parcalar[1]);
                dialog.dismiss();


            }
        });
        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void rehberUyeEkle(String id)
    {
        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();

        HashMap mData;
        mData = new HashMap<>();
        mData.put("ad","");
        mData.put("soyad","");
        mData.put("sifre","");
        mData.put("telNo","");
        mReference.child("rehberler").child(userId).child("üyeler").child(id)
                .setValue(mData)
                .addOnCompleteListener(RehberKullaniciListesi.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(RehberKullaniciListesi.this,"Kayıt işlemi başarılı",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(RehberKullaniciListesi.this,"Kayıt işlemi başarısız",Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }


    private void kullaniciRehberEkle(String id)
    {
        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();

        HashMap mData;
        mData = new HashMap<>();
        mData.put("ad","");
        mData.put("soyad","");
        mData.put("sifre","");
        mData.put("telNo","");
        mReference.child("kullanıcılar").child(id).child("rehber").child(userId)
                .setValue(mData)
                .addOnCompleteListener(RehberKullaniciListesi.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(RehberKullaniciListesi.this,"Kayıt işlemi başarılı",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(RehberKullaniciListesi.this,"Kayıt işlemi başarısız",Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }





}