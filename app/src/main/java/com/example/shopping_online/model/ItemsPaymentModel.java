package com.example.shopping_online.model;

import java.io.Serializable;

public class ItemsPaymentModel implements Serializable {
    String id, color_product_items_history, describe_product_items_history, image_product_items_history, name_product_items_history, size_product_items_history;
    double price_product_items_history, rating_product_items_history;
    int quantity_product_items_history;

    public ItemsPaymentModel() {
    }

    public ItemsPaymentModel(String color_product_items_history, String describe_product_items_history, String id, String image_product_items_history, String name_product_items_history, double price_product_items_history, int quantity_product_items_history, double rating_product_items_history, String size_product_items_history) {
        this.color_product_items_history = color_product_items_history;
        this.describe_product_items_history = describe_product_items_history;
        this.id = id;
        this.image_product_items_history = image_product_items_history;
        this.name_product_items_history = name_product_items_history;
        this.price_product_items_history = price_product_items_history;
        this.quantity_product_items_history = quantity_product_items_history;
        this.rating_product_items_history = rating_product_items_history;
        this.size_product_items_history = size_product_items_history;
    }

    public String getColor_product_items_history() {
        return color_product_items_history;
    }

    public void setColor_product_items_history(String color_product_items_history) {
        this.color_product_items_history = color_product_items_history;
    }

    public String getDescribe_product_items_history() {
        return describe_product_items_history;
    }

    public void setDescribe_product_items_history(String describe_product_items_history) {
        this.describe_product_items_history = describe_product_items_history;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage_product_items_history() {
        return image_product_items_history;
    }

    public void setImage_product_items_history(String image_product_items_history) {
        this.image_product_items_history = image_product_items_history;
    }

    public String getName_product_items_history() {
        return name_product_items_history;
    }

    public void setName_product_items_history(String name_product_items_history) {
        this.name_product_items_history = name_product_items_history;
    }

    public double getPrice_product_items_history() {
        return price_product_items_history;
    }

    public void setPrice_product_items_history(double price_product_items_history) {
        this.price_product_items_history = price_product_items_history;
    }

    public int getQuantity_product_items_history() {
        return quantity_product_items_history;
    }

    public void setQuantity_product_items_history(int quantity_product_items_history) {
        this.quantity_product_items_history = quantity_product_items_history;
    }

    public double getRating_product_items_history() {
        return rating_product_items_history;
    }

    public void setRating_product_items_history(double rating_product_items_history) {
        this.rating_product_items_history = rating_product_items_history;
    }

    public String getSize_product_items_history() {
        return size_product_items_history;
    }

    public void setSize_product_items_history(String size_product_items_history) {
        this.size_product_items_history = size_product_items_history;
    }
}
