package com.n.interlocallyapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Retrieve extends ShopOwner{


    FirebaseFirestore firestore;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;

    private String email, password;

    public void retrieveData() {
        reference = FirebaseDatabase.getInstance().getReference().child("Shop");

        firestore = FirebaseFirestore.getInstance();
        firestore.collection("Shop").document()
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ShopOwner shopOwner = documentSnapshot.toObject(ShopOwner.class);
                email = shopOwner.getEmail().trim();;
                password = shopOwner.getPassword().trim();

                authenticate();
            }
        });
    }

    private void authenticate() {
        //register the user into the firebase
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                System.out.println("User Authenticated.");
            }
        });
    }
}
