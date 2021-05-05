package com.example.offline.backend.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class Storage {

    private static final String SETTINGS_NAME = "offline_settings";
    private SharedPreferences mSharedPref;
    private SharedPreferences.Editor mEditor;


    public Storage(Context context) {
        mSharedPref = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPref.edit();
    }

    /**
     * Get String value from SharedPreferences at 'key'. If key not found, return ""
     *
     * @param key SharedPreferences key
     * @return String value at 'key' or "" (empty String) if key not found
     */
    public String getString(Key key) {

        String defaultValue = "";
        return mSharedPref.getString(key.name(), defaultValue);
    }

    /**
     * Get Integer value from SharedPref at 'key'. If not found return 0 (default value)
     *
     * @param key to map the integer
     * @return integer value
     */
    public int getInt(Key key) {
        Integer defaultValue = 0;
        return mSharedPref.getInt(key.name(), defaultValue);
    }

    public long getLong(String key) {
        long defaultValue = 0;
        return mSharedPref.getLong(key, defaultValue);
    }

    public void putLong(String key, long value) {
        mEditor.putLong(key, value);
        mEditor.apply();
    }

    /**
     * Get parsed ArrayList of String from SharedPreferences at 'key'
     *
     * @param key SharedPreferences key
     * @return ArrayList of String
     */
    public ArrayList<String> getListString(Key key) {
        return new ArrayList<String>(Arrays.asList(TextUtils.split(mSharedPref.getString(key.name(), ""), "‚‗‚")));
    }

    /**
     * Get object from local storage
     *
     * @param key      to map the object
     * @param classOfT object type
     * @param <T>
     * @return
     */
    public <T> T getObject(Key key, Class<T> classOfT) {

        String json = getString(key);
        Object value = new Gson().fromJson(json, classOfT);

        if (value == null)
            return null;
        return (T) value;
    }

    /**
     * Get a list of objects from local storage
     *
     * @param key    to map the list of objects
     * @param mClass type of the object
     * @return
     */
    public ArrayList<Object> getListObject(Key key, Class<?> mClass) {
        Gson gson = new Gson();

        ArrayList<String> objStrings = getListString(key);
        ArrayList<Object> objects = new ArrayList<>();

        for (String jObjString : objStrings) {
            Object value = gson.fromJson(jObjString, mClass);
            objects.add(value);
        }
        return objects;
    }

    /**
     * Save String value into SharedPreferences with 'key' and save
     *
     * @param key   to map the string
     * @param value to be saved
     */
    public void saveString(Key key, String value) {
        mSharedPref.edit().putString(key.name(), value).apply();
    }

    /**
     * Save integer value into SharedPreferences with 'key' and save
     *
     * @param key   to map the int
     * @param value to be saved
     */
    public void saveInt(Key key, Integer value) {
        mSharedPref.edit().putInt(key.name(), value).apply();
    }

    /**
     * Put ArrayList of String into SharedPreferences with 'key' and save
     *
     * @param key        SharedPreferences key
     * @param stringList ArrayList of String to be added
     */
    public void putListString(Key key, ArrayList<String> stringList) {
        String[] myStringList = stringList.toArray(new String[stringList.size()]);
        mSharedPref.edit().putString(key.name(), TextUtils.join("‚‗‚", myStringList)).apply();
    }

    /**
     * Save object in local storage
     *
     * @param key to map the object
     * @param obj to be  saved
     */
    public void saveObject(Key key, Object obj) {
        Gson gson = new Gson();
        saveString(key, gson.toJson(obj));
    }

    /**
     * Save a list of objects in local storage
     *
     * @param key      to map the list
     * @param objArray to be saved
     */
    public void saveListObject(Key key, ArrayList<?> objArray) {
        Gson gson = new Gson();
        ArrayList<String> objStrings = new ArrayList<String>();
        for (Object obj : objArray) {
            objStrings.add(gson.toJson(obj));
        }
        putListString(key, objStrings);
    }

    /**
     * Save boolean value into SharedPreferences with 'key' and save
     *
     * @param key   to map the Boolean
     * @param value to be saved
     */
    public void saveBoolean(Key key, Boolean value) {
        mSharedPref.edit().putBoolean(key.name(), value).apply();
    }

    /**
     * Get Boolean value from SharedPref at 'key'. If not found return false (default value)
     *
     * @param key to map the Boolean
     * @return the Boolean value
     */
    public Boolean getBoolean(Key key) {
        return mSharedPref.getBoolean(key.name(), false);
    }

}
