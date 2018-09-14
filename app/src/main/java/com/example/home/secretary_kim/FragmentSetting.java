package com.example.home.secretary_kim;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;


public class FragmentSetting extends PreferenceFragmentCompat {

    public FragmentSetting() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.fragment_setting);
    }
}