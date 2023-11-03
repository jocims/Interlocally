package com.n.interlocallyapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[!@#$%^&+=])" +    //at least 1 special character
                    ".{8,}" +               //at least 8 characters
                    "$");

    private EditText mEmail, mPassword;
    private Button mLoginBtn;
    private TextView passwordInput, emailInput, textRegister, textPassword;

    private String email, password;
    private boolean passwordVisible;

    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // giving values to attributes from activity
        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.password);
        fAuth = FirebaseAuth.getInstance();
        mLoginBtn = findViewById(R.id.loginBtn);

        passwordInput = findViewById(R.id.passwordInput);
        emailInput = findViewById(R.id.emailInput);

        seePassword();

        //validate the user input
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = mEmail.getText().toString().trim();
                password = mPassword.getText().toString().trim();

                if (!validateEmail(email) | !validatePassword(password)) {
                    return;
                }
                authentication();
            }
        });

        //Make TextView clickable
        clickableText();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void seePassword() {
        mPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int Right=2;
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    if(motionEvent.getRawX()>= mPassword.getRight()-mPassword.getCompoundDrawables()[Right].getBounds().width()){
                        int selection = mPassword.getSelectionEnd();
                        if(passwordVisible){
                            //set drawable image here
                            mPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_off_24, 0);
                            // for hide password
                            mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible=false;
                        }else {
                            // set drawable image here
                            mPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_24, 0);
                            // for show password
                            mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible=true;
                        }
                        mPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void clickableText() {
        textRegister = findViewById(R.id.Register);
        textPassword = findViewById(R.id.PasswordText);

        String register = "First-time User? Register Here";
        String password = "Forgotten Password? Click Here";

        SpannableString sRegister = new SpannableString(register);
        SpannableString sPassword = new SpannableString(password);

        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
//                Toast.makeText(Login.this, "One", Toast.LENGTH_SHORT).show();

                //Going from Login to Registration when clicked.
                Intent registrationIntent = new Intent(Login.this, Register.class);

                startActivity(registrationIntent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(true);
            }
        };

        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
//                Going from Login to password reset when clicked.
                Intent passwordIntent = new Intent(Login.this, ForgotPassword.class);

                startActivity(passwordIntent);

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(true);
            }
        };

        sRegister.setSpan(clickableSpan1, 17, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sPassword.setSpan(clickableSpan2, 20, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //Set new registration text now clickable
        textRegister.setText(sRegister);
        textRegister.setMovementMethod(LinkMovementMethod.getInstance());

        // Set new forgot password text now clickable
        textPassword.setText(sPassword);
        textPassword.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void authentication() {
        //authenticate the user
        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Login.this, "Logged in Successfully.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    emailInput.setText(task.getException().getMessage());
                }
            }
        });
    }

    private boolean validateEmail(String email) {

        if (email.isEmpty()) {
            emailInput.setText("Field can't be empty");//changes the selected item text to this
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setText("Enter a valid email address Eg: user@user.com"); //changes the selected item text to this
            return false;
        } else {
            emailInput.setText("");
            return true;
        }
    }

    private boolean validatePassword(String password) {

        if (password.isEmpty()) {
            passwordInput.setText("Field can't be empty");
            return false;
            //defining the length of password, if is less than 8 characters it will display an error message
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            passwordInput.setText("Password must have at least: \n" +
                    "8 characters \n " +
                    "One number \n" +
                    "One upper case letter \n" +
                    "One lower case letter \n" +
                    "One special character (!@#$%^&+=)");
            return false;
        } else {
            passwordInput.setText("");
            return true;
        }
    }

//    public void loadNotes(View v){
//        shopReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                String data = "";
//
//                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
//                    ShopOwner shopOwner = documentSnapshot.toObject(ShopOwner.class);
//                    shopOwner.setId(documentSnapshot.getId());
//
//                    String email = shopOwner.getEmail();
//                    String password = shopOwner.getPassword();
//
//                    data += "email: " + email + "\npassword: " + password + "\n\n";
//                }
//
//            }
//        });
//    }

//    protected void onStart() {
//        super.onStart();
//
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        CollectionReference shopReference = db.collection("Shop");
//
//        shopReference.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
//                if (e != null){
//                    return;
//                }
//
//                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
//                    ShopOwner shopOwner = documentSnapshot.toObject(ShopOwner.class);
//                    shopOwner.setId(documentSnapshot.getId());
//
//                    String id = shopOwner.getId();
//                    String email = shopOwner.getEmail();
//                    String password = shopOwner.getPassword();
//                }
//            }
//        });
//    }
}