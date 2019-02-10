package cn.morfans.chenjunyu19.eyed;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class eyedService extends Service {
    private SensorListener sensorListener;
    private ScreenListener screenListener;

    @Override
    public void onCreate() {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        sensorListener = new SensorListener(this);
        sensorListener.register();
        screenListener = new ScreenListener(this, sensorListener);
        screenListener.register();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        sensorListener.unregister();
        screenListener.unregister();
    }
}
