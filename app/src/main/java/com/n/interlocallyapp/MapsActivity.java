package com.n.interlocallyapp;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.n.interlocallyapp.databinding.ActivityMapsBinding;

import java.util.ArrayList;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Intent productInfoIntent;
    private Bundle args;
    private ArrayList<Map<String,Object>> profile;
    private String shopName, address, contactNumber, picture, ID;
    private double latitude, longitude,longitudeUser, latitudeUser;
    LatLng location, locationUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser activeUser = FirebaseAuth.getInstance().getCurrentUser();
    private String currentId = activeUser.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        productInfoIntent = getIntent();
        args = productInfoIntent.getExtras();
        profile = (ArrayList<Map<String, Object>>) args.getSerializable("profiles_dataProvider");
        Log.d("TAG_MAP", profile.toString());

        db.collection("Users").document(currentId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                latitudeUser = Double.parseDouble(documentSnapshot.getString("latitude"));
                longitudeUser = Double.parseDouble(documentSnapshot.getString("longitude"));

                Log.d("TAG_MAP", "locationUser: " + latitudeUser + ", " + longitudeUser);
            }
        });

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // we iterate over all arrays of Map
        for (Map<String,Object> map : profile) {
            // we iterate over all values of the current map
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if(latitude == 0 || longitude == 0 || shopName == null || address == null || picture == null || contactNumber == null || ID == null){
                    if (entry.getKey().equals("latitude")) {
                        latitude = (double) entry.getValue();
                    }
                    if (entry.getKey().equals("longitude")) {
                        longitude = (double) entry.getValue();
                    }
                    if(entry.getKey().equals("Name")){
                        shopName = (String) entry.getValue();
                    }
                    if(entry.getKey().equals("address")){
                        address = (String) entry.getValue();
                    }
                    if(entry.getKey().equals("contactNumber")){
                        contactNumber = (String) entry.getValue();
                    }
                    if(entry.getKey().equals("picture")){
                        picture = (String) entry.getValue();
                    }
                    if(entry.getKey().equals("ID")){
                        ID = (String) entry.getValue();
                    }
                    if(latitude != 0 && longitude != 0 && shopName != null) {
                        location = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions().position(location).title(shopName));
                        Log.d("TAG_MAP", "location: " + latitude + ", " + longitude + " NAME: " + shopName + " address: " + address + " contactNumber " + contactNumber + " picture: " + picture + " ID: " + ID);
                    }
                }else{
                    latitude = 0;
                    longitude = 0;
                }
            }

        }
        Log.d("TAG_MAP", "OUTSIDE MARKER");

        locationUser = new LatLng(latitudeUser,longitudeUser);
//        Log.d("TAG_MAP", "locationUser: " + latitudeUser + ", " + longitudeUser);

        MarkerOptions userLocation = new MarkerOptions().position(locationUser).title("You are here.");
        userLocation.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mMap.addMarker(userLocation);

        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(locationUser));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
            @Override
            public boolean onMarkerClick(Marker arg0) {
                if(arg0.getTitle().equals("You are here.")){
                    return false;
                }else if (arg0.getTitle() != null) {
                    if(arg0.getPosition().equals(location)) {
                        Log.d("TAG_MAP", "SELECTED NAME: " + shopName);
                        args.putString("ID_dataProvider", ID);
                        args.putString("ShopName_dataProvider", shopName);
                        args.putString("address_dataProvider", address);
                        args.putString("contactNumber_dataProvider", contactNumber);
                        args.putString("shopPicture_dataProvider", picture);
                        Log.d("TAG_MAP", "NAME SELECTED " + arg0.getTitle());
                        productInfoIntent = new Intent(MapsActivity.this, ProductInfo.class);
                        productInfoIntent.putExtras(args);
                        startActivity(productInfoIntent);
                        Log.d("TAG_MAP", "INSIDE MARKER");
                        return true;
                    }
                }
                return true;
            }
        });
    }
}