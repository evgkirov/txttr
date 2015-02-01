package com.izgoy.txttr;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

public class UserManager {

    public static final String TAG = "UserManager";

    private SharedPreferences db;
    private SmsManager smsManager;

    public UserManager(Context context) {
        db = context.getSharedPreferences(TxttrApplication.PREFS_USERS, Context.MODE_PRIVATE);
        smsManager = SmsManager.getDefault();
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

    public String getNick(String address) {
        return db.getString(address, null);
    }

    public void sendTextMessage(String address, String body) {
        ArrayList<String> parts = smsManager.divideMessage(body);
        sendMultipartTextMessage(address, parts);
    }

    public void sendTextMessageToAll(String body, String exceptAddress) {
        ArrayList<String> parts = smsManager.divideMessage(body);
        for (Map.Entry<String, String> user : getAll().entrySet()) {
            if ((exceptAddress != null) && !exceptAddress.equals(user.getKey())) {
                sendMultipartTextMessage(user.getKey(), parts);
            }
        }
    }

    public void sendMultipartTextMessage(String address, ArrayList<String> parts) {
        smsManager.sendMultipartTextMessage(address, null, parts, null, null);
        String bodyRepr = parts.get(0);
        if (parts.size() > 1) {
            bodyRepr += "...";
        }
        Log.d(TAG, address + " <<< " + bodyRepr);
    }

}
