package com.oop.projectgroup10.studychatroom;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

//this class lets user change his settings and some preferences
public class MemberSettings extends PreferenceActivity {



    Context context = null;
    Activity act = this;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_settings);

        getFragmentManager().beginTransaction().replace(R.id.frameL, new MyPreferenceFragment()).commit();


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.setting_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            final String userid = String.valueOf(pref.getInt("userid", 0));
            final String fname = pref.getString("fname", "ERROR");
            final String lname = pref.getString("lname", "ERROR");
            final String email = pref.getString("emailaddress", "ERROR");
            final int notif = pref.getInt("notifsettings", 2);

            final EditTextPreference firstName = (EditTextPreference) findPreference("firstName");
            final EditTextPreference lastName = (EditTextPreference) findPreference("lastName");
            final EditTextPreference emailAddress = (EditTextPreference) findPreference("emailAddress");
            final SwitchPreference enableNotif = (SwitchPreference) findPreference("enableNotif");

            firstName.setText((fname));
            lastName.setText(lname);
            emailAddress.setText(email);
            enableNotif.setChecked(notif == 1);


            //Handling changes

            firstName.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SharedPreferences.Editor editor = pref.edit();

                    if (!firstName.getText().equals(newValue)) {
                        firstName.setText(newValue.toString());
                        new SendDataAsync(null, null).execute("updateFname", userid, newValue.toString());

                        editor.putString("fname", newValue.toString());
                        editor.apply();
                    }


                    return false;
                }

            });
            lastName.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SharedPreferences.Editor editor = pref.edit();

                    if (!lastName.getText().equals(newValue)) {
                        lastName.setText(newValue.toString());
                        new SendDataAsync(null, null).execute("updateLname", userid, newValue.toString());

                        editor.putString("lname", newValue.toString());
                        editor.apply();
                    }

                    return false;
                }

            });
            emailAddress.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SharedPreferences.Editor editor = pref.edit();

                    if (!emailAddress.getText().equals(newValue)) {
                        emailAddress.setText(newValue.toString());
                        new SendDataAsync(null, null).execute("updateEmail", userid, newValue.toString());

                        editor.putString("emailaddress", newValue.toString());
                        editor.apply();
                    }
                    return false;
                }

            });

            enableNotif.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    SharedPreferences.Editor editor = pref.edit();

                    if (enableNotif.isChecked()) {
                        new SendDataAsync(null, null).execute("updateNotif", userid, "1");
                        editor.putInt("notifsettings", 1);
                    } else {
                        new SendDataAsync(null, null).execute("updateNotif", userid, "0");
                        editor.putInt("notifsettings", 0);
                    }
                    editor.apply();
                    return false;
                }

            });

        }
    }
}
