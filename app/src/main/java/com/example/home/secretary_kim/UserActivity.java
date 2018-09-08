package com.example.home.secretary_kim;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.home.secretary_kim.VR.BluetoothActivity;

/*
 * 로그인한 뒤에 나오는 화면
 * 재접속시 바로 이 액티비티가 떠야됨
 */
public class UserActivity extends Activity {
    private Button bluetooth_act_btn;
    private Button settings_btn;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        bluetooth_act_btn = (Button) findViewById(R.id.bluetooth_act_btn);
        bluetooth_act_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        UserActivity.this.getApplicationContext(), BluetoothActivity.class);
                startActivity(intent);
            }
        });

//        settings_btn = (Button) findViewById(R.id.settings_btn);
//        settings_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }
}
