package com.binarybricks.coinbit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.binarybricks.coinbit.features.HomeActivity;
import com.binarybricks.coinbit.features.launch.LaunchActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ktx.Firebase;

import java.util.Objects;

import timber.log.Timber;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        TextView signupToLogin = findViewById(R.id.signup_to_login_tv);

        EditText email = findViewById(R.id.email_signup_et);
        EditText password = findViewById(R.id.password_signup_et);
        EditText confirmPassword = findViewById(R.id.confirm_signup_et);

        Button signupButton = findViewById(R.id.signup_btn);

        ProgressBar signupProgressBar = findViewById(R.id.signup_progressbar);

        View parentLayout = findViewById(android.R.id.content);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String errMsg = AuthenticationValidator.validateEmail(email.getText().toString());

                if (!errMsg.isEmpty()) {
                    Snackbar.make(parentLayout, errMsg, Snackbar.LENGTH_LONG).show();
                    return;
                }

                errMsg = AuthenticationValidator.validatePassword(password.getText().toString(), 5);

                if (!errMsg.isEmpty()) {
                    Snackbar.make(parentLayout, errMsg, Snackbar.LENGTH_LONG).show();
                    return;
                }

                errMsg = AuthenticationValidator.comparePasswords(password.getText().toString(), confirmPassword.getText().toString());

                if (!errMsg.isEmpty()) {
                    Snackbar.make(parentLayout, errMsg, Snackbar.LENGTH_LONG).show();
                    return;
                }

                signupButton.setVisibility(View.GONE);
                signupProgressBar.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Timber.d("createUserWithEmail:success");

                                    Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                                    startActivity(homeIntent);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Timber.d(task.getException());
                                    Snackbar.make(parentLayout, task.getException().getMessage(),
                                            Snackbar.LENGTH_SHORT).show();
                                }

                                signupButton.setVisibility(View.VISIBLE);
                                signupProgressBar.setVisibility(View.GONE);
                            }
                        });

            }
        });

        signupToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

                startActivity(intent);
            }
        });
    }
}