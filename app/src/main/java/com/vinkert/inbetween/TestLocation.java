package com.vinkert.inbetween;

/**
 * Created by Vincent for Coding on 4/25/2017.
 */

public class TestLocation {
    public static void main(String[] args)  {
        Location loc = new Location(0.710599509, -1.291647896);
        for(Double temp: loc.cartesian)
            System.out.println(temp);
    }
}
