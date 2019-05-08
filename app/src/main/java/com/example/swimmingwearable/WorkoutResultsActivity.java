package com.example.swimmingwearable;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WorkoutResultsActivity extends AppCompatActivity {

    private Integer WorkoutID;
    private DatabaseHelper db;

    private ArrayList<ArrayList<Lap>> Workout; //The Grail

    private List<Integer> sets;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postswim);

        Intent i = getIntent();
        WorkoutID = i.getIntExtra("WorkoutID", -1);
        //FIXME: Error checking

        db = new DatabaseHelper(this);

        //Workout = getWorkoutDetails(WorkoutID, db);
        Workout = demoWkout();


        //Avalon
        sets = new ArrayList<Integer>();
        for(int a = 0; a < Workout.size(); a++){
            sets.add(a+1);
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, R.layout.layout_spinneritem, sets);

        //((Spinner) findViewById(R.id.setSpinner)).setAdapter(adapter);

        display(0);
    }

    private ArrayList<ArrayList<Lap>> demoWkout(){
        ArrayList<ArrayList<Lap>> w = new ArrayList<ArrayList<Lap>>();

      /*  public class Lap {
            public String stroke;
            public int distance;
            public int goal; //in seconds
            public int actual; //in seconds
            public int delay;
            public int swimID;
        }*/
      ArrayList<Lap> set = new ArrayList<>();
      Lap l = new Lap();
      l.actual = 88;
      set.add(l);
      l.actual = 92;
      set.add(l);
      l.actual = 83;
      set.add(l);

      w.add(set);
        l.actual = 105;
        set.add(l);
        l.actual = 108;
        set.add(l);
        l.actual = 95;
        set.add(l);

        w.add(set);



        return w;
    }

    //Rhongomyniad
    private void display(Integer i){
        ArrayList<Lap> set = Workout.get(i);

        Integer avg = 0;
        Integer slow = Integer.MAX_VALUE;
        Integer fast = Integer.MIN_VALUE;
        Integer total = 0;

        for(int j = 0; j < set.size(); j++){
            avg += set.get(i).actual;
            total += set.get(i).actual;
            if(set.get(i).actual > fast){
                fast = set.get(i).actual;
            }
            if(set.get(i).actual < slow){
                slow = set.get(i).actual;
            }
        }
        avg = avg/(set.size()+1);
        ((TextView) findViewById(R.id.textAvg)).setText(prep(avg));
        ((TextView) findViewById(R.id.textSlow)).setText(prep(slow));
        ((TextView) findViewById(R.id.textFast)).setText(prep(fast));
        ((TextView) findViewById(R.id.textTotal)).setText(prep(total));
        ((TextView) findViewById(R.id.textGoal)).setText(((Integer)set.get(0).goal).toString());


    }

    //Bedivere
    private String prep(Integer i){
        Integer j = i / 60;
        Integer k = i % 60;
        String l = k.toString();
        if (l.length() < 2){
            l = "0" + l;
        }
        String text = j.toString() + ":" + l;
        return text;
    }

    //Parzival
    private ArrayList<ArrayList<Lap>> getWorkoutDetails(Integer workoutID, DatabaseHelper db){
        ArrayList<ArrayList <Lap> > workoutLaps = db.getWorkoutDetailsID(workoutID);
        return workoutLaps;
    }


}
