package com.example.swimmingwearable;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

public class PoolSelectionActivity extends AppCompatActivity{

    private ArrayList<Tuple<String,Integer>> poolList = new ArrayList<>();
    private Integer userID;

    DatabaseHelper db = new DatabaseHelper(this);

    //Database controls
    //User Recyclerview
    PoolRecyclerViewAdapter adapter;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poolselection);



        //FIXME: potential error: new databasehelper
        DatabaseHelper db = new DatabaseHelper(this);


        Intent intent = getIntent();
        //Can get selected userID and name from intent getExtra methods
        userID = intent.getIntExtra("UserID", -1);
        //Can set text as needed.

        initPoolList(userID, db);

    }

    public void newPool(View view){
        Intent i = new Intent(this, PoolCreationActivity.class);
        i.putExtra("UserID", userID);
        startActivity(i);
    }



    private void updatePoolList(Integer userID, DatabaseHelper db){
        poolList.clear();
        //initPoolList(userID, db);
        poolList = db.getPools(userID);
    }

    private void initPoolList(Integer userID, DatabaseHelper db){
        //List to populate the pool selection
        //FIXME: get list of pools from DB, loop to create them.
        poolList = db.getPools(userID);
        initPoolRecycler();

        return;
    }

    private void initPoolRecycler(){
        RecyclerView poolRecycler = findViewById(R.id.recyclerPoolList);
        adapter = new PoolRecyclerViewAdapter(this, poolList, userID);
        //FIXME: Hardcoded uID, shouldn't even be in main
        poolRecycler.setAdapter(adapter);
        poolRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

}
