package com.izgoy.txttr;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class UserManager {

    private SharedPreferences db;

    public UserManager(Context context) {
        db = context.getSharedPreferences(TxttrApplication.PREFS_USERS, Context.MODE_PRIVATE);
    }

    public Map<String, String> getAll() {
        return (Map<String, String>) db.getAll();
    }

    public void register(String address, String nick) {
        SharedPreferences.Editor editor = db.edit();
        editor.putString(address, nick);
        editor.commit();
    }

    public void unregister(String address) {
        SharedPreferences.Editor editor = db.edit();
        editor.remove(address);
        editor.commit();
    }

    public boolean isNickRegistered(String nick) {
        return getAll().containsValue(nick);
    }

    public boolean isAddressRegistered(String address) {
        return getAll().containsKey(address);
    }
}
