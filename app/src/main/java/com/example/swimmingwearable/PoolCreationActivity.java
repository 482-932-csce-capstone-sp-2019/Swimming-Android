package com.example.swimmingwearable;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class PoolCreationActivity extends AppCompatActivity {

    private Integer UserID;
    DatabaseHelper db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poolcreation);

        Intent i = getIntent();
        UserID = i.getIntExtra("UserID", -1);

        db = new DatabaseHelper(this);



    }

    public void addPool(View view){
        //The one

        TextView n = findViewById(R.id.newPoolName);
        TextView d = findViewById(R.id.newPoolLength);
        //FIXME: SPinner

        pushPool(n.getText().toString(), Integer.parseInt(d.getText().toString()), "Yards", UserID, db);

        Intent i = new Intent(this, PoolSelectionActivity.class);
        i.putExtra("UserID", UserID);
        startActivity(i);

    }

    private void pushPool(String name, Integer length, String unitType, Integer swimmerID, DatabaseHelper db){
        db.addPool(name, length, unitType, swimmerID);
    }
}
