package com.example.home.secretary_kim;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.home.secretary_kim.LOGIN.LoginActivity;
import com.example.home.secretary_kim.VR.BluetoothActivity;

public class MainActivity_NOLAYOUT extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nolayout);

        getSupportActionBar().setElevation(200);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);

        Button b = (Button) findViewById(R.id.loginbutton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        Button bluetooth_btn = (Button)findViewById(R.id.bluetooth_btn);
        bluetooth_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(), BluetoothActivity.class);
                startActivity(intent);
            }
        });
    }
}




