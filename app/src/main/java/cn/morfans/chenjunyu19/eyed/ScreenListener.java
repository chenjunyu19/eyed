package cn.morfans.chenjunyu19.eyed;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

class ScreenListener {
    private Context context;
    private SensorListener sensorListener;
    private BroadcastReceiver broadcastReceiver;

    ScreenListener(Context context, SensorListener sensorListener) {
        this.context = context;
        this.sensorListener = sensorListener;
    }

    private class BroadcastReceiver extends android.content.BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case Intent.ACTION_SCREEN_OFF:
                        sensorListener.unregister();
                        break;
                    case Intent.ACTION_USER_PRESENT:
                        sensorListener.register();
                        break;
                }
            }
        }
    }

    void register() {
        broadcastReceiver = new BroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    void unregister() {
        context.unregisterReceiver(broadcastReceiver);
    }
}
