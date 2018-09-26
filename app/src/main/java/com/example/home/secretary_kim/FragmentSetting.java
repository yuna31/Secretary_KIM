package com.example.home.secretary_kim;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class FragmentSetting extends PreferenceFragmentCompat{

    SharedPreferences prefs;

    public FragmentSetting() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.fragment_setting);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(prefListener);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

//    @Override
//    public void onCreatePreferences(Bundle bundle, String s) {
//        //setPreferencesFromResource(R.xml.fragment_setting, s);
//        addPreferencesFromResource(R.xml.fragment_setting);
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
//        onSharedPreferenceChanged(sharedPref, "notifications_new_message");
//        //Boolean switchPref = sharedPref.getBoolean(FragmentSetting.KEY_PREF_EXAMPLE_SWITCH, false);
//    }

    SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            DBnotification NotiOnOff = new DBnotification(getContext(), "NotiOnOff.db", null, 1);
            SQLiteDatabase dbNoti;

            dbNoti = NotiOnOff.getWritableDatabase();
            NotiOnOff.onCreate(dbNoti);
            ContentValues values = new ContentValues();

            if (key.equals("notifications_new_message")) {
                if(prefs.getBoolean("notifications_new_message", true)) {
                    values.put("id", 1);
                    values.put("OnOff", "ON");
                    dbNoti.insert("NotiOnOff", null,values);
                    String notification = NotiOnOff.getResult();
                    System.out.println("noti On/Off : " + notification);
                }
                else {
                    values.put("id", 1);
                    values.put("OnOff", "OFF");
                    dbNoti.insert("NotiOnOff", null,values);
                    String notification = NotiOnOff.getResult();
                    System.out.println("noti On/Off : " + notification);
                }

            }

        }

    };


}
