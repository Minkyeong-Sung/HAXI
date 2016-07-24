package com.ensharp.haxi;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class SearchLocation {
    private static String TAG = "MainActivity";
    Geocoder geocoder;
    GoogleMap gMap;
    LatLng searchLatLng;
    Double searchLatLng_latitude;
    Double searchLatLng_longitude;
    String searchStr;

    public SearchLocation(GoogleMap gMap, Geocoder geocoder) {
        this.gMap = gMap;
        this.geocoder = geocoder;
    }

    public void findLocation(String searchStr) {
        this.searchStr = searchStr;
        if (searchStr.equals(null))
            return;
        List<Address> addressList;
        try {
            // Geocoder객체인 gc에서 Location이름에 대한 정보를 List에 담기.
            addressList = geocoder.getFromLocationName(searchStr, 1);

            if (addressList != null) {

                searchLatLng_latitude = addressList.get(0).getLatitude();
                searchLatLng_longitude = addressList.get(0).getLongitude();

                searchLatLng = new LatLng(searchLatLng_latitude, searchLatLng_longitude);
                gMap.clear();

                MarkerOptions options = new MarkerOptions().title(searchLatLng_latitude.toString() + "\n" + searchLatLng_longitude).position(searchLatLng).draggable(true);
                gMap.addMarker(options);
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchLatLng, 18));
            }

        } catch (IOException ex) {
            // 예외처리 Log로 찍어줌!
            Log.d(TAG, "예외 : " + ex.toString());
        }

    }
}