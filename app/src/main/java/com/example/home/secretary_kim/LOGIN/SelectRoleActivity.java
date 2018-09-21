package com.example.home.secretary_kim.LOGIN;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.home.secretary_kim.DBUserORSecretary;
import com.example.home.secretary_kim.R;
import com.example.home.secretary_kim.SecretaryMenuActivity;
import com.example.home.secretary_kim.VR.BluetoothActivity;

public class SelectRoleActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectrole);

        final RadioGroup rg = (RadioGroup)findViewById(R.id.radioGroup1);
        Button b = (Button)findViewById(R.id.button1);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = rg.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton)findViewById(id);
                System.out.println("결과 : " + rb.getText().toString());

                DBUserORSecretary SelectRole = new DBUserORSecretary(SelectRoleActivity.this, "SelectRole.db", null, 1);
                SQLiteDatabase dbRole;

                dbRole = SelectRole.getWritableDatabase();
                SelectRole.onCreate(dbRole);
                ContentValues values = new ContentValues();

                values.put("num", 1);
                values.put("role", rb.getText().toString());
                dbRole.insert("SelectRole", null,values);
                //dbRole.update("SelectRole", values, "1", null);
                //dbRole.delete("SelectRole", "1", null);
                String selectedRole = SelectRole.getResult();
                Toast.makeText(getApplicationContext(), selectedRole, Toast.LENGTH_SHORT).show();
                System.out.println("db 입력값 : " + selectedRole);

                if(selectedRole.equals("사용자")) {
                    Intent i= new Intent(SelectRoleActivity.this, BluetoothActivity.class);
                    startActivityForResult(i, 0);
                } else if(selectedRole.equals("관리자")) {
                    Intent i= new Intent(SelectRoleActivity.this, SecretaryMenuActivity.class);
                    startActivityForResult(i, 0);
                }

            }
        });
    }

}
