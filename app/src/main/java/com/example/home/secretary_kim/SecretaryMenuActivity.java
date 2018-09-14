package com.example.home.secretary_kim;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SecretaryMenuActivity extends Activity {

    public Button connect_btn;
    public Button menu_btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secretarymenu);

        connect_btn = (Button) findViewById(R.id.connect_button);
        connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MakeConnActivity.class);
                startActivityForResult(i, 0);
            }
        });

        menu_btn = (Button) findViewById(R.id.menu_button);
        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(i, 0);
            }
        });

    }
}
