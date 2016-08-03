package com.ensharp.haxi.Map;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by min on 2016-07-25.
 */
public class AccureCurrentPath {

    public boolean drawPolyline(GoogleMap gMap, LatLng pastPath, LatLng currentPath )
    {
        PolylineOptions poption = new PolylineOptions().add(pastPath).add(currentPath).width(30).color(Color.RED).geodesic(true);

        if( Math.abs( pastPath.latitude - currentPath.latitude ) > 0.001 || Math.abs( pastPath.longitude - currentPath.longitude ) > 0.001 )
        {
            return false;
        }/*
        else if (Math.abs( pastPath.latitude - currentPath.latitude ) < 0.00005 || Math.abs( pastPath.longitude - currentPath.longitude ) < 0.00005)
        {
            return false;
        }*/
        else
        {
            gMap.addPolyline(poption);
            return true;
        }
    }

    public void deletePolyline(GoogleMap gMap)
    {
        gMap.clear();
    }

}
