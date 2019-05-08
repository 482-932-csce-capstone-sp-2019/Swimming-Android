package com.example.swimmingwearable;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log; //For debug testing remove at production
import android.view.View;
import android.widget.EditText;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //List of users stored as tuples (Name,ID)
    private ArrayList<Tuple<String,Integer>> userList = new ArrayList<>();
    private ArrayList<Tuple<String,Integer>> poolList = new ArrayList<>();
    private ArrayList<Tuple<String,Integer>> workoutList = new ArrayList<>();

    DatabaseHelper db = new DatabaseHelper(this);

    //Database controls
    //User Recyclerview
    UserRecyclerViewAdapter userAdapter;


    private File directory;
    private File preJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Need to get/create list of users.



        //Delete
        directory = Environment.getExternalStorageDirectory();
        File[] arrayfile = new File[0];
        arrayfile = directory.listFiles();
        for(int x = 0; x < arrayfile.length; x++){
            if(arrayfile[x].getAbsolutePath().contains("swimmingwearable")) {
                directory = arrayfile[x];
                break;
            }
        }

        String toDevice = "TESTING 123 NERDS";
        preJSON = new File(directory.getAbsolutePath()+"/workout-pre.json");

        FileOutputStream oStream = null;
        try {
            oStream = new FileOutputStream(preJSON.getAbsolutePath());
            oStream.write(toDevice.getBytes());
            oStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //executeSSHCommand();

        //delete

        initUserList(db);
    }

    //ENDDELETE

    public void executeSSHCommand(){
         //Get IP of pi
        Log.d("Boi", "1");
        MainActivity.ipFetchTask fetcher = new MainActivity.ipFetchTask();
       // String host = "192.0.0.1";
        Log.d("Boi", "2");
        try {
            fetcher.execute(new URL("https://swimfanatic.net:6443/uploads/bip.txt"));
            //host = fetcher.get();
        } catch (Exception e) {
            Log.d("SwimmingWearable", "Exception Ignored");
        }

        //If host is bad do error
        Log.d("Boi", "3");
    }

    protected class ipFetchTask extends AsyncTask<URL, Integer, String> {
        protected String doInBackground(URL... url){
            String host = "192.0.0.1";
            try {
                // Create a URL for the desired page
                BufferedReader in = new BufferedReader(new InputStreamReader( new URL("https://swimfanatic.net:6443/uploads/jip.txt").openStream()));
                String temp;
                while ((temp = in.readLine()) != null) {
                    host = temp;
                }
                in.close();

                //Perform the thing
                int port = 22;
                String user = "pi";
                String password = user;

                JSch jsch = new JSch();
                Session session = jsch.getSession(user, host, port);
                Log.d("Boi", "4");
                session.setPassword(password);
                session.setConfig("StrictHostKeyChecking", "no");
                Log.d("Boi", "5");
                session.setTimeout(10000);
                session.connect();

                Log.d("Boi", "6");
                ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
                sftp.connect();
                Log.d("Boi", "7");
                sftp.put(preJSON.getAbsolutePath(), "/home/pi/pi-stroker/workout-pre.json");
                Log.d("Boi", "8");
                //sftp.disconnect();

                //SFTP to give JSON
                /*ChannelExec channel = (ChannelExec)session.openChannel("exec");
                //FIXME:IMPLEMENT COMMAND NOW
                channel.setCommand("./python3 example.py");
                channel.connect();*/
                //recommended line???
                try{Thread.sleep(1000);}catch(Exception ee){}
                // channel.disconnect();
                session.disconnect();
                Log.d("Boi", "5");

            } catch (Exception e){
                Log.d("SwimmingWearable:", "Exception Ignored");
            }
            return host;
        }

        protected void onProgressUpdate(Integer... progress) {
            //No Progress shown
        }
    }

    //END DELETE


    public void addUser(View v){
        //handles pressing the new user button
        //Creates a popup to enter a new name

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter New User Name");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected;
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString();
                pushUser(name, db);
                //Update userlist
                updateUserList();
                userAdapter.notifyItemInserted(userList.size());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void updateUserList(){
        //Needed when a new user is inserted
        userList.clear();
        //Recreates the userlist with the new user.
        Integer numSwimmers = db.getNumSwimmers();
        for(Integer x = 0; x < numSwimmers; x++){
            Tuple<String,Integer> user = db.getSwimmer(x);
            userList.add(user);
        }
    }

    private void initUserList(DatabaseHelper db){
        /*FIXME: Remove later, just need to populate list
            db.addSwimmer("Alice");
            db.addSwimmer("Bob");
            db.addSwimmer("Carl");
            db.addSwimmer("Doug");
            db.addSwimmer("Ethan");
            db.addSwimmer("Frank");
            db.addSwimmer("Gigi");*/

        //get list of users from DB.
        Integer numSwimmers = db.getNumSwimmers();
        for(Integer x = 0; x < numSwimmers; x++){
            Tuple<String,Integer> user = db.getSwimmer(x);
            userList.add(user);
        }

        initUserRecycler();
    }

    private void initUserRecycler(){
        RecyclerView userRecycler = findViewById(R.id.recyclerUserList);

        userAdapter = new UserRecyclerViewAdapter(this, userList);
        userRecycler.setAdapter(userAdapter);
        userRecycler.setLayoutManager(new LinearLayoutManager(this));
    }



    //FIXME: POOL CODE NOT NEEDED IN MAIN
    private void initPoolList(Integer userID, DatabaseHelper db){
        //List to populate the pool selection
        //FIXME: get list of pools from DB, loop to create them.
        poolList = db.getPools(userID);
        return;
        //FIXME: End Drew's Part
        //Entries are same as UserList, name and then ID
    }

    private void initPoolRecycler(){
        RecyclerView poolRecycler = findViewById(R.id.recyclerPoolList);
        //FIXME: Hardcoded uID, shouldn't even be in main
        PoolRecyclerViewAdapter adapter = new PoolRecyclerViewAdapter(this, poolList,7);
        poolRecycler.setAdapter(adapter);
        poolRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initWkoutList(Integer userID){
        //List to populate the old workouts selection
        //FIXME: get list of workouts from DB, loop to create them.

        workoutList = db.getWorkoutInfo(userID);

        //Entries are same as UserList, name and then ID
        //Note: feel free to create a string function to get the name if it gets too cluttered.
    }

    private void initWkoutRecycler(){
        RecyclerView workoutRecycler = findViewById(R.id.recyclerWorkoutList);
        //FIXME: wrong adapter
        UserRecyclerViewAdapter adapter = new UserRecyclerViewAdapter(this, workoutList);
        workoutRecycler.setAdapter(adapter);
        workoutRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    private void pushUser(String name, DatabaseHelper db){
        db.addSwimmer(name);
    }



    private void pushWorkout(ArrayList<ArrayList<Lap> > laps, Integer distance){
        //FIXME: add new workout (minus resulting pace) and return ID

        Integer lapSwimID = db.getLastLapSwimID() + 1;
        Integer lapWorkoutID = db.getLastLapWorkoutID() + 1;
        for(ArrayList<Lap> a : laps){
            for (Lap l : a){
                //db.addLap(lapSwimID, l.stroke, l.goal, l.delay, l.actual, lapWorkoutID);
            }
            lapSwimID = lapSwimID + 1;
        }

        //Please change arguments to fit whatever order you want
    }






}
