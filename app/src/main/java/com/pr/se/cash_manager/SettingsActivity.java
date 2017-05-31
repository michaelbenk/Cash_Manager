package com.pr.se.cash_manager;

/**
 * Created by ivanlazic on 24.04.17.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.*;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import java.io.File;

public class SettingsActivity extends PreferenceActivity {

    private SwitchPreference s;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.activity_settings);
        final Preference changePasswordPref = findPreference("pref_password_change");
        final Preference factoryResetPref = findPreference("pref_reset");

        s = (SwitchPreference) findPreference("pref_password_login");

        changePasswordPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference p) {
                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(intent);
                return true;
            }
        });

        factoryResetPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference p) {

                File dir = getFilesDir();

                deleteRecursive(dir.getPath());

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                System.exit(0);
                return true;
            }
        });
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("pref_password_login")) {
            boolean test = sharedPreferences.getBoolean("pref_password_login", false);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.activity_settings_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void deleteRecursive(String strPath) {

        File fileOrDirectory = new File(strPath);

        if (fileOrDirectory.isDirectory()){
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child.getPath());
            fileOrDirectory.delete();
        }else{

            fileOrDirectory.delete();
        }
    }
}
