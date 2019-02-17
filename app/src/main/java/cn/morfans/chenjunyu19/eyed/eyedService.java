package cn.morfans.chenjunyu19.eyed;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public class eyedService extends Service {
    private final String STOP_SERVICE = "cn.morfans.chenjunyu19.eyed.STOP_SERVICE";
    private SensorListener sensorListener;
    private ScreenListener screenListener;

    @Override
    public void onCreate() {
        Notification.Builder builder = new Notification.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("eyed");
        }
        builder.setSmallIcon(R.drawable.ic_visibility)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text))
                .setPriority(Notification.PRIORITY_MIN)
                .setContentIntent(PendingIntent.getService(this, 0,
                        new Intent(this, eyedService.class).setAction(STOP_SERVICE),
                        PendingIntent.FLAG_CANCEL_CURRENT));
        startForeground(233, builder.build());
        sensorListener = new SensorListener(this);
        sensorListener.register();
        screenListener = new ScreenListener(this, sensorListener);
        screenListener.register();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null && intent.getAction().equals(STOP_SERVICE)) {
            onDestroy();
            System.exit(0);
        }
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
