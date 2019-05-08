package com.example.swimmingwearable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "sqlDbExample";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sqlSwimmers = "CREATE TABLE swimmers(swimmerID INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR)";
        String sqlPool = "CREATE TABLE pools(poolID INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, distance INTEGER, distanceUnit VARCHAR, swimmerID INTEGER, FOREIGN KEY(swimmerID) REFERENCES swimmers(swimmerID))";
        String sqlWorkout = "CREATE TABLE workouts(workoutID INTEGER PRIMARY KEY AUTOINCREMENT, startTime DATETIME, swimmerID INTEGER, poolID INTEGER, FOREIGN KEY(swimmerID) REFERENCES swimmers(swimmerID), FOREIGN KEY(poolID) REFERENCES pools(poolID))";
        String sqlLaps = "CREATE TABLE laps(lapID INTEGER PRIMARY KEY AUTOINCREMENT, swimID INTEGER, stroke VARCHAR, goal INTEGER, startTime INTEGER, workoutID INTEGER, result INTEGER, FOREIGN KEY(workoutID) REFERENCES workouts(workoutID))";
        //All time is in seconds

        sqLiteDatabase.execSQL(sqlSwimmers);
        sqLiteDatabase.execSQL(sqlPool);
        sqLiteDatabase.execSQL(sqlWorkout);
        sqLiteDatabase.execSQL(sqlLaps);
    }

    public boolean addSwimmer(String name){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        db.insert("swimmers", null, contentValues);

        db.close();
        return true;
    }

    public boolean addPool(String name, Integer distance, String distanceUnit, Integer swimmerID){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("swimmerID", swimmerID);
        contentValues.put("distance", distance);
        contentValues.put("distanceUnit", distanceUnit);
        db.insert("pools", null, contentValues);


        db.close();
        return true;
    }

    public Integer getNumSwimmers(){
        SQLiteDatabase database = getReadableDatabase();
        Cursor curs = database.query("swimmers", null, null, null, null, null, null);
        return curs.getCount();
    }

    public Integer getNumWorkouts(){
        SQLiteDatabase database = getReadableDatabase();
        Cursor curs = database.query("workouts", null, null, null, null, null, null);
        return curs.getCount();
    }

    public Tuple<String, Integer> getSwimmer(Integer index){
        SQLiteDatabase database = getReadableDatabase();
        //query(table, columns(SELECT), selection(WHERE), ...)
        Cursor curs = database.query("swimmers", null, null, null, null, null, null);
        curs.moveToFirst();
        curs.moveToPosition(index);
        String name = curs.getString(1);
        Integer id = curs.getInt(0);
        Tuple<String, Integer> user = new Tuple<String, Integer>(name, id);
        return user;
    }

    public Integer getNumPools(){
        SQLiteDatabase database = getReadableDatabase();
        Cursor curs = database.query("pools", null, null, null, null, null, null);
        return curs.getCount();
    }

    public ArrayList<Tuple<String, Integer> > getPools(Integer userID){
        SQLiteDatabase database = getReadableDatabase();
        ArrayList<Tuple<String, Integer> > allPools = new ArrayList<Tuple<String, Integer> >();

        Cursor curs = database.query("pools", null, null, null, null, null, null);
        curs.moveToFirst();
        while(curs.isAfterLast() != true){
            if(curs.getInt(4) == userID){
                Integer ID = curs.getInt(0);
                String name = curs.getString(1);
                Tuple<String, Integer> workoutInfo = new Tuple<>(name, ID);
                allPools.add(workoutInfo);
            }
            curs.moveToNext();
        }
        return allPools;
    }

    public boolean addLap(Integer swimID, String stroke, Integer goal, Integer startTime, Integer result, Integer workoutID){
        SQLiteDatabase db= getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("swimID", swimID);
        contentValues.put("stroke", stroke);
        contentValues.put("goal", goal);
        contentValues.put("result", result);
        contentValues.put("startTime", startTime);
        contentValues.put("workoutID", workoutID);
        db.insert("laps", null, contentValues);
        db.close();

        return true;
    }

    public Integer getLastLapSwimID(){
        SQLiteDatabase db  = getReadableDatabase();

        Cursor curs = db.query("laps", null, null, null, null, null, null);
        curs.moveToLast();
        return curs.getInt(1);

    }

    public Integer getLastLapWorkoutID(){
        SQLiteDatabase db  = getReadableDatabase();

        Cursor curs = db.query("laps", null, null, null, null, null, null);
        curs.moveToLast();
        return curs.getInt(5);
    }

    public ArrayList<Tuple<String, Integer> > getWorkoutInfo(Integer userID){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Tuple<String, Integer> > allWorkouts = new ArrayList<Tuple<String, Integer> >();

        Cursor curs = db.query("workouts", null, null, null, null, null, null);
        curs.moveToFirst();
        while(curs.isAfterLast() != true) {
            if(curs.getInt(2) == userID){
                Integer ID = curs.getInt(0);
                String dateTime = curs.getString(1);
                Tuple<String, Integer> workoutInfo = new Tuple<>(dateTime, ID);
                allWorkouts.add(workoutInfo);
            }
            curs.moveToNext();
        }
        return allWorkouts;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String sqlWorkouts= "DROP TABLE IF EXISTS workouts";
        String sqlPool = "DROP TABLE IF EXISTS pools";
        String sqlSwimmers = "DROP TABLE IF EXISTS swimmers";
        String sqlLaps = "DROP TABLE IF EXISTS laps";

        sqLiteDatabase.execSQL(sqlWorkouts);
        sqLiteDatabase.execSQL(sqlPool);
        sqLiteDatabase.execSQL(sqlSwimmers);
        sqLiteDatabase.execSQL(sqlLaps);

        onCreate(sqLiteDatabase);
    }

    public ArrayList<ArrayList<Lap>> getWorkoutDetailsID(Integer workoutID){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<ArrayList<Lap> > allWorkoutLaps = new ArrayList<ArrayList<Lap> >();
        ArrayList<Integer> correspondingSwimIDs = new ArrayList<Integer>();

        Cursor curs = db.query("laps", null, null, null, null, null, null);
        while(curs.isAfterLast() != true){
            Integer currentWorkoutID = curs.getInt(5);
            if(currentWorkoutID == workoutID){
                Integer currentSwimID = curs.getInt(1);
                String currentStroke = curs.getString(2);
                Integer currentGoal = curs.getInt(3);
                Integer currentStartTime = curs.getInt(4);
                Integer currentResult = curs.getInt(6);

                Lap currentLap = new Lap();
                currentLap.stroke = currentStroke;
                currentLap.actual = currentResult;
                currentLap.goal = currentGoal;
                currentLap.delay = currentStartTime;
                currentLap.swimID = currentSwimID;

                Integer swim = -1;
                for(int x = 0; x < correspondingSwimIDs.size(); x++){
                    if(correspondingSwimIDs.get(x) == currentLap.swimID){
                        swim = x;
                    }
                }

                if(swim == -1){
                    ArrayList<Lap> newSwim = new ArrayList<Lap>();
                    correspondingSwimIDs.add(currentLap.swimID);
                    newSwim.add(currentLap);
                    allWorkoutLaps.add(newSwim);
                }
                else{
                    ArrayList<Lap> existingSwim = allWorkoutLaps.get(swim);
                    existingSwim.add(currentLap);
                    allWorkoutLaps.set(swim, existingSwim);
                }


            }
        }
        return allWorkoutLaps;
    }
}