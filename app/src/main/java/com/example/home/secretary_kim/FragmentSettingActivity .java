package com.example.home.secretary_kim;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class FragmentSettingActivity extends AppCompatActivity  {

    //@Override
    // protected void onCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceStage) {
     protected void onCreate(Bundle savedInstanceStage) {
        super.onCreate(savedInstanceStage);
        setContentView(R.layout.activity_setting);
        //inflater.inflate(R.layout.fragment_list, container, false);


        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new FragmentSetting(), null).commit();

    }

}
