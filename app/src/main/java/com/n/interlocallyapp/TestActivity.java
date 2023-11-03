package com.n.interlocallyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class TestActivity extends AppCompatActivity {


    private TextView shopName, contactNumber, address, productNameView, descriptionView, priceView, discountView;
    private String iD, productPicture;
    private ImageView shopImage, productImage;
    private ArrayList<Map<String,Object>> product;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);

        shopImage = findViewById(R.id.shopImageView);
        shopName = findViewById(R.id.textViewShopName);
        contactNumber = findViewById(R.id.textViewPhoneNumber);
        address = findViewById(R.id.textViewShopAddress);
        productNameView = findViewById(R.id.textViewProductName);
        descriptionView = findViewById(R.id.textViewDescription);
        priceView = findViewById(R.id.textViewPrice);
        discountView = findViewById(R.id.textViewDiscount);
        productImage = findViewById(R.id.productImageView);

        //get the intent in the target activity
        Intent intent = getIntent();

        Bundle args = intent.getExtras();
        product = (ArrayList<Map<String, Object>>) args.getSerializable("products_dataProvider");
        iD = args.getString("ID_dataProvider");

        Log.d("TAG2","ID: " + iD);
        Log.d("TAG2", product.toString());

//                Map<String, Object> productData = (Map<String, Object>) entry.getValue();
//                if (productData.get("Name").equals(selectedProduct)) {
//                    Map<String, Object> productsMap = (Map<String, Object>) entry.getValue();

        for (Map<String,Object> map : product) {
            // we iterate over all values of the current map
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if(map.get("ID").equals(iD)){
                    if(entry.getKey().equals("Description")){
                        descriptionView.setText(entry.getValue().toString());
                    }else if(entry.getKey().equals("DiscountDescription")){
                        discountView.setText(entry.getValue().toString());
                    }else if(entry.getKey().equals("Name")){
                        productNameView.setText(entry.getValue().toString());
                    }else if(entry.getKey().equals("Picture")){
                        Picasso.get().load(entry.getValue().toString()).into(productImage);
//                        productPicture = entry.getValue().toString();
                    }else if(entry.getKey().equals("Price")){
                        priceView.setText(entry.getValue().toString());
                    }
                }
            }
        }
//        Picasso.get().load(productPicture).into(productImage);
        Picasso.get().load(args.getString("shopPicture_dataProvider")).into(shopImage);
        shopName.setText(args.getString("ShopName_dataProvider"));
        address.setText(args.getString("address_dataProvider"));
        contactNumber.setText(args.getString("contactNumber_dataProvider"));
    }
}