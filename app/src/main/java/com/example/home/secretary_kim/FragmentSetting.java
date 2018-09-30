package com.example.home.secretary_kim;

import android.app.Activity;
import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentSetting extends PreferenceFragmentCompat {

    SharedPreferences prefs;
    private Context context;

    public FragmentSetting() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.fragment_setting);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(prefListener);
        //getListView().setBackgroundColor(Color.WHITE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().setBackgroundColor(Color.WHITE);
        getView().setClickable(true);
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

            DBnotificationEmergency EmerNotiOnOff = new DBnotificationEmergency(getContext(), "EmerNotiOnOff.db", null, 1);
            SQLiteDatabase dbNotiEmer;

            dbNotiEmer = EmerNotiOnOff.getWritableDatabase();
            EmerNotiOnOff.onCreate(dbNotiEmer);
            ContentValues values1 = new ContentValues();

            if (key.equals("notifications_new_message")) {
                if(prefs.getBoolean("notifications_new_message", true)) {

                    values.put("id", 1);
                    values.put("OnOff", "ON");
                    dbNoti.insert("NotiOnOff", null,values);

                    values1.put("id", 1);
                    values1.put("OnOff", "ON");
                    dbNotiEmer.insert("EmerNotiOnOff", null,values1);

                    String notification = NotiOnOff.getResult();
                    System.out.println("noti On/Off : " + notification);
                }
                else {
                    values.put("id", 1);
                    values.put("OnOff", "OFF");
                    dbNoti.insert("NotiOnOff", null,values);

                    values1.put("id", 1);
                    values1.put("OnOff", "OFF");
                    dbNotiEmer.insert("EmerNotiOnOff", null,values1);

                    String notification = NotiOnOff.getResult();
                    System.out.println("noti On/Off : " + notification);
                }
            }

            if (key.equals("notifications_new_message_move")) {
                if (prefs.getBoolean("notifications_new_message_move", true)) {
                    values.put("id", 1);
                    values.put("OnOff", "ON");
                    dbNoti.insert("NotiOnOff", null, values);
                    String notification = NotiOnOff.getResult();
                    System.out.println("noti On/Off : " + notification);
                } else {
                    values.put("id", 1);
                    values.put("OnOff", "OFF");
                    dbNoti.insert("NotiOnOff", null, values);
                    String notification = NotiOnOff.getResult();
                    System.out.println("noti On/Off : " + notification);
                }
            }

            if (key.equals("notifications_new_message_emergency")) {
                if (prefs.getBoolean("notifications_new_message_emergency", true)) {
                    values1.put("id", 1);
                    values1.put("OnOff", "ON");
                    dbNotiEmer.insert("EmerNotiOnOff", null, values1);

                    String notification = EmerNotiOnOff.getResult();
                    System.out.println("emergency noti On/Off : " + notification);
                } else {
                    values1.put("id", 1);
                    values1.put("OnOff", "OFF");
                    dbNotiEmer.insert("EmerNotiOnOff", null, values1);

                    String notification = EmerNotiOnOff.getResult();
                    System.out.println("emergency noti On/Off : " + notification);
                }
            }

        }

    };

}
