package com.ft.hacibul;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class RehberUyeListesi extends AppCompatActivity {

    private ListView mListView;
    private ArrayAdapter<String> adapter;

    private DatabaseReference reference;
    private ArrayList<String> üyeList = new ArrayList<>();

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


            }
        });



    }
}