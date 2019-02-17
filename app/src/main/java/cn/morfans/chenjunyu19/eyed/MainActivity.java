package cn.morfans.chenjunyu19.eyed;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceScreen;
import android.provider.Settings;

public class MainActivity extends Activity {
    static Intent intent;
    static DevicePolicyManager devicePolicyManager;
    static ComponentName deviceAdminReceiver;

    public static class DeviceAdminReceiver extends android.app.admin.DeviceAdminReceiver {
    }

    public static class PreferenceFragment extends android.preference.PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        PreferenceScreen preferenceScreen;
        SharedPreferences sharedPreferences;

        void setSummary(String key, String defValue) {
            preferenceScreen.findPreference(key).setSummary(sharedPreferences.getString(key, defValue));
        }

        void refreshSummaries() {
            setSummary("delay", "5000");
            setSummary("abs_gx_max", "9");
            setSummary("gz_min", "-3");
            setSummary("illuminance_min", "1");
        }

        boolean isIgnoringBatteryOptimizations() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PowerManager powerManager = (PowerManager) getActivity().getSystemService(POWER_SERVICE);
                if (powerManager != null) {
                    return powerManager.isIgnoringBatteryOptimizations(getActivity().getPackageName());
                } else {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            preferenceScreen = getPreferenceScreen();
            sharedPreferences = getPreferenceManager().getSharedPreferences();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case "delay":
                case "abs_gx_max":
                case "gz_min":
                case "illuminance_min":
                    refreshSummaries();
                case "gravity":
                case "light":
                    getActivity().stopService(intent);
                    getActivity().startService(intent);
                    break;
                case "ignore_battery_optimizations":
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (sharedPreferences.getBoolean(key, false)) {
                            startActivity(new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + getActivity().getPackageName())));
                        } else {
                            startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));
                        }
                    }
                    break;
                case "device_admin":
                    if (sharedPreferences.getBoolean(key, false)) {
                        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdminReceiver);
                        startActivity(intent);
                    } else {
                        devicePolicyManager.removeActiveAdmin(deviceAdminReceiver);
                    }
                    break;
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            sharedPreferences.edit()
                    .putBoolean("ignore_battery_optimizations", isIgnoringBatteryOptimizations())
                    .putBoolean("device_admin", devicePolicyManager.isAdminActive(deviceAdminReceiver))
                    .apply();
            onCreate(new Bundle());
            try {
                PackageManager packageManager = getActivity().getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
                preferenceScreen.findPreference("version").setSummary(packageInfo.versionName + " (" + packageInfo.versionCode + ")");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            refreshSummaries();
            preferenceScreen.findPreference("ignore_battery_optimizations").setEnabled(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        deviceAdminReceiver = new ComponentName(this, DeviceAdminReceiver.class);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenceFragment()).commit();
        intent = new Intent(this, eyedService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(new NotificationChannel("eyed", getString(R.string.app_name), NotificationManager.IMPORTANCE_MIN));
            }
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }
}
