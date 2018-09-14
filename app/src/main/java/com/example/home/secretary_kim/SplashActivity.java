package com.example.home.secretary_kim;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.home.secretary_kim.LOGIN.LoginActivity;
import com.example.home.secretary_kim.LOGIN.SelectRoleActivity;
import com.example.home.secretary_kim.VR.BluetoothActivity;
import com.google.common.logging.nano.Vr;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.helper.log.Logger;

import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.NULL;

public class SplashActivity extends Activity {
    private Handler handler;
    String LoginName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                requestMe();
            }
        },2000);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                DBUserORSecretary SelectRole = new DBUserORSecretary(SplashActivity.this, "SelectRole.db", null, 1);
                SQLiteDatabase dbRole;

                dbRole = SelectRole.getReadableDatabase();
                SelectRole.onCreate(dbRole);
                String selectedRole = SelectRole.getResult();
                System.out.println("db 입력값 : " + selectedRole);
                System.out.println("if 결과 : " + (LoginName.length() == 0) + " " + selectedRole.equals("사용자"));

                if((LoginName.length() == 0) && selectedRole.equals("사용자")) {
                    Intent i= new Intent(SplashActivity.this, BluetoothActivity.class);
                    startActivityForResult(i, 0);
                } else if((LoginName.length() == 0) && selectedRole.equals("관리자")) {
                    Intent intent =new Intent(SplashActivity.this, com.example.home.secretary_kim.SecretaryMenuActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Intent i= new Intent(SplashActivity.this, LoginActivity.class);
                    startActivityForResult(i, 0);
                }
            }
        },2800);

    }


    private void requestMe() {
        List<String> keys = new ArrayList<>();
        keys.add("properties.nickname");

        UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(i, 0);
                Toast.makeText(getApplicationContext(), "다시 로그인해주세요", Toast.LENGTH_LONG).show();
                System.out.println("****** " + errorResult.getErrorMessage());
            }

            @Override
            public void onSuccess(MeV2Response response) {

                System.out.println("@@@@@로그인 한 사람 : " + response.getNickname());
            }
        });
    }
}
