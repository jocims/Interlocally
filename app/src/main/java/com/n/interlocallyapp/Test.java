package com.n.interlocallyapp;

import com.google.firebase.database.Exclude;

public class Test {
    private String id, test;

    public Test() {
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Test(String test) {
        this.test = test;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }


//
//    for (Map.Entry<String, Object> dataEntry : profileMap.entrySet()) {
//        if (dataEntry.getKey().equals("location")) {
//            //                                                    Toast.makeText(Search.this, dataEntry.getValue().toString(), Toast.LENGTH_SHORT).show();
//            GeoPoint geoPoint = (GeoPoint) dataEntry.getValue();
//            latitude = geoPoint.getLatitude();
//            longitude = geoPoint.getLongitude();
//
//            position = new LatLng(latitude, longitude);
//            args.putParcelable("longLat_dataProvider",  position);
//            Log.d("TAG", dataEntry.getValue().toString());
//        }
//        if (dataEntry.getKey().equals("Name")) {
//            args.putString("shopName_dataProvider",dataEntry.getValue().toString());
//            Log.d("TAG", dataEntry.getValue().toString());
//        }
//        if (dataEntry.getKey().equals("address")) {
//            args.putString("address_dataProvider",dataEntry.getValue().toString());
//            Log.d("TAG", dataEntry.getValue().toString());
//        }
//        if (dataEntry.getKey().equals("contactNumber")) {
//            args.putString("contactNumber_dataProvider",dataEntry.getValue().toString());
//            Log.d("TAG", dataEntry.getValue().toString());
//        }
//        if (dataEntry.getKey().equals("picture")) {
//            args.putString("shopPicture_dataProvider",dataEntry.getValue().toString());
//            Log.d("TAG", dataEntry.getValue().toString());
//        }
//    }

}



