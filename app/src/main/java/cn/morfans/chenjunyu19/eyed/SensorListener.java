package cn.morfans.chenjunyu19.eyed;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.Toast;

public class SensorListener implements SensorEventListener {
    private long lastToast;
    private Context context;

    SensorListener(Context context) {
        this.context = context;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_GRAVITY:
                if (event.values[2] <= -3 && System.currentTimeMillis() - lastToast >= 2500) {
                    Toast.makeText(context, "你正在以不健康的姿势使用手机！", Toast.LENGTH_SHORT).show();
                    lastToast = System.currentTimeMillis();
                }
                break;
            case Sensor.TYPE_LIGHT:
                if (event.values[0] == 0 && System.currentTimeMillis() - lastToast >= 2500) {
                    Toast.makeText(context, "你正在黑暗的环境中使用手机！", Toast.LENGTH_SHORT).show();
                    lastToast = System.currentTimeMillis();
                }
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
