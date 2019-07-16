package com.example.myappvideo2;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Accelerometer {

    public interface Listener{
        void onTranslation(float tx, float ty, float tz);
    }
    public Listener listener;
    public void setListener(Listener l){
        listener=l;
    }

    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener sensorEventListener;

    private float[] valuesAccel = new float[3];
    private float[] valuesAccelMotion = new float[3];
    private float[] valuesAccelGravity = new float[3];

    Accelerometer(Context context){
        sensorManager=(SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorEventListener=new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if(listener!=null){
                    for (int i = 0; i < 3; i++) {
                        valuesAccel[i] = sensorEvent.values[i];
                        valuesAccelGravity[i] = (float) (0.1 * sensorEvent.values[i] + 0.9 * valuesAccelGravity[i]);
                        valuesAccelMotion[i] = sensorEvent.values[i] - valuesAccelGravity[i];
                    }
                    listener.onTranslation(valuesAccelMotion[0], valuesAccelMotion[1], valuesAccelMotion[2]);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    public void register(){
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregister(){
        sensorManager.unregisterListener(sensorEventListener);
    }
}
