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

    private static Location findMiddleLoc(Location loc1, Location loc2) {
        double averageX = (loc1.cartesian.get(0) + loc2.cartesian.get(0))/2;
        double averageY = (loc1.cartesian.get(1) + loc2.cartesian.get(1))/2;
        double averageZ = (loc1.cartesian.get(2) + loc2.cartesian.get(2))/2;
        double longitude = Math.atan2(averageY, averageX);
        double hypotenuse = Math.sqrt(averageX * averageX + averageY * averageY);
        double latitude = Math.atan2(averageZ, hypotenuse);
        return new Location(latitude, longitude);
    }
}
