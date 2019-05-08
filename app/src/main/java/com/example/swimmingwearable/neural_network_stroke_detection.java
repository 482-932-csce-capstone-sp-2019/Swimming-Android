package com.example.swimmingwearable;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;

import static java.lang.Thread.interrupted;

/**
 * Created by orion_000 on 4/6/2017.
 */

public class neural_network_stroke_detection {
    tesnsorswimrecognition tensor_recognizer = null;
    private static final String TAG = "WRITEFILEACTIVITY";

    public neural_network_stroke_detection(Context context) throws IOException {
        tensor_recognizer = new tesnsorswimrecognition(context);
    }


    private double distance(Double p1_x,Double p1_y, Double p2_x,Double p2_y) {
        double dx = p2_x-p1_x;
        double dy =p2_y-p1_y;
        return Math.sqrt(((dx*dx) +(dy*dy)));
    }
    private double pathlength(ArrayList<Double> points_x,ArrayList<Double> points_y){
        double d = 0.0;
        for(int index = 1;index < points_x.size();index++){
            d += distance(points_x.get(index-1),points_y.get(index-1),points_x.get(index),points_y.get(index));
        }

        return d;
    }
    private double[] resample(ArrayList<Double> points_x,ArrayList<Double> points_y,int totalPoints){
        double I = pathlength(points_x,points_y) / (totalPoints - 1);
        double D = 0.0;
        ArrayList<Double> newpoints_sensor_value = new ArrayList<>();
        ArrayList<Double> newpoints_time_value = new ArrayList<>();
        newpoints_sensor_value.add(points_x.get(0));
        newpoints_time_value.add(points_y.get(0));
        int i = 1;
        while(i <= points_x.size()-1) {
            double d = distance(points_x.get(i - 1),points_y.get(i - 1), points_x.get(i),points_y.get(i));
            if ((D + d) >= I) {
                double qx = points_x.get(i - 1) + ((I - D) / d) * (points_x.get(i) - points_x.get(i - 1));
                double qy = points_y.get(i - 1) + ((I - D) / d) * (points_y.get(i) - points_y.get(i - 1));
                newpoints_sensor_value.add(qx);
                newpoints_time_value.add(qy);
                points_y.add(i, qy);
                points_x.add(i, qx);
                D = 0.0;
            }
            else
                D += d;
            i += 1;
        }
        if(newpoints_sensor_value.size() == totalPoints-1)
            newpoints_sensor_value.add(points_x.get(points_x.size()-1));
        double[] double_sensor_value = new double[newpoints_sensor_value.size()];
        for(i = 0;i<newpoints_sensor_value.size();i++){
            double_sensor_value[i] = newpoints_sensor_value.get(i);
        }
        return double_sensor_value;
    }
    private float[] output_values(final ArrayList<Double> x_group, final ArrayList<Double> y_group, final ArrayList<Double> z_group, final ArrayList<Long> time_group){
//        Double[] resampled_x_value = null;
//        Double[] resampled_y_value = null;
//        Double[] resampled_z_value = null;
        Long origional_time_stamp = time_group.get(0);
        final ArrayList<Double> double_time_group = new ArrayList<>();
        for(int i = 0;i < time_group.size();i++) {
            Long time_difference = time_group.get(i) - origional_time_stamp;
            double_time_group.add(time_difference.doubleValue());
        }
//        new Thread(new Runnable() {
//            public void run(){
//                resampled_x_value = resample(x_group,new ArrayList<Double>(double_time_group),192);
//            }
//        }).start();
        double[] resampled_x_value = resample(x_group,new ArrayList<Double>(double_time_group),192);
        double[] resampled_y_value = resample(y_group,new ArrayList<Double>(double_time_group),192);
        double[] resampled_z_value = resample(z_group,new ArrayList<Double>(double_time_group),192);

        return combine_data(resampled_x_value, resampled_y_value, resampled_z_value);
    }
    private float[] combine_data(double[] resampled_x_value, double[] resampled_y_value, double[] resampled_z_value){
        ArrayList<Float> combined_data = new ArrayList<>();
        for(int i = 0;i<192;i++){
            combined_data.add(Float.parseFloat(String.valueOf(resampled_x_value[i])));
            combined_data.add(Float.parseFloat(String.valueOf(resampled_y_value[i])));
            combined_data.add(Float.parseFloat(String.valueOf(resampled_z_value[i])));
        }
        float[] data_converted = new float[combined_data.size()];
        for(int i = 0;i<data_converted.length;i++){
            data_converted[i]=combined_data.get(i);
        }
        return data_converted;
    }
    public String detect_neural_network(ArrayList<Double> x_group, ArrayList<Double> y_group, ArrayList<Double> z_group,ArrayList<Long> time_group,String actual){
        long start_time = System.currentTimeMillis();
        String detection = tensor_recognizer.recognize_value(output_values(x_group,y_group,z_group,time_group));
        start_time = System.currentTimeMillis() - start_time;
        return detection+","+actual+","+String.valueOf(start_time)+","+String.valueOf(System.currentTimeMillis());
    }

}
