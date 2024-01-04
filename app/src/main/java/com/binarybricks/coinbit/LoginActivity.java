package com.binarybricks.coinbit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.binarybricks.coinbit.features.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import timber.log.Timber;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();

        EditText email = findViewById(R.id.email_login_et);
        EditText password = findViewById(R.id.password_login_et);
        TextView loginToSignin = findViewById(R.id.login_to_signin_tv);

        Button signinButton = findViewById(R.id.signin_btn);

        ProgressBar loginProgressBar = findViewById(R.id.login_progressbar);

        View parentLayout = findViewById(android.R.id.content);

        signinButton.setOnClickListener(new View.OnClickListener() {
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

                signinButton.setVisibility(View.GONE);
                loginProgressBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Timber.d("loginUserWithEmail:success");

                                    Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                                    startActivity(homeIntent);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Timber.d(task.getException());
                                    Snackbar.make(parentLayout, task.getException().getMessage(),
                                            Snackbar.LENGTH_SHORT).show();
                                }

                                signinButton.setVisibility(View.VISIBLE);
                                loginProgressBar.setVisibility(View.GONE);
                            }
                        });

            }
        });


        loginToSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);

                startActivity(intent);
            }
        });
    }
}