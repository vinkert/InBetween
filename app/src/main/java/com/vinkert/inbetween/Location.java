package com.vinkert.inbetween;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent for Coding on 4/25/2017.
 */

class Location {
    private double latitude;
    private double longitude;
    public List<Double> cartesian;
    public Location(double latitude, double longitude)  {
        this.latitude = latitude;
        this.longitude = longitude;
        cartesian = convertToCartesian(latitude, longitude);
    }

    private static List<Double> convertToCartesian(double latitude, double longitude)    {
        List<Double> xyz = new ArrayList<Double>(3);
        double x = Math.cos(latitude) * Math.cos(longitude);
        double y = Math.cos(latitude) * Math.sin(longitude);
        double z =  Math.sin(latitude);
        xyz.add(x);
        xyz.add(y);
        xyz.add(z);
        return xyz;
    }
}
