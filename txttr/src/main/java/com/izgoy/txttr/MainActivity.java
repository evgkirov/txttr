package com.izgoy.txttr;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class MainActivity extends Activity implements View.OnClickListener {

    private CheckBox enableCheckBox;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = TxttrApplication.getSettings(this);

        enableCheckBox = (CheckBox) findViewById(R.id.enable);
        enableCheckBox.setOnClickListener(this);
        enableCheckBox.setChecked(settings.getBoolean(TxttrApplication.PREF_ENABLED, false));
    }

    private void onEnableClick(View view) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(TxttrApplication.PREF_ENABLED, enableCheckBox.isChecked());
        editor.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.enable:
                onEnableClick(view);
                return;
        }
    }
}
