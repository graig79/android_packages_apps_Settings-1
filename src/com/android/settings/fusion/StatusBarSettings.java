/*
 * Copyright (C) 2014 The Fusion Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.fusion;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

//import com.android.internal.util.fusion.DeviceUtils;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarSettings extends SettingsPreferenceFragment 
        implements OnPreferenceChangeListener {

    private static final String TAG = "StatusBarSettings";

    private static final String KEY_STATUS_BAR_CLOCK = "clock_style_pref";
    private static final String STATUS_BAR_CARRIER = "status_bar_carrier";
    private static final String STATUS_BAR_CARRIER_COLOR = "status_bar_carrier_color";
    private static final String KEY_NETWORK_TRAFFIC_STATUS = "network_traffic";
    private static final String KEY_STATUS_BAR_GREETING = "status_bar_greeting";

    static final int DEFAULT_STATUS_CARRIER_COLOR = 0xffffffff;

    private PreferenceScreen mClockStyle;
    SwitchPreference mStatusBarCarrier;
    ColorPickerPreference mCarrierColorPicker;
    private PreferenceScreen mNetworkTraffic;
    private SwitchPreference mStatusBarGreeting;

    private String mCustomGreetingText = "";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.fusion_statusbar_settings);

        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        int intColor;
        String hexColor;

        PackageManager pm = getPackageManager();
        Resources systemUiResources;
        try {
            systemUiResources = pm.getResourcesForApplication("com.android.systemui");
        } catch (Exception e) {
            Log.e(TAG, "can't access systemui resources",e);
            return;
        }

        /*if (DeviceUtils.isPhone(getActivity())) {
            PreferenceScreen notifSystemIcons =
                    (PreferenceScreen) findPreference("status_bar_notif_system_icons_settings");
            notifSystemIcons.setTitle(R.string.status_bar_notif_system_icons_settings_title_phone);
        }*/

        // MIUI-like carrier Label
        mStatusBarCarrier = (SwitchPreference) prefSet.findPreference(STATUS_BAR_CARRIER);
        mStatusBarCarrier.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_CARRIER, 0) == 1));
        mStatusBarCarrier.setOnPreferenceChangeListener(this);

        // MIUI-like carrier Label color
        mCarrierColorPicker = (ColorPickerPreference) findPreference(STATUS_BAR_CARRIER_COLOR);
        mCarrierColorPicker.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(getContentResolver(),
                    Settings.System.STATUS_BAR_CARRIER_COLOR, DEFAULT_STATUS_CARRIER_COLOR);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mCarrierColorPicker.setSummary(hexColor);
        mCarrierColorPicker.setNewPreviewColor(intColor);

        mStatusBarGreeting = (SwitchPreference) findPreference(KEY_STATUS_BAR_GREETING);
        mCustomGreetingText = Settings.System.getString(resolver, Settings.System.STATUS_BAR_GREETING);
        boolean greeting = mCustomGreetingText != null && !TextUtils.isEmpty(mCustomGreetingText);
        mStatusBarGreeting.setChecked(greeting);

        mClockStyle = (PreferenceScreen) prefSet.findPreference(KEY_STATUS_BAR_CLOCK);
        updateClockStyleDescription();

        mNetworkTraffic = (PreferenceScreen) prefSet.findPreference(KEY_NETWORK_TRAFFIC_STATUS);
        updateNetworkTrafficDescription();
    }


    @Override
    public void onResume() {
        super.onResume();
        updateClockStyleDescription();
        updateNetworkTrafficDescription();
    }

    private void updateNetworkTrafficDescription() {
        if (mNetworkTraffic == null) {
            return;
        }
        if (Settings.System.getInt(getContentResolver(),
                Settings.System.NETWORK_TRAFFIC_STATE, 0) != 0) {
            mNetworkTraffic.setSummary(getString(R.string.enabled));
        } else {
            mNetworkTraffic.setSummary(getString(R.string.disabled));
        }
    }

    private void updateClockStyleDescription() {
        if (mClockStyle == null) {
            return;
        }
        if (Settings.System.getInt(getContentResolver(),
               Settings.System.STATUS_BAR_CLOCK, 1) == 1) {
            mClockStyle.setSummary(getString(R.string.enabled));
        } else {
            mClockStyle.setSummary(getString(R.string.disabled));
         }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        final ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mStatusBarGreeting) {
           boolean enabled = mStatusBarGreeting.isChecked();
           if (enabled) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

                alert.setTitle(R.string.status_bar_greeting_title);
                alert.setMessage(R.string.status_bar_greeting_dialog);

                // Set an EditText view to get user input
                final EditText input = new EditText(getActivity());
                input.setText(mCustomGreetingText != null ? mCustomGreetingText : "Welcome to Fusion");
                alert.setView(input);
                alert.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = ((Spannable) input.getText()).toString();
                        Settings.System.putString(getActivity().getContentResolver(),
                                Settings.System.STATUS_BAR_GREETING, value);
                        updateCheckState(value);
                    }
                });
                alert.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            } else {
                Settings.System.putString(getActivity().getContentResolver(),
                                Settings.System.STATUS_BAR_GREETING, "");
            }
        }
        // If we didn't handle it, let preferences handle it.		
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void updateCheckState(String value) {
        if (value == null || TextUtils.isEmpty(value)) mStatusBarGreeting.setChecked(false);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mCarrierColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_CARRIER_COLOR, intHex);
            return true;
        } else if (preference == mStatusBarCarrier) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_CARRIER, value ? 1 : 0);
            return true;
        }
        return false;
    }

}
