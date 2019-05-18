/**
* ------------------------------------------------------------
* Utilities
* Copyright LightningFastRom
* ------------------------------------------------------------
*/
package com.android.launcher3;

import android.content.SharedPreferences;
import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.Utilities;

public final class SharedPrefrencesHelper {	
	
	
	/**
	* ------------------------------------------------------------
	* Shared Preferences Put & Get Type for my two cystom function
	* ------------------------------------------------------------
	* Update Preferences Functions
	* ------------------------------------------------------------
	* updateBoolean(Context context, String key, boolean value)
	* updateInt(Context context, String key, int value)
	* updateFloat(Context context, String key, float value)
	* updateLong(Context context, String key, long value)
	* updateString(Context context, String key, string value)
	* ------------------------------------------------------------
	* Read Preferences Functions
	* ------------------------------------------------------------
	* updateBoolean(Context context, String key, boolean value)
	* readInt(Context context, String key, int value)
	* readFloat(Context context, String key, float value)
	* readLong(Context context, String key, long value)
	* readString(Context context, String key, string value)
	* -------------------------------------------------------------
	*/
	
	// Shared Preferences vaveable
	//private SharedPreferences sharedPreferences;
	//private static final String PREF_NAME = "com.android.launcher3.device.prefs";
	
	// Updating Shared Preference value
	// Updating Boolean
	public static void updateBoolean(Context context, String key, boolean value) {
		// Shared Preference Boolean
		SharedPreferences.Editor editor = getPrefs(context).edit();
		editor.putBoolean(key, value);
		editor.apply();
	}
	// Updating Int
	public static void updateInt(Context context, String key, int value) {
		// Shared Preference Int
		SharedPreferences.Editor editor = getPrefs(context).edit();
		editor.putInt(key, value);
		editor.apply();
	}
	// Updating Float
	public static void updateFloat(Context context, String key, float value) {
		// Shared Preference Float
		SharedPreferences.Editor editor = getPrefs(context).edit();
		editor.putFloat(key, value);
		editor.apply();
	}
	// Updating Long
	public static void updateLong(Context context, String key, long value) {
		// Shared Preference Long
		SharedPreferences.Editor editor = getPrefs(context).edit();
		editor.putLong(key, value);	
		editor.apply();		
	}
	// Updating String
	public static void updateString(Context context, String key, String value) {
		// Shared Preference String 
		SharedPreferences.Editor editor = getPrefs(context).edit();
		editor.putString(key, value);
		editor.apply();
	}
	
	// Reading Shared Preference value
	// Read Boolean
	public static boolean readBoolean(Context context, String key, boolean value) {
		// Shared Preference Boolean
		return getPrefs(context).getBoolean(key, value);
	}
	// Read Int
	public static int readInt(Context context, String key, int value) {
		// Shared Preference Int
		return getPrefs(context).getInt(key, value);
	}
	// Read Float
	public static float readFloat(Context context, String key, float value) {
		// Shared Preference Float
		return getPrefs(context).getFloat(key, value);
	}
	// Read Long
	public static long readLong(Context context, String key, long value) {
		// Shared Preference Long
		return getPrefs(context).getLong(key, value);			
	}
	// Read String
	public static String readString(Context context, String key, String value) {
		// Shared Preference String 
		return getPrefs(context).getString(key, value);
	}
	
	public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(
                LauncherFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    public static SharedPreferences getDevicePrefs(Context context) {
        return context.getSharedPreferences(
                LauncherFiles.DEVICE_PREFERENCES_KEY, Context.MODE_PRIVATE);
    }
}