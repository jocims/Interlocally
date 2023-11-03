package com.n.interlocallyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
//import com.google.android.gms.location.LocationRequest;


public class Register extends AppCompatActivity {
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[!@#$%^&+=])" +    //at least 1 special character
                    ".{8,}" +               //at least 8 characters
                    "$");

    private String password, email,latitude, longitude;;
    private boolean passwordVisible;

    private FirebaseAuth fAuth;

    private EditText mEmail, mPassword;
    private Button mRegisterBtn;
    private TextView passwordInput, emailInput, locationView;

    // location variables
    private Button locationBtn;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initiateVariables();
        seePassword();

        //Check if user already exists in database
        validation();

        // making text clickable
        clickableText();

        locationButton();
        System.out.println("latitude " + latitude + ", longitude " + longitude);
//        locationView.setText("latitude " + latitude + ", longitude " + longitude);

        // validate user input
        registration();
    }

    private void locationButton() {
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check permission
                if (ActivityCompat.checkSelfPermission(Register.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // When permission granted
                    getLocation();
//                  locationView.setText("latitude " + latitude + ", longitude " + longitude); //changes the selected item text to this
                } else {
                    ActivityCompat.requestPermissions(Register.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }
        });
    }

    private void initiateVariables() {
        // giving values to attributes from activity
        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.password);
        mRegisterBtn = findViewById(R.id.registerBtn);
        fAuth = FirebaseAuth.getInstance();

        passwordInput = findViewById(R.id.passwordInput);
        emailInput = findViewById(R.id.emailInput);
        locationView = findViewById(R.id.locationText);

        //Assign location variables
        locationBtn = findViewById(R.id.locationBtn);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Register.this);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void seePassword() {
        mPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int Right = 2;
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getRawX() >= mPassword.getRight() - mPassword.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = mPassword.getSelectionEnd();
                        if (passwordVisible) {
                            //set drawable image here
                            mPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_off_24, 0);
                            // for hide password
                            mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        } else {
                            // set drawable image here
                            mPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_24, 0);
                            // for show password
                            mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                        mPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void validation() {
        //check if the user has already created one account
        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    private void clickableText() {
        TextView textLogin = findViewById(R.id.Login);

        String login = "Already Registered? Click Here";

        SpannableString sLogin = new SpannableString(login);

        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {

                //Going from Registration to Login when clicked.
                Intent loginIntent = new Intent(Register.this, Login.class);

                startActivity(loginIntent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(true);
            }
        };

        sLogin.setSpan(clickableSpan1, 20, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // change textView to new clickable text
        textLogin.setText(sLogin);
        textLogin.setMovementMethod(LinkMovementMethod.getInstance());
    }

    // Register user on Firebase
    private void registration() {
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = mEmail.getText().toString().trim();
                password = mPassword.getText().toString().trim();

                user = new User(email, latitude, longitude);
//
//                if (!isLocationSaved()) {
//                    // show my message
//                    locationView.setText("Location needs to be accessed.");
//                }else{
//                    locationView.setText("");
//                }

                if (!validateEmail(email) | !validatePassword(password)) {
                    return;
                }

                //register the user into the firebase
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //display error to the user or the result if the user was created
                        if (task.isSuccessful()) {
                            user.createNewUser();
                            Toast.makeText(Register.this, "User Created!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Register.class));
                        } else {
                            emailInput.setText(task.getException().getMessage());
                        }

                    }
                });
            }
        });
    }

    // Validate email input
    @SuppressLint("SetTextI18n")
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

    // Validate Password Input
    @SuppressLint("SetTextI18n")
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

    private boolean isLocationSaved() {
        return latitude != "" && longitude != "";
    }

    // Get location
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    latitude = location.getLongitude() + "";
                    longitude = location.getLatitude() + "";
                    locationView.setText(latitude + ", " + longitude);
                }
            }
        });
    }
}