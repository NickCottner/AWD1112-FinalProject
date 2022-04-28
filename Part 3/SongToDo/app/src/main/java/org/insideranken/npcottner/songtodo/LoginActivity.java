package org.insideranken.npcottner.songtodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    EditText loginEmail, loginPassword;
    Button btnLogin;
    TextView tvLoginQuestion;

    FirebaseAuth auth;
    ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvLoginQuestion = findViewById(R.id.tvLoginQuestion);

        getSupportActionBar().setTitle("Login Page");

        auth = FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);

        tvLoginQuestion.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailLogin = loginEmail.getText().toString().trim();
                String passwordLogin = loginPassword.getText().toString().trim();

                if(TextUtils.isEmpty(emailLogin))
                {
                    loginEmail.setError("Email Required!");
                    loginEmail.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(passwordLogin))
                {
                    loginPassword.setError("Password Required!");
                    loginPassword.requestFocus();
                    return;
                }
                loader.setMessage("Login In Progress");
                loader.setCanceledOnTouchOutside(false);
                loader.show();

                auth.signInWithEmailAndPassword(emailLogin, passwordLogin).
                        addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    Intent intent = new Intent(LoginActivity.this,
                                            SongListActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    String error = task.getException().toString();
                                    Toast.makeText(LoginActivity.this,
                                            "Login Failed\n" + error,
                                            Toast.LENGTH_LONG).show();
                                }
                                loader.dismiss();
                            }
                        });
            }
        });
    }
}