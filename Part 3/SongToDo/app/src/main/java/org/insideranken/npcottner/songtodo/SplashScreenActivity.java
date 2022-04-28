package org.insideranken.npcottner.songtodo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class SplashScreenActivity extends AppCompatActivity {
    Button btnGoLogin, btnGoRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        btnGoLogin = findViewById(R.id.btnGoToLogin);
        btnGoRegister = findViewById(R.id.btnGoToRegister);

        btnGoLogin.setOnClickListener(view -> {
            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        btnGoRegister.setOnClickListener(view -> {
            Intent intent = new Intent(SplashScreenActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}