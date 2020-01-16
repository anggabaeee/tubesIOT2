package com.example.tubesiot2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {
    private float acelVal;
    private float acelLast;
    private float shake;

    private SensorManager sensorManager;
    private Sensor mySensor;

    private Vibrator getar;

    private int active = 0;

    private  int RESULT_SPEECH= 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        mySensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(sensorListener, mySensor, SensorManager.SENSOR_DELAY_NORMAL);

        acelVal = SensorManager.GRAVITY_EARTH;
        acelLast = SensorManager.GRAVITY_EARTH;
        shake = 0.00f;

        getar = (Vibrator) getSystemService(VIBRATOR_SERVICE);

    }
    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            acelLast = acelVal;
            acelVal = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = acelVal - acelLast;
            shake = shake * 0.9f + delta;

            if(shake < -9 && active == 0){
                myExecute();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private void myExecute(){
        showToastMessage("Masukkan Suara Anda");
        String ID_ModelBahasaIndonesia = "id";
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, ID_ModelBahasaIndonesia);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, ID_ModelBahasaIndonesia);
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, ID_ModelBahasaIndonesia);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, ID_ModelBahasaIndonesia);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Masukkan Suara");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        getar.vibrate(200);
        active = 1;

        try{
            startActivityForResult(intent, RESULT_SPEECH);
        }
        catch (ActivityNotFoundException a){}
    }

    private void showToastMessage(String message) {
        Toast.makeText(this, message, LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        active=0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        active=0;
    }
}
