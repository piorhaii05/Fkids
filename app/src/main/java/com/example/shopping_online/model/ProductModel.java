package com.example.shopping_online.model;

import java.io.Serializable;

public class ProductModel implements Serializable {
    private String id, describe_product, image_product, name_product;
    private double price_product, rating_product;
    private boolean status_product;
    public ProductModel() {
    }

    public ProductModel(String describe_product, String id, String image_product, String name_product, double price_product, double rating_product, boolean status_product) {
        this.describe_product = describe_product;
        this.id = id;
        this.image_product = image_product;
        this.name_product = name_product;
        this.price_product = price_product;
        this.rating_product = rating_product;
        this.status_product = status_product;
    }

    public boolean isStatus_product() {
        return status_product;
    }

    public void setStatus_product(boolean status_product) {
        this.status_product = status_product;
    }

    public String getDescribe_product() {
        return describe_product;
    }

    public void setDescribe_product(String describe_product) {
        this.describe_product = describe_product;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage_product() {
        return image_product;
    }

    public void setImage_product(String image_product) {
        this.image_product = image_product;
    }

    public String getName_product() {
        return name_product;
    }

    public void setName_product(String name_product) {
        this.name_product = name_product;
    }

    public double getPrice_product() {
        return price_product;
    }

    public void setPrice_product(double price_product) {
        this.price_product = price_product;
    }

    public double getRating_product() {
        return rating_product;
    }

    public void setRating_product(double rating_product) {
        this.rating_product = rating_product;
    }
}
