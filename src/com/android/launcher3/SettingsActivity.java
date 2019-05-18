/*
 * Copyright (C) 2015 The Android Open Source Project
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

package com.android.launcher3;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.preference.TwoStatePreference;
import android.preference.ListPreference;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.provider.Settings;
import android.provider.Settings.System;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.widget.Toast;  

import com.android.launcher3.Utilities;
import com.android.launcher3.SharedPrefrencesHelper;
import com.android.launcher3.util.LooperExecutor;

/**
 * Settings activity for Launcher. Currently implements the following setting: Allow rotation
 */
public class SettingsActivity extends Activity {
	public static Launcher mLauncher;
	private static final long WAIT_BEFORE_RESTART = 250;
				
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new LauncherSettingsFragment())
                .commit();
    }

    /**
     * This fragment shows the launcher preferences.
     */
    public static class LauncherSettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        private SystemDisplayRotationLockObserver mRotationLockObserver;

    	private static boolean mRestartNeeded;
	
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getPreferenceManager().setSharedPreferencesName(LauncherFiles.SHARED_PREFERENCES_KEY);
            addPreferencesFromResource(R.xml.launcher_preferences);
			
			// Setup allow rotation preference
            Preference rotationPref = findPreference(Utilities.ALLOW_ROTATION_PREFERENCE_KEY);
            if (getResources().getBoolean(R.bool.allow_rotation)) {
                // Launcher supports rotation by default. No need to show this setting.
                getPreferenceScreen().removePreference(rotationPref);
            } else {
                ContentResolver resolver = getActivity().getContentResolver();
                mRotationLockObserver = new SystemDisplayRotationLockObserver(rotationPref, resolver);

                // Register a content observer to listen for system setting changes while
                // this UI is active.
                resolver.registerContentObserver(
                        Settings.System.getUriFor(System.ACCELEROMETER_ROTATION),
                        false, mRotationLockObserver);

                // Initialize the UI once
                mRotationLockObserver.onChange(true);
                rotationPref.setDefaultValue(Utilities.getAllowRotationDefaultValue(getActivity()));
            }
			
			/**
			* Warning: Dont Remove this is unless you know how to remove app Predictions with out losing home overview setting button,
			* Removing this can cause a bug to appre & frankly I find this options rather annoying.
			* Disable PredictionsBug to avoid a huge bugs
			*/
			Preference predictionsPref = findPreference(Utilities.SHOW_PREDICTIONS_PREF);
			// predictionsPref.setOnPreferenceChangeListener(this);
			predictionsPref.setEnabled(false);
			getPreferenceScreen().removePreference(predictionsPref);
			
			HomeKeyWatcher mHomeKeyListener = new HomeKeyWatcher(getActivity());
            mHomeKeyListener.setOnHomePressedListener(() -> {
                if (mRestartNeeded) {
                    restart(getActivity());
                }
            });
            mHomeKeyListener.startWatch();
			
			// Custom Mods
			
			// Show Google Search Preference
			// Todo: Add Ability Hide Google Search like in the LineageOS Default Launcher(Trebuchet)
			Preference showQSBPref = (SwitchPreference) findPreference(Utilities.SHOW_QSB);
			showQSBPref.setEnabled(false);	
			getPreferenceScreen().removePreference(showQSBPref);
			
            // Show App Search Preference
			// Todo: Add Ability Hide App Search In App Drawer like in the LineageOS Default Launcher(Trebuchet)
			Preference appSearchPref = (SwitchPreference) findPreference(Utilities.SHOW_APP_SEARCH);
			appSearchPref.setEnabled(false);	
			getPreferenceScreen().removePreference(appSearchPref);
			
			// Pinch To Overview Preference
			// Todo: Add Ability to toggle pinch to overview
			Preference PinchToOverviewPref = (SwitchPreference) findPreference(Utilities.PINCH_TO_OVERVIEW);
			PinchToOverviewPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {	
				@Override
				public boolean onPreferenceChange(Preference preference, Object o) {
					mRestartNeeded = true;
					return true;
				}
			});
			
			// Light Status Bar  Preference
			// Todo: Add Ability to toggle between Light Statue Bar
			Preference LighrStatusBarPref = (SwitchPreference) findPreference(Utilities.LIGHT_STATUS_BAR);
			LighrStatusBarPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {	
				@Override
				public boolean onPreferenceChange(Preference preference, Object o) {
					mRestartNeeded = true;
					return true;
				}
			});
			
			//Pulldown Search
			// Todo: Add Ability to toggle Pull Down Search Like Apple's iOS
			Preference PulldownSearchPref = (SwitchPreference) findPreference(Utilities.PULLDOWN_SEARCH);
			PulldownSearchPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {	
				@Override
				public boolean onPreferenceChange(Preference preference, Object o) {
					mRestartNeeded = false;
					return true;
				}
			});
			
			// Show All App Icon Like LineageOS Default Launcher(Trebuchet) look
			Preference allAppsIconPref = (SwitchPreference) findPreference(Utilities.SHOW_ALL_APPS_ICON);
			allAppsIconPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {	
				@Override
				public boolean onPreferenceChange(Preference preference, Object o) {
					mRestartNeeded = false;
					return true;
				}
			});
			
        }
		
		@Override
		public void onResume() {
			super.onResume();
		}
		
		@Override
		public void onPause() {
			super.onPause();
		}

		@Override
		public boolean onPreferenceChange(Preference preference, Object o) {
			return false;
		}
		
		@Override
        public void onDestroy() {
            if (mRotationLockObserver != null) {
                getActivity().getContentResolver().unregisterContentObserver(mRotationLockObserver);
                mRotationLockObserver = null;
            }
            super.onDestroy();
        }
    }

    /**
     * Content observer which listens for system auto-rotate setting changes, and enables/disables
     * the launcher rotation setting accordingly.
     */
    private static class SystemDisplayRotationLockObserver extends ContentObserver {

        private final Preference mRotationPref;
        private final ContentResolver mResolver;

        public SystemDisplayRotationLockObserver(
                Preference rotationPref, ContentResolver resolver) {
            super(new Handler());
            mRotationPref = rotationPref;
            mResolver = resolver;
        }

        @Override
        public void onChange(boolean selfChange) {
            boolean enabled = Settings.System.getInt(mResolver,
                    Settings.System.ACCELEROMETER_ROTATION, 1) == 1;
            mRotationPref.setEnabled(enabled);
            mRotationPref.setSummary(enabled
                    ? R.string.allow_rotation_desc : R.string.allow_rotation_blocked_desc);
        }
    }
    public static void restart(final Context context) {
        //ProgressDialog.show(context, null, context.getString(R.string.state_loading), true, false);
        new LooperExecutor(LauncherModel.getWorkerLooper()).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(WAIT_BEFORE_RESTART);
                } catch (Exception e) {
                    Log.e("SettingsActivity", "Error waiting", e);
                }

                Intent intent = new Intent(Intent.ACTION_MAIN)
                        .addCategory(Intent.CATEGORY_HOME)
                        .setPackage(context.getPackageName())
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 50, pendingIntent);

                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
    }
}
