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

public class RehberUyeListesi extends AppCompatActivity {

    private ListView mListView;
    private ArrayAdapter<String> adapter;

    private DatabaseReference reference;
    private ArrayList<String> üyeList = new ArrayList<>();
    private ArrayList<String> üyeAdList = new ArrayList<>();

    String userId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rehber_uye_listesi);

        mListView = findViewById(R.id.listee);

        üyeList = getIntent().getStringArrayListExtra("üyeList");
        userId = getIntent().getStringExtra("userId");




        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,üyeList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                showAlertDialog(üyeList.get(i).toString());

            }
        });


    }

    private void showAlertDialog(String selectedItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Silme işlemi");
        builder.setMessage(selectedItem + "silmek istiyor musun?");
        builder.setPositiveButton("sil", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {



                üyeList.remove(selectedItem);
                rehberUyeSil(selectedItem);
                kullaniciRehberSil(selectedItem);
                dialog.dismiss();

                adapter.notifyDataSetChanged();
                mListView.setAdapter(adapter);
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

    private void rehberUyeSil(String üyeId)
    {

        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();


        mReference.child("rehberler").child(userId).child("üyeler").child(üyeId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RehberUyeListesi.this, "Silme işlemi başarılı", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RehberUyeListesi.this, "Silme işlemi başarısız", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void kullaniciRehberSil(String üyeId)
    {
        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
        mReference.child("kullanıcılar").child(üyeId).child("rehber").child(userId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RehberUyeListesi.this, "Silme işlemi başarılı", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RehberUyeListesi.this, "Silme işlemi başarısız", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }



}