package com.aotuo.h3officeplat.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.aotuo.h3officeplat.BaseApplication;

import java.util.HashMap;
import java.util.Set;

public class SharedPreferencesHelper {
    private static SharedPreferencesHelper instance = null;

    static final String XML_APP_DATA = "H3_app_data";

    public static final String KEY_APP_FIRST_USE = "FirstUse";
    public static final String KEY_APP_SERVER_ADDRESS = "ServerAddress";
    public static final String KEY_APP_USE_LANGUAGE = "useLanguage";
    public static final String KEY_APP_USE_LANGUAGE_ZH = "ZH";
    public static final String KEY_APP_USE_LANGUAGE_EN = "EN";

    HashMap<String, SharedPreferences> sharedPreferencesHashMap;

    private SharedPreferencesHelper() {
        sharedPreferencesHashMap = new HashMap<>();
    }

    public static SharedPreferencesHelper getInstance() {
        if (instance == null) {
            synchronized (SharedPreferencesHelper.class) {
                if (instance == null) {
                    instance = new SharedPreferencesHelper();
                }
            }
        }
        return instance;
    }

    public SharedPreferences getSharedPreferences(String name) {
        SharedPreferences sharedPreferences = sharedPreferencesHashMap.get(name);
        if (sharedPreferences == null) {
            sharedPreferences = BaseApplication.getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
            sharedPreferencesHashMap.put(name, sharedPreferences);
        }
        return sharedPreferences;
    }

    @SuppressWarnings("unchecked")
    public <E> E getAppData(String name, E defaultValue) {
        return (E) getValue(getSharedPreferences(XML_APP_DATA), name, defaultValue);
    }

    public <E> void setAppData(String name, E value) {
        setValue(getSharedPreferences(XML_APP_DATA), name, value);
    }

    private Object getValue(SharedPreferences sharedPreferences, String name, Object defaultValue) {
        Object returnValue = defaultValue;
        if (defaultValue instanceof Integer) {
            returnValue = sharedPreferences.getInt(name, (Integer) defaultValue);
        } else if (defaultValue instanceof Long) {
            returnValue = sharedPreferences.getLong(name, (Long) defaultValue);
        } else if (defaultValue instanceof Boolean) {
            returnValue = sharedPreferences.getBoolean(name, (Boolean) defaultValue);
        } else if (defaultValue instanceof Float) {
            returnValue = sharedPreferences.getFloat(name, (Float) defaultValue);
        } else if (defaultValue instanceof String) {
            returnValue = sharedPreferences.getString(name, (String) defaultValue);
        } else if (defaultValue instanceof Set) {
            returnValue = sharedPreferences.getStringSet(name, (Set<String>) defaultValue);
        }
        return returnValue;
    }

    private void setValue(SharedPreferences sharedPreferences, String name, Object value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (value instanceof Integer) {
            editor.putInt(name, (Integer) value);
        } else if (value instanceof Long) {
            editor.putLong(name, (Long) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(name, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(name, (Float) value);
        } else if (value instanceof String) {
            editor.putString(name, (String) value);
        } else if (value instanceof Set) {
            editor.putStringSet(name, (Set<String>) value);
        }
        editor.commit();
    }

}
