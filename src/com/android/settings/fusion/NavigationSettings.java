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
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.text.Spannable;
import android.util.Log;
import android.view.WindowManagerGlobal;
import android.widget.Toast;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.internal.util.fusion.FusionUtils;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class NavigationSettings extends SettingsPreferenceFragment
    implements OnPreferenceChangeListener {

    private static final String TAG = "NavigationSettings";

    private static final String CATEGORY_NAVBAR = "navigation_bar";
    private static final String CATEGORY_NAV_BAR_ENABLE = "navigation_bar_enable";
    private static final String KEY_BUTTON_BACKLIGHT = "button_backlight";
    private static final String KEY_NAVIGATION_BAR_LEFT = "navigation_bar_left";
    private static final String KEYS_OVERFLOW_BUTTON = "keys_overflow_button";
    private static final String NAVIGATION_BAR_TINT = "navigation_bar_tint";

    private boolean mCheckPreferences;

    private SwitchPreference mNavigationBarLeftPref;
    private ListPreference mOverflowButtonMode;
    private ColorPickerPreference mNavbarButtonTint;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.fusion_navigation_settings);
        PreferenceScreen prefSet = getPreferenceScreen();

        // Navigation bar left
        mNavigationBarLeftPref = (SwitchPreference) findPreference(KEY_NAVIGATION_BAR_LEFT);

        // Navigation bar button color
        mNavbarButtonTint = (ColorPickerPreference) findPreference(NAVIGATION_BAR_TINT);
        mNavbarButtonTint.setOnPreferenceChangeListener(this);
        int intColor = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.NAVIGATION_BAR_TINT, 0xffffffff);
        String hexColor = String.format("#%08x", (0xffffffff & intColor));
        mNavbarButtonTint.setSummary(hexColor);
        mNavbarButtonTint.setNewPreviewColor(intColor);

        // Menu Overflow Config.        
        mOverflowButtonMode = (ListPreference) findPreference(KEYS_OVERFLOW_BUTTON);
        mOverflowButtonMode.setOnPreferenceChangeListener(this);

        String overflowButtonMode = Integer.toString(Settings.System.getInt(getContentResolver(),
                Settings.System.UI_OVERFLOW_BUTTON, 2));
        mOverflowButtonMode.setValue(overflowButtonMode);
        mOverflowButtonMode.setSummary(mOverflowButtonMode.getEntry());

        final boolean hasRealNavigationBar = getResources()
                .getBoolean(com.android.internal.R.bool.config_showNavigationBar);
        if (hasRealNavigationBar) { // only disable on devices with REAL navigation bars
            final Preference pref = findPreference(CATEGORY_NAV_BAR_ENABLE);
            if (pref != null) {
                getPreferenceScreen().removePreference(pref);
            }
        // Attach final settings screen.
        reloadSettings();
        }

        try {
            boolean hasNavBar = WindowManagerGlobal.getWindowManagerService().hasNavigationBar();

            // Hide navigation bar category on devices without navigation bar
            if (!hasNavBar) {
                prefSet.removePreference(findPreference(CATEGORY_NAVBAR));
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error getting navigation bar status");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mOverflowButtonMode) {
            int val = Integer.parseInt((String) newValue);
            int index = mOverflowButtonMode.findIndexOfValue((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.UI_OVERFLOW_BUTTON, val);
            mOverflowButtonMode.setSummary(mOverflowButtonMode.getEntries()[index]);
            Toast.makeText(getActivity(), R.string.keys_overflow_toast, Toast.LENGTH_LONG).show();
            return true;
        } else if (preference == mNavbarButtonTint) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NAVIGATION_BAR_TINT, intHex);
            return true;
        }
        return false;
    }

    private PreferenceScreen reloadSettings() {
        mCheckPreferences = false;
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.fusion_navigation_settings);
        prefs = getPreferenceScreen();

        final ButtonBacklightBrightness backlight =
                (ButtonBacklightBrightness) findPreference(KEY_BUTTON_BACKLIGHT);
        if (!backlight.isButtonSupported() && !backlight.isKeyboardSupported()) {
            prefs.removePreference(backlight);
        }

        mCheckPreferences = true;
        return prefs;
    }
}
