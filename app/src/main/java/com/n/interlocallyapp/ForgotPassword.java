package com.n.interlocallyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText mEmail;
    private Button resetPassword;
    private TextView error;

    private String email;

    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mEmail = findViewById(R.id.Email);
        resetPassword = findViewById(R.id.ResetBtn);
        error = findViewById(R.id.errorMessage);
        fAuth = FirebaseAuth.getInstance();

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = mEmail.getText().toString();

                if (!validateEmail(email)) {
                    return;
                }
                reset();
            }

            private void reset() {

                //authenticate the user
                fAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showAlertDialog("Please check your email.");
//                            Toast.makeText(ForgotPassword.this, "Please check your email.", Toast.LENGTH_SHORT).show();
                        } else {
                            error.setText(task.getException().getMessage());
                        }
                    }
                });
            }
        });
    }

    private void showAlertDialog(String s) {

        AlertDialog dialog = new AlertDialog.Builder(ForgotPassword.this)
                .setTitle("Password Reset")
                .setMessage(s)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getApplicationContext(), Login.class));
                        finish();
                    }
                }).create();
        dialog.show();
    }

    private boolean validateEmail(String email) {

        if (email.isEmpty()) {
            error.setText("Field can't be empty");//changes the selected item text to this
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            error.setText("Enter a valid email address Eg: user@user.com"); //changes the selected item text to this
            return false;
        } else {
            error.setText("");
            return true;
        }
    }
}