package com.example.shopping_online.model;

import java.io.Serializable;

public class MyReviewModel implements Serializable {
    private String id, image_avatar_myreview, image_product_myreview, items_color_myreview, items_date_myreview, items_describe_myreview, items_name_myreview, items_size_myreview;
    private double items_rating_myreview;

    public MyReviewModel() {
    }

    public MyReviewModel(String id, String image_avatar_myreview, String image_product_myreview, String items_color_myreview, String items_date_myreview, String items_describe_myreview, String items_name_myreview, double items_rating_myreview, String items_size_myreview) {
        this.id = id;
        this.image_avatar_myreview = image_avatar_myreview;
        this.image_product_myreview = image_product_myreview;
        this.items_color_myreview = items_color_myreview;
        this.items_date_myreview = items_date_myreview;
        this.items_describe_myreview = items_describe_myreview;
        this.items_name_myreview = items_name_myreview;
        this.items_rating_myreview = items_rating_myreview;
        this.items_size_myreview = items_size_myreview;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage_avatar_myreview() {
        return image_avatar_myreview;
    }

    public void setImage_avatar_myreview(String image_avatar_myreview) {
        this.image_avatar_myreview = image_avatar_myreview;
    }

    public String getImage_product_myreview() {
        return image_product_myreview;
    }

    public void setImage_product_myreview(String image_product_myreview) {
        this.image_product_myreview = image_product_myreview;
    }

    public String getItems_color_myreview() {
        return items_color_myreview;
    }

    public void setItems_color_myreview(String items_color_myreview) {
        this.items_color_myreview = items_color_myreview;
    }

    public String getItems_date_myreview() {
        return items_date_myreview;
    }

    public void setItems_date_myreview(String items_date_myreview) {
        this.items_date_myreview = items_date_myreview;
    }

    public String getItems_describe_myreview() {
        return items_describe_myreview;
    }

    public void setItems_describe_myreview(String items_describe_myreview) {
        this.items_describe_myreview = items_describe_myreview;
    }

    public String getItems_name_myreview() {
        return items_name_myreview;
    }

    public void setItems_name_myreview(String items_name_myreview) {
        this.items_name_myreview = items_name_myreview;
    }

    public double getItems_rating_myreview() {
        return items_rating_myreview;
    }

    public void setItems_rating_myreview(double items_rating_myreview) {
        this.items_rating_myreview = items_rating_myreview;
    }

    public String getItems_size_myreview() {
        return items_size_myreview;
    }

    public void setItems_size_myreview(String items_size_myreview) {
        this.items_size_myreview = items_size_myreview;
    }
}
