package com.ensharp.haxi.Map;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.EditText;

import com.ensharp.haxi.UcMainActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class SearchLocation {
    private static String TAG = "UcMainActivity";
    private Geocoder geocoder;
    private GoogleMap gMap;
    private LatLng searchLatLng;
    private Double searchLatLng_latitude;
    private Double searchLatLng_longitude;
    private EditText input_text;

    public static Marker startMarker;
    public static Marker destinationMarker;
    public static boolean startMarker_flag = false;
    public static boolean destinationMarker_flag = false;
    public static int START = 1;
    public static int DESTINATION = 2;

    public SearchLocation(GoogleMap gMap, Geocoder geocoder) {
        this.gMap = gMap;
        this.geocoder = geocoder;
    }

    // 위치 찾기 위한 메소드
    public void findLocation(String searchStr, int option, EditText input) {
        this.input_text = input;
        List<Address> addressList = null;

        // 입력하지 않았으면 return
        if (option == START && input_text.length() == 0) {
            return;
        } else if (option == DESTINATION && input_text.length() == 0) {
            return;
        }

        try {
            // Geocoder객체인 gc에서 Location이름에 대한 정보를 List에 담기.
            addressList = geocoder.getFromLocationName(searchStr, 1);

            if (addressList.size() != 0) {
                // 위경도값 받기.
                searchLatLng_latitude = addressList.get(0).getLatitude();
                searchLatLng_longitude = addressList.get(0).getLongitude();
                searchLatLng = new LatLng(searchLatLng_latitude, searchLatLng_longitude);

                // 출발지와 도착지 Marker 구분해주기.
                // 출발지에 관했을 경우.
                if (option == START) {
                    if(startMarker_flag == true)            // 출발지에 대한 Marker가 이미 존재할 때 삭제 해주기.
                        startMarker.remove();
                    startMarker = gMap.addMarker(new MarkerOptions().position(new LatLng(searchLatLng_latitude, searchLatLng_longitude))
                            .title("출발지\n" + searchLatLng_latitude.toString() + "\n" + searchLatLng_longitude)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            .draggable(true));
                    startMarker.showInfoWindow();
                    startMarker_flag = true;
                    UcMainActivity.start_URL_latlng = new StringBuilder(searchLatLng_longitude +","+searchLatLng_latitude); // Url 해당 위치로 갱신하기 위한 작업.
                }

                else if(option == DESTINATION) {
                    if(destinationMarker_flag == true)
                        destinationMarker.remove();
                    destinationMarker = gMap.addMarker(new MarkerOptions().position(new LatLng(searchLatLng_latitude, searchLatLng_longitude))
                            .title("도착지\n" + searchLatLng_latitude.toString() + "\n" + searchLatLng_longitude)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                            .draggable(true));
                    destinationMarker.showInfoWindow();
                    destinationMarker_flag = true;
                    UcMainActivity.destination_URL_latlng = new StringBuilder(searchLatLng_longitude +","+searchLatLng_latitude);
                }
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchLatLng, 16));
            } else {
                return;
            }

        } catch (IOException ex) {
            // 예외처리 Log로 찍어줌!
            Log.d(TAG, "예외 : " + ex.toString());
        }
        gMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                // Drag 끝냈을 경우 해당 위치에 대한 Position값(위,경도) 저장.
                LatLng end_LatLng = marker.getPosition();

                // Marker Title 변경해주기.
                marker.setTitle(end_LatLng.latitude + "\n" + end_LatLng.longitude);
                // 해당 위치에 대한 searchLocation 실행
                searchLocation(end_LatLng.latitude,end_LatLng.longitude,input_text);
                // 출발지에 대한 Marker일 경우 이 위치로 URL 변경해주기.
                if( marker.getId().equals("m0")) {
                    UcMainActivity.start_URL_latlng = new StringBuilder( end_LatLng.longitude +","+end_LatLng.latitude);
                }
                // 도착지에 대한 Marker일 경우 이 위치로 URL 변경해주기.
                else if (marker.getId().equals("m1")) {
                    UcMainActivity.destination_URL_latlng = new StringBuilder( end_LatLng.longitude +","+end_LatLng.latitude);
                }

            }
        });
    }


    // 위치 좌표를 이용해 주소를 검색하는 메소드 정의
    public void searchLocation(double latitude, double longitude, EditText input_text) {

        List<Address> addressList = null;
        try {

            addressList = geocoder.getFromLocation(latitude, longitude, 1);
            // 주소 정보 저장.
            Address outAddr = addressList.get(0);

            if (addressList != null) {
                input_text.setText(outAddr.getAddressLine(0));
            }

        } catch(IOException ex) {
            Log.d(TAG, "예외 : " + ex.toString());
        }

    }
}