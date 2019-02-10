package cn.morfans.chenjunyu19.eyed;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class MainActivity extends Activity {
    static Intent intent;
    static DevicePolicyManager devicePolicyManager;
    static ComponentName deviceAdminReceiver;
    static SharedPreferences sharedPref;

    public static class DeviceAdminReceiver extends android.app.admin.DeviceAdminReceiver {
    }

    public static class PreferenceFragment extends android.preference.PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case "gravity":
                case "light":
                    getActivity().stopService(intent);
                    getActivity().startService(intent);
                    break;
                case "device_admin":
                    if (sharedPreferences.getBoolean("device_admin", false)) {
                        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdminReceiver);
                        startActivity(intent);
                    } else {
                        devicePolicyManager.removeActiveAdmin(deviceAdminReceiver);
                    }
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
            sharedPref.edit().putBoolean("device_admin", devicePolicyManager.isAdminActive(deviceAdminReceiver)).apply();
            onCreate(new Bundle());
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        deviceAdminReceiver = new ComponentName(this, DeviceAdminReceiver.class);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenceFragment()).commit();
        intent = new Intent(this, eyedService.class);
        startService(intent);
    }
}
