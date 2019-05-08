package com.example.swimmingwearable;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.widget.Switch;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;
//import org.tensorflow.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Created by orion on 3/5/2018.
 */

public class tesnsorswimrecognition {
    TensorFlowInferenceInterface tensorflow;

    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "output";
    private static final int INPUT_SIZE = 24;
    private float[] outputs;
    private String[] OUTPUTNAMES;
    private static final float THRESHOLD = 0.1f;//

    public tesnsorswimrecognition(Context context){
        tensorflow = new TensorFlowInferenceInterface(context.getAssets(),"file:///android_asset/mnistmodelgraph.pb");
        int numClasses = (int) tensorflow.graph().operation(OUTPUT_NAME).output(0).shape().size(1);
        OUTPUTNAMES = new String[]{OUTPUT_NAME};
        outputs = new float[numClasses];
//        try {
//            write_raw(context);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public String recognize_value(final float[] datasets) {
        String recognized_value = "";
        // Copy the input data into TensorFlow.
        tensorflow.feed(INPUT_NAME, datasets, new long[]{INPUT_SIZE * INPUT_SIZE});

        // Run the inference call.
        tensorflow.run(OUTPUTNAMES);

        // Copy the output Tensor back into the output array.
        tensorflow.fetch(OUTPUT_NAME, outputs);

        int location = 0;
        String value = "";
        // Find the best classifications.
        for (int i = 0; i < outputs.length; ++i) {
            if (outputs[i] > THRESHOLD) {
                switch(location){
                    case 0:
                        value = "BREASTSTROKE";
                        break;
                    case 1:
                        value = "BACKSTROKE";
                        break;
                    case 2:
                        value = "FREESTYLE";
                        break;
                    case 3:
                        value = "BUTTERFLY";
                        break;
                    case 4:
                        value = "SIDESTROKE";
                        break;
                    case 5:
                        value = "TREADINGWATER";
                        break;
                    case 6:
                        value = "FLIPTURN";
                        break;
                }
                break;
            }
            location+=1;
        }
        for (int i = 0; i < outputs.length; ++i) {
            value += "," + String.valueOf(outputs[i]);
        }
        return value;
    }


    //
//public void write_raw(Context context) throws IOException {
//InputStream inputStream = context.getResources().openRawResource(R.raw.time2250ms2500msaveragetest);
//BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//String csvLine;
//File path_dir = Environment.getExternalStorageDirectory();
//File reults_csv = new File(path_dir,"sampledata");
//if(!reults_csv.mkdirs())
//System.out.println("made directory");
//final File results_csv = new File(reults_csv,"data.txt");
//if(!reults_csv.exists()){
//reults_csv.createNewFile();
//}
//OutputStream os = new FileOutputStream(results_csv,true);
//while ((csvLine = reader.readLine()) != null){
//String[] row = csvLine.split(",");
//float[] data_set_input = new float[row.length];
//for(int x = 0;x<row.length;x++){
//data_set_input[x] = Float.valueOf(row[x]);
//}
//
//PrintStream ps = new PrintStream(os);
//ps.println(recognize_value(data_set_input));
//}
//os.close();
//}
}
