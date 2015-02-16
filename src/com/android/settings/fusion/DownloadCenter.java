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

import android.app.ActivityManager;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.SeekBarPreference;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class DownloadCenter extends SettingsPreferenceFragment 
    implements OnPreferenceChangeListener {

    Preference mFusionOfficial;
    Preference mFusionNightly;
    Preference mFusionGapps;
    Preference mPAGapps;
    Preference mFusionKernel;
    Preference mFusionRecovery;
    Preference mSupport;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.fusion_download_center);

        final ContentResolver resolver = getActivity().getContentResolver();

        mFusionOfficial = findPreference("fusion_official");
        mFusionNightly = findPreference("fusion_nightly"); 
        mFusionGapps = findPreference("fusion_gapps");       
        mPAGapps = findPreference("pa_gapps");
        mFusionKernel = findPreference("fusion_kernel");
        mFusionRecovery = findPreference("fusion_recovery");
        mSupport = findPreference("support_fusion");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mFusionOfficial) {
            Uri uri = Uri.parse("http://fs1.androidfilesharing.com/62~f#.VKCQLeAEAIA");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return true;
        } else if (preference == mFusionNightly) {
            Uri uri = Uri.parse("http://fs1.androidfilesharing.com/63~f#.VKCRleAEAIA");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return true;
        } else if (preference == mFusionGapps) {
            Uri uri = Uri.parse("http://fs1.androidfilesharing.com/64~f#.VKCRv-AEAIA");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return true;
        } else if (preference == mPAGapps) {
            Uri uri = Uri.parse("https://s.basketbuild.com/devs/TKruzze/Android%205.0.1%20GApps");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return true;
        } else if (preference == mFusionKernel) {
            Uri uri = Uri.parse("http://fs1.androidfilesharing.com/65~f#.VKCR6-AEAIA");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return true;
        } else if (preference == mFusionRecovery) {
            Uri uri = Uri.parse("http://fs1.androidfilesharing.com/66~f#.VKCSD-AEAIA");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return true;
        } else if (preference == mSupport) {
            Uri uri = Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=CDAAXFJCLA43A");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public boolean onPreferenceChange(Preference preference, Object value) {
         return true;
    }

    public static class DeviceAdminLockscreenReceiver extends DeviceAdminReceiver {}

}
