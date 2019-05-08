package com.example.swimmingwearable;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class WorkoutCreationActivity extends AppCompatActivity {

    //Rebuild extendable recycler view to get information
    //

    /*private void extendWorkout(View view){
        //On click for extending recycler view
        //insert new "layout_workoutlistitem"
        //maybe store array of items in it here, just cheat.
    }*/

    private Integer UserID;
    private Integer PoolID;
    private DatabaseHelper db;

    private File directory;
    private File preJSON;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workoutcreation);

        directory = Environment.getExternalStorageDirectory();
        File[] arrayfile = directory.listFiles();
        for(int x = 0; x < arrayfile.length;x++){
            if(arrayfile[x].getAbsolutePath().contains("swimmingwearable")) {
                directory = arrayfile[x];
                break;
            }
        }

        Intent i = getIntent();
        UserID = i.getIntExtra("UserID", -1);
        PoolID = i.getIntExtra("PoolID", -1);
        //FIXME: Error checking

        db = new DatabaseHelper(this);

    }

    public void startWorkout(View view){
        //Parse textbox into json format
        TextView t = findViewById(R.id.workoutDefinition);
        String workout = t.getText().toString();

       ArrayList<ArrayList<Lap>> w = parse(workout);
        //Push workout
            //
        //parse to JSON
            //Basically the opposite of parse
        //send JSON to machine
            //SCP work
        //Start machine
        executeSSHCommand();

        Intent i = new Intent(this, SwimActivity.class);
        startActivity(i);

    }

    //FIXME:Need to implement actual absh command
    public void executeSSHCommand(){
        String user = "pi";
        String password = "pi";

        //Get IP of pi
        ipFetchTask fetcher = new ipFetchTask();
        // String host = "192.0.0.1";
        Log.d("Boi", "2");
        try {
            fetcher.execute(new URL("https://swimfanatic.net:6443/uploads/jip.txt"));
            //host = fetcher.get();
        }
        catch(Exception e){
            //Exception ignored
            Log.d("SwimmingWearable: ", e.getMessage());
        }
    }

    private ArrayList<ArrayList<Lap>> parse(String s){
        int end = 0;
        ArrayList<Integer> raw = new ArrayList<>();
        for(int x = 0; x < s.length(); x++){
            char c = s.charAt(x);
            if(c==','){
                raw.add(Integer.parseInt(s.substring(end,x)));
                end = x+1;
            }
        }



        //Reps
        //Distance
        //Interval
        //Goal
        ArrayList<ArrayList<Lap>> w = new ArrayList<>();

        Integer sets = raw.get(0); //Number of sets total
        ArrayList<Integer> rests = new ArrayList<>();
        for(int i = 1; i < sets; i++){
            //For the number of sets -1 add a rest time
            rests.add(raw.get(i));
        }

        //Make Workout from raw
        for(int i = raw.get(0); i < raw.size(); i += 4){
            ArrayList<Lap> v = new ArrayList<>();
            for( int j = 0; j < raw.get(i); j++){
                Lap l = new Lap();
                l.distance = raw.get(i+1);
                l.delay = raw.get(i+2);
                l.goal = raw.get(i+3);
                v.add(l);
            }
            w.add(v);
        }

        //Make JSON from raw
        String toDevice = "{"; //the JSON
        //FIXME: get swimmer name here
        toDevice = toDevice + "swimmer\": " + UserID +",";
        toDevice = toDevice + "\"set\": [";
        int setcount = 0;
        for(int i = raw.get(0); i < raw.size(); i +=4){
            toDevice = toDevice + "{";
            toDevice = toDevice + "\"reps\": " + "\"" + raw.get(i) + "\",";
            toDevice = toDevice + "\"distance\": " + "\"" + raw.get(i+1) + "\",";
            toDevice = toDevice + "\"time_to_complete\": " + "\"" + raw.get(i+2) + "\",";
            toDevice = toDevice + "\"target_time\": " + "\"" + raw.get(i+3) + "\",";

            //Innerloop for times
            toDevice = toDevice + "\"times\": " + "[{";
            for(int j = 0; j < raw.get(i); j++){
                toDevice = toDevice +"\"" + j + "\": \"\",";
            }
            toDevice = toDevice + "}]";
            toDevice = toDevice + "}";
            if (setcount < sets) {
                toDevice = toDevice + ",";
            }
            setcount++;
        }
        toDevice = toDevice + "], \"rest_time\": {";
        for(int i = 1; i < sets; i++){
            //sets-1 times of rests
            toDevice = toDevice + "\"" + i + "\": \"" + rests.get(i-1) +"\"";
        }
        toDevice = toDevice + "}}";

        try {
            preJSON = new File(directory.getAbsolutePath()+"/workout-pre.json");

            FileOutputStream oStream = new FileOutputStream(preJSON.getAbsolutePath());

            oStream.write(toDevice.getBytes());
            oStream.close();

            //HAVE THIS SHIT

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("SwimmingWearable", "parsing done.");


        //Send to device


        return w;
    }

    //Asynchronous class created to get IP Address of Raspberry Pi at runtime.
    public class ipFetchTask extends AsyncTask<URL, Integer, String> {
        protected String doInBackground(URL... url){
            String host = "192.0.0.1";
            try {
                // Create a URL for the desired page
                BufferedReader in = new BufferedReader(new InputStreamReader( new URL("https://swimfanatic.net:6443/uploads/bip.txt").openStream()));
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
                session.setPassword(password);
                session.setConfig("StrictHostKeyChecking", "no");
                session.setTimeout(10000);
                session.connect();

                ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
                sftp.connect();
                sftp.put(preJSON.getAbsolutePath(), "/home/pi/pi-stroker/workout-pre.json");
                sftp.disconnect();

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

}
