package com.vinkert.inbetween;
import com.yelp.fusion.client.models.Business;

import java.io.Serializable;
import java.util.ArrayList;

public class BusinessWrapper implements Serializable {

    private ArrayList<Business> businesses;

    public BusinessWrapper(ArrayList<Business> data) {
        this.businesses = data;
    }

    public ArrayList<Business> getBusinesses() {
        return this.businesses;
    }

}