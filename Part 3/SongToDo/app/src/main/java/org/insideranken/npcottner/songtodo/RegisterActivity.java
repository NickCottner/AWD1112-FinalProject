package org.insideranken.npcottner.songtodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    EditText registerEmail, registerPassword;
    Button btnRegister;
    TextView tvRegisterQuestion;

    FirebaseAuth auth;
    ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvRegisterQuestion = findViewById(R.id.tvRegisterQuestion);
        getSupportActionBar().setTitle("Register Page");
        auth = FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);

        tvRegisterQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String theRegisterEmail = registerEmail.getText().toString().trim();
                String theRegisterPassword = registerPassword.getText().toString().trim();

                if(TextUtils.isEmpty(theRegisterEmail))
                {
                    registerEmail.setError("Email Required!");
                    registerPassword.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(theRegisterPassword))
                {
                    registerPassword.setError("Password Required!");
                    registerPassword.requestFocus();
                    return;
                }
                auth.createUserWithEmailAndPassword(theRegisterEmail,theRegisterPassword).
                        addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                {
                                    Intent intent = new Intent(RegisterActivity.this,
                                            LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    String error = task.getException().toString();
                                    Toast.makeText(RegisterActivity.this,
                                            "Registration Failed\n" + error,
                                            Toast.LENGTH_LONG).show();
                                }
                                loader.dismiss();
                            }
                        });
            }
        });
    }
}