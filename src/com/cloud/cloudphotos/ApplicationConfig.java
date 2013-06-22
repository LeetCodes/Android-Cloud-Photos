package com.cloud.cloudphotos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class ApplicationConfig {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    /**
     * Initialise
     * 
     * @param context
     */
    public ApplicationConfig(Context context) {
        prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    /**
     * Get a string, and return a default value if not present.
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public String getString(String key, String defaultValue) {
        return prefs.getString(key, defaultValue);
    }

    /**
     * Set a string value
     * 
     * @param key
     * @param value
     */
    public void setString(String key, String value) {
        edit();
        editor.putString(key, value);
        editor.commit();
        return;
    }

    /**
     * Unset a string value
     * 
     * @param key
     */
    public void unsetString(String key) {
        unset(key);
        return;
    }

    /**
     * Get an integer, and return a default value if not present.
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public int getInteger(String key, Integer defaultValue) {
        return prefs.getInt(key, defaultValue);
    }

    /**
     * Set an integer
     * 
     * @param key
     * @param value
     */
    public void setInteger(String key, Integer value) {
        edit();
        editor.putInt(key, value);
        editor.commit();
        return;
    }

    /**
     * Unset an integer value
     * 
     * @param key
     */
    public void unsetInteger(String key) {
        unset(key);
        return;
    }

    /**
     * Unset a value based on key name.
     * 
     * @param key
     */
    private void unset(String key) {
        edit();
        editor.remove(key);
        editor.commit();
        return;
    }

    /**
     * Initialise the edit of preferences.
     */
    @SuppressLint("CommitPrefEdits")
    private void edit() {
        if (editor == null) {
            editor = prefs.edit();
        }
    }
}