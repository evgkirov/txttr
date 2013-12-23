package com.izgoy.txttr;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class TxttrApplication extends Application {

    public static final String PREFS_SETTINGS = "settings";
    public static final String PREFS_USERS = "users";

    public static final String PREF_ENABLED = "enabled";

    public static final int MIN_NICK_LENGTH = 2;
    public static final int MAX_NICK_LENGTH = 2;

    public static SharedPreferences getSettings(Context context) {
        return context.getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE);
    }

}
