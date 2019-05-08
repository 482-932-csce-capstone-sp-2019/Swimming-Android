package com.example.swimmingwearable;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class SwimActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duringswim);

        //Start timer

        //Declare the timer
        Timer t = new Timer();
        //Set the schedule function and rate
        t.scheduleAtFixedRate(new TimerTask() {

            int seconds = 0;
            int minutes = 0;

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        TextView tv = (TextView) findViewById(R.id.timerText);
                        String time =  String.valueOf(minutes)+":"+String.valueOf(seconds);
                        tv.setText(time);
                        seconds += 1;

                        if(seconds == 60)
                        {
                            seconds=0;
                            minutes=minutes+1;
                            time = String.valueOf(minutes)+":"+String.valueOf(seconds);
                            tv.setText(time);

                        }



                    }

                });
            }

        }, 0, 1000);

    }

    public void finishWorkout(View v){
        //Stop timer
        //Getfile
        //executeSSHCommand();
        //move on
        Intent i = new Intent(this, WorkoutResultsActivity.class);
        startActivity(i);
    }

    public void executeSSHCommand(){
        String user = "pi";
        String password = "pi";

        //Get IP of pi
        SwimActivity.jsonFetchTask fetcher = new SwimActivity.jsonFetchTask();
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

    public class jsonFetchTask extends AsyncTask<URL, Integer, String> {
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
                //sftp.get(preJSON.getAbsolutePath(), "/home/pi/pi-stroker/workout-pre.json"); //Temporarily removed for this demo
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
