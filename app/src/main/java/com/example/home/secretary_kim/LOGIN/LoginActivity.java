package com.example.home.secretary_kim.LOGIN;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.home.secretary_kim.DBUserORSecretary;
import com.example.home.secretary_kim.DBnotification;
import com.example.home.secretary_kim.MyFirebaseMessagingService;
import com.example.home.secretary_kim.R;
import com.example.home.secretary_kim.VR.BluetoothActivity;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

public class LoginActivity extends Activity {

    SessionCallback callback;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);

        final RadioGroup rg = (RadioGroup)findViewById(R.id.radioGroup1);
        Button b = (Button)findViewById(R.id.button1);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = rg.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton)findViewById(id);
                System.out.println("결과 : " + rb.getText().toString());

                DBUserORSecretary SelectRole = new DBUserORSecretary(LoginActivity.this, "SelectRole.db", null, 1);
                SQLiteDatabase dbRole;

                dbRole = SelectRole.getWritableDatabase();
                SelectRole.onCreate(dbRole);
                ContentValues values = new ContentValues();

                //values.put("num", 1);
                values.put("role", rb.getText().toString());
                //dbRole.insert("SelectRole", null,values);
                dbRole.update("SelectRole", values, "1", null);
                //dbRole.delete("SelectRole", "1", null);
                String selectedRole = SelectRole.getResult();
                System.out.println("db 입력값 : " + selectedRole);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            redirectSignupActivity();  // 세션 연결성공 시 redirectSignupActivity() 호출
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logger.e(exception);
            }
            setContentView(R.layout.activity_login); // 세션 연결이 실패했을때
        }                                            // 로그인화면을 다시 불러옴
    }

    protected void redirectSignupActivity() {       //세션 연결 성공 시 SignupActivity로 넘김
        final Intent intent = new Intent(this, KakaoSignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }
}
