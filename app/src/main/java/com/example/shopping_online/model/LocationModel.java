package com.example.shopping_online.model;

import java.io.Serializable;

public class LocationModel implements Serializable {
    String id, street_location, ward_location, district_location, city_location;

    public LocationModel() {
    }

    public LocationModel(String city_location, String district_location, String id, String street_location, String ward_location) {
        this.city_location = city_location;
        this.district_location = district_location;
        this.id = id;
        this.street_location = street_location;
        this.ward_location = ward_location;
    }

    public String getCity_location() {
        return city_location;
    }

    public void setCity_location(String city_location) {
        this.city_location = city_location;
    }

    public String getDistrict_location() {
        return district_location;
    }

    public void setDistrict_location(String district_location) {
        this.district_location = district_location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStreet_location() {
        return street_location;
    }

    public void setStreet_location(String street_location) {
        this.street_location = street_location;
    }

    public String getWard_location() {
        return ward_location;
    }

    public void setWard_location(String ward_location) {
        this.ward_location = ward_location;
    }
}
