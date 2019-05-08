package com.example.swimmingwearable;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import static android.support.v4.content.ContextCompat.startActivity;

public class MenuActivity  extends AppCompatActivity {

    private Integer userID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        Intent intent = getIntent();
        //Can get selected userID and name from intent getExtra methods
        //Can set text as needed.
        userID = intent.getIntExtra("UserID", -1);
        if (userID == -1){
            //FIXME: MAJOR ERROR
        }
    }

    public void newWorkout(View view){
        //Move to pool selection thing, pass selected userID and name
        Intent intent = new Intent(this, PoolSelectionActivity.class);
        intent.putExtra("UserID", userID);
        startActivity(intent);

    }

    public void viewWorkout(){
        //Move to pastWorkouts Activity, pass userID and name
        Intent intent = new Intent(this, WorkoutSelectionActivity.class);
        intent.putExtra("UserID", userID);
        startActivity(intent);
    }
}
