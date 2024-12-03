package com.example.shopping_online.model;

import java.io.Serializable;

public class ListLikeModel implements Serializable {

    private String id, describe_product_listlike, image_product_listlike, name_product_listlike , productId;
    private double price_product_listlike, rating_product_listlike;

    public ListLikeModel() {
    }

    public ListLikeModel(String describe_product_listlike, String id, String image_product_listlike, String name_product_listlike, double price_product_listlike, String productId, double rating_product_listlike) {
        this.describe_product_listlike = describe_product_listlike;
        this.id = id;
        this.image_product_listlike = image_product_listlike;
        this.name_product_listlike = name_product_listlike;
        this.price_product_listlike = price_product_listlike;
        this.productId = productId;
        this.rating_product_listlike = rating_product_listlike;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getDescribe_product_listlike() {
        return describe_product_listlike;
    }

    public void setDescribe_product_listlike(String describe_product_listlike) {
        this.describe_product_listlike = describe_product_listlike;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage_product_listlike() {
        return image_product_listlike;
    }

    public void setImage_product_listlike(String image_product_listlike) {
        this.image_product_listlike = image_product_listlike;
    }

    public String getName_product_listlike() {
        return name_product_listlike;
    }

    public void setName_product_listlike(String name_product_listlike) {
        this.name_product_listlike = name_product_listlike;
    }

    public double getPrice_product_listlike() {
        return price_product_listlike;
    }

    public void setPrice_product_listlike(double price_product_listlike) {
        this.price_product_listlike = price_product_listlike;
    }

    public double getRating_product_listlike() {
        return rating_product_listlike;
    }

    public void setRating_product_listlike(double rating_product_listlike) {
        this.rating_product_listlike = rating_product_listlike;
    }
}
