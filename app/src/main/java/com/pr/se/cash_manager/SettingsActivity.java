package com.pr.se.cash_manager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.*;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

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
            public SharedPreferences prefs;

            @Override
            public boolean onPreferenceClick(Preference p) {

                deleteLists();
                this.prefs = getSharedPreferences("com.pr.se.cash_manager", MODE_PRIVATE);
                this.prefs.edit().putBoolean("firstFilter", true).apply();
                this.prefs.edit().putBoolean("firstrun", true).apply();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                System.exit(0);
                return true;
            }
        });
    }

    private void deleteLists() {
        RW.writeCategories(this, new ArrayList<Category>(), "categories");
        RW.writeExpenses(this, new ArrayList<Expense>(), "expenses");
        RW.writeFilter(this, new ArrayList<Filter>(), "filters");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.activity_settings_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.getNavigationIcon().setColorFilter(-1, PorterDuff.Mode.SRC_IN);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
