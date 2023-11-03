package com.n.interlocallyapp;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Search extends AppCompatActivity {

    private String selectedCategory, selectedProduct;
    private AutoCompleteTextView autoCompleteProductsTxt;
    private final List<String> setCategories = new ArrayList<>();
    private final List<String> setProducts = new ArrayList<>();
    private final List<Map<String,Object>> setProfiles = new ArrayList<>();
    private final List<Map<String,Object>> setProductsProfiles = new ArrayList<>();
    private final List<Map<String,Object>> setCategoriesProfiles = new ArrayList<>();
    private final List<String> duplicatesCategories = new ArrayList<>();
    private final List<String> duplicatesProducts= new ArrayList<>();
    private final List<Map<String,Object>> duplicatesProfiles = new ArrayList<>();
    private final List<Map<String,Object>> duplicatesProductsProfiles = new ArrayList<>();
    private final List<Map<String,Object>> duplicatesCategoriesProfiles = new ArrayList<>();
    private Bundle args = new Bundle();

    private Button searchButton, loadButton;

    private TextView textViewData;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference shopReference = db.collection("Shop");
    private CollectionReference BrazilianFeedbacks = db.collection("LinkBrazil");
    private CollectionReference IndianFeedbacks = db.collection("IndianShop");
    private CollectionReference ChineseFeedbacks = db.collection("ChineseShop");

    //creating and initializing an Intent object
    private Intent testIntent;
    private Intent categoryIntent;

    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

//        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
//        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

        testIntent = new Intent(this, TestActivity.class);

        args = new Bundle();

        categoryFinder();

        loadButton = findViewById(R.id.loadBtn);
        searchButton = findViewById(R.id.searchBtn);

        textViewData = findViewById(R.id.textViewData);
        AutoCompleteTextView autoCompleteCategoryTxt = findViewById(R.id.auto_complete_category_txt);
        autoCompleteProductsTxt = findViewById(R.id.auto_complete_product_txt);

        ArrayAdapter<String> adapterCategories = new ArrayAdapter<String>(this, R.layout.dropdown_item, setCategories);
        ArrayAdapter<String> adapterProducts = new ArrayAdapter<>(this, R.layout.dropdown_item, setProducts);

        autoCompleteCategoryTxt.setAdapter(adapterCategories);
        autoCompleteProductsTxt.setAdapter(adapterProducts);

        autoCompleteCategoryTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = parent.getItemAtPosition(position).toString();
//                Toast.makeText(getApplicationContext(), "Item: " + selectedCategory, Toast.LENGTH_SHORT).show();

                productsFinder(selectedCategory);
                args.putString("selectedCategory_dataProvider", selectedCategory);

                autoCompleteProductsTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectedProduct = parent.getItemAtPosition(position).toString();
//                        Toast.makeText(getApplicationContext(), "Item: " + selectedProduct, Toast.LENGTH_SHORT).show();

                        duplicatesProductsProfiles.clear();
                        for (Map<String,Object> map : setProductsProfiles) {
                            // we iterate over all values of the current map
                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                Map<String, Object> productData = (Map<String, Object>) entry.getValue();
                                if (productData.get("Name").equals(selectedProduct)) {
                                    Map<String, Object> productsMap = (Map<String, Object>) entry.getValue();
                                    duplicatesProductsProfiles.add(productsMap);
                                }
                            }
                        }

                        args.putSerializable("products_dataProvider", (ArrayList<Map<String, Object>>) duplicatesProductsProfiles);
                        Log.d("TAG_1", duplicatesProductsProfiles.toString());
                        args.putString("selectedProduct_dataProvider", selectedProduct);
                    }
                });
            }
        });

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //attach the bundle to the Intent object
                testIntent.putExtras(args);

                //finally start the activity
                startActivity(testIntent);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedCategory == null || selectedProduct == null){
                    textViewData.setText("Please choose category and product before proceeding.");
                }else {
                    getMap(selectedCategory);
                }
            }
        });
    }

    private void getMap(String selectedCategory) {
        shopReference
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        categoryIntent = new Intent(Search.this, MapsActivity.class);

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    String ID = document.getId();
                                    Map<String, Object> shop = document.getData();
                                    Map<String, Object> product = (Map<String, Object>) shop.get("ShopCuisineProduct");
                                    Map<String, Object> category = (Map<String, Object>) shop.get("CuisineCategory");
                                    for (Map.Entry<String, Object> entry : category.entrySet()) {
                                        for (Map.Entry<String, Object> entry2 : product.entrySet()) {
                                            Map<String, Object> productData = (Map<String, Object>) entry2.getValue();
                                            for (Map.Entry<String, Object> dataEntry : productData.entrySet()) {
                                                if (dataEntry.getKey().equals("Name") && entry.getValue().equals(selectedCategory)) {
                                                    for (Map.Entry<String, Object> entry3 : shop.entrySet()) {
                                                        if (entry3.getKey().equals("ShopCuisineProfile")) {
                                                            Map<String, Object> profileMap = (Map<String, Object>) entry3.getValue();
                                                            profileMap.put("ID", ID);
                                                            Log.d("TAGSS", productData.toString());
                                                            duplicatesProfiles.add(profileMap);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Toast.makeText(Search.this, "No such document", Toast.LENGTH_SHORT).show();
                                    Log.d("TAG", "No such document");
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        Set<Map<String,Object>> set = new HashSet<Map<String,Object>>(duplicatesProfiles);
                        for (Map p : set) {
                            setProfiles.add(p);
                        }

//                        args.putSerializable("products_dataProvider", (ArrayList<Map<String, Object>>) setProductsProfiles);
//                        args.putSerializable("categories_dataProvider", (ArrayList<Map<String, Object>>) setCategoriesProfiles);
                        args.putSerializable("profiles_dataProvider", (ArrayList<Map<String, Object>>) setProfiles);
                        Log.d("TAG_1", setProfiles.toString());
                        Log.d("TAG_1", args.toString());
                        categoryIntent.putExtras(args);
                        startActivity(categoryIntent);
                    }
                });
    }

    private void productsFinder(String selectedCategory) {
        duplicatesProducts.clear();
        shopReference
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    String ID = document.getId();
                                    Map<String, Object> data = document.getData();
                                    Map<String, Object> product = (Map<String, Object>) data.get("ShopCuisineProduct");
                                    Map<String, Object> category = (Map<String, Object>) data.get("CuisineCategory");
                                    for (Map.Entry<String, Object> entry : category.entrySet()) {
                                        for (Map.Entry<String, Object> entry2 : product.entrySet()) {
                                            Map<String, Object> productData = (Map<String, Object>) entry2.getValue();
                                            productData.put("ID", ID);
                                            if(productData.get("Availability").equals(true)) {
                                                for (Map.Entry<String, Object> dataEntry : productData.entrySet()) {
                                                    if (dataEntry.getKey().equals("Name") && entry.getValue().equals(selectedCategory)) {
                                                        duplicatesProducts.add(dataEntry.getValue().toString());
                                                        Log.d("TAG", dataEntry.getValue().toString());
                                                        for (Map.Entry<String, Object> entry3 : data.entrySet()) {
                                                            if (entry3.getKey().equals("ShopCuisineProduct")) {
                                                                Map<String, Object> productMap = (Map<String, Object>) entry3.getValue();
                                                                duplicatesProductsProfiles.add(productMap);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
//                                    Toast.makeText(Search.this, "No such document", Toast.LENGTH_SHORT).show();
                                    Log.d("TAG", "No such document");
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        Set<Map<String,Object>> set1 = new HashSet<Map<String,Object>>(duplicatesProductsProfiles);
                        for (Map p : set1) {
                            setProductsProfiles.add(p);
                        }
                        Log.d("TAG_1", setProductsProfiles.toString());

                        setProducts.clear();
                        Set<String> set = new HashSet<>(duplicatesProducts);
                        for (String p : set) {
                            setProducts.add(p);
                        }
                        Collections.sort(setProducts, new Comparator<String>() {
                            @Override
                            public int compare(String o1, String o2) {
                                return o1.compareTo(
                                        o2);
                            }
                        });
                    }
                });
    }

    private void categoryFinder() {
        duplicatesCategories.clear();
        shopReference
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    Map<String, Object> cuisineCategory = document.getData();
                                    for (Map.Entry<String, Object> entry : cuisineCategory.entrySet()) {
                                        if (entry.getKey().equals("CuisineCategory")) {
                                            Map<String, Object> categoryName = (Map<String, Object>) entry.getValue();
                                            for (Map.Entry<String, Object> dataEntry : categoryName.entrySet()) {
                                                if (dataEntry.getKey().equals("Name")) {
                                                    duplicatesCategories.add(dataEntry.getValue().toString());
                                                    Log.d("TAGS", dataEntry.getValue().toString());
                                                    for (Map.Entry<String, Object> entry3 : cuisineCategory.entrySet()) {
                                                        if (entry3.getKey().equals("CuisineCategory")) {
                                                            Map<String, Object> categoryMap = (Map<String, Object>) entry3.getValue();
                                                            duplicatesCategoriesProfiles.add(categoryMap);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Toast.makeText(Search.this, "No such document", Toast.LENGTH_SHORT).show();
                                    Log.d("TAG", "No such document");
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        Set<Map<String,Object>> set1 = new HashSet<>(duplicatesCategoriesProfiles);
                        for (Map p : set1) {
                            setCategoriesProfiles.add(p);
                        }
                        Log.d("TAG_1", setCategoriesProfiles.toString());

                        Set<String> set = new HashSet<>(duplicatesCategories);
                        for (String p : set) {
                            setCategories.add(p);
                        }
                        Collections.sort(setCategories, new Comparator<String>() {
                            @Override
                            public int compare(String o1, String o2) {
                                return o1.compareTo(
                                        o2);
                            }
                        });
                    }
                });
    }

//    private void feedbacks() {
//        duplicatesCategories.clear();
//        BrazilianFeedbacks
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                if (document.exists()) {
//                                    Map<String, Object> feedbacks = document.getData();
//                                    for (Map.Entry<String, Object> entry : feedbacks.entrySet()) {
//                                        if (entry.getKey().equals("rating")) {
//
//                                            Map<String, Object> categoryName = (Map<String, Object>) entry.getValue();
//                                            for (Map.Entry<String, Object> dataEntry : categoryName.entrySet()) {
//                                                if (dataEntry.getKey().equals("Name")) {
//                                                    duplicatesCategories.add(dataEntry.getValue().toString());
//                                                    Log.d("TAGS", dataEntry.getValue().toString());
//                                                    for (Map.Entry<String, Object> entry3 : cuisineCategory.entrySet()) {
//                                                        if (entry3.getKey().equals("CuisineCategory")) {
//                                                            Map<String, Object> categoryMap = (Map<String, Object>) entry3.getValue();
//                                                            duplicatesCategoriesProfiles.add(categoryMap);
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                } else {
//                                    Toast.makeText(Search.this, "No such document", Toast.LENGTH_SHORT).show();
//                                    Log.d("TAG", "No such document");
//                                }
//                            }
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//
//                        Set<Map<String,Object>> set1 = new HashSet<>(duplicatesCategoriesProfiles);
//                        for (Map p : set1) {
//                            setCategoriesProfiles.add(p);
//                        }
//                        Log.d("TAG_1", setCategoriesProfiles.toString());
//
//                        Set<String> set = new HashSet<>(duplicatesCategories);
//                        for (String p : set) {
//                            setCategories.add(p);
//                        }
//                        Collections.sort(setCategories, new Comparator<String>() {
//                            @Override
//                            public int compare(String o1, String o2) {
//                                return o1.compareTo(
//                                        o2);
//                            }
//                        });
//                    }
//                });
//    }

}