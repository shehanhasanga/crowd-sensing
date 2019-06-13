package mobile.crowdsensing;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.content.Context;

import android.support.annotation.Nullable;

import android.util.Log;

import java.util.Calendar;

/**
 * Created by arvi on 12/11/17.
 */

public class SensorService extends Service implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Sensor temp;
    private Sensor humidity;
    private Sensor light;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;


    private Context ctx;

    private int counter = 0;
    private static final String TAG = "kkkkkkkkkkkkkkkk";

    public SensorService() {

    }

    public SensorService(Context applicationContext) {
        super();
        ctx = applicationContext;
        Log.i(TAG, "SensorService class");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");

//        tc = new TimerCounter();
//        Intent notificationIntent = new Intent(this, MainActivity.class);

//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//                notificationIntent, 0);

//        Notification notification = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("My Awesome App")
//                .setContentText("Doing some work...")
//                .setContentIntent(pendingIntent).build();

//        startForeground(1337, notification);

        // Get sensor manager on starting the service.
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Registering...
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Get default sensor type
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        temp=mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        humidity=mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        light=mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "onStartCommand()");
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Registering...
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

        mSensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, temp, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, humidity, SensorManager.SENSOR_DELAY_NORMAL);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "serviceOnDestroy()");
        Intent broadcastIntent = new Intent("mobile.crowdsensing.RestartSensor");
        sendBroadcast(broadcastIntent);
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG, "serviceonTaskRemoved()");
        Intent broadcastIntent = new Intent("mobile.crowdsensing.RestartSensor");
        sendBroadcast(broadcastIntent);
        super.onTaskRemoved(rootIntent);

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.i(TAG, "onLowMemory()");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (light.getType() == Sensor.TYPE_LIGHT) {
            float x =  event.values[0];
            Log.i("kkkkkkkkkkkk", " Ambient light level in SI lux units " + x);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}