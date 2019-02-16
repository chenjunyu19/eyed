package cn.morfans.chenjunyu19.eyed;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.widget.Toast;

class SensorListener {
    private long lastToast;
    private Context context;
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;
    private DevicePolicyManager devicePolicyManager;

    SensorListener(Context context) {
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    private class SensorEventListener implements android.hardware.SensorEventListener {
        final int delay;
        final int illuminanceMin;

        SensorEventListener(int delay, int illuminanceMin) {
            this.delay = delay;
            this.illuminanceMin = illuminanceMin;
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (System.currentTimeMillis() - lastToast >= delay) {
                String toastText = "";
                switch (event.sensor.getType()) {
                    case Sensor.TYPE_GRAVITY:
                        if (event.values[2] <= -3) {
                            toastText = context.getString(R.string.toast_gravity);
                        }
                        break;
                    case Sensor.TYPE_LIGHT:
                        if (event.values[0] == 0) {
                            toastText = context.getString(R.string.toast_light);
                        }
                        break;
                }
                if (!toastText.equals("")) {
                    Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
                    lastToast = System.currentTimeMillis();
                    if (devicePolicyManager.isAdminActive(new ComponentName(context, MainActivity.DeviceAdminReceiver.class))) {
                        devicePolicyManager.lockNow();
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    void register() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sensorEventListener = new SensorEventListener(Integer.parseInt(sharedPreferences.getString("delay", "5000")), Integer.parseInt(sharedPreferences.getString("illuminance_min", "1")));
        if (sharedPreferences.getBoolean("gravity", false)) {
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (sharedPreferences.getBoolean("light", false)) {
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    void unregister() {
        sensorManager.unregisterListener(sensorEventListener);
    }
}
