package com.example.shopping_online.model;

import java.io.Serializable;

public class CartModel implements Serializable {

    String id, describe_product_cart, image_product_cart, name_product_cart, color_product_cart, size_product_cart;
    int quantity_product_cart;
    double price_product_cart, rating_product_cart;
    boolean selected;

    public CartModel() {
    }

    public CartModel(String color_product_cart, String describe_product_cart, String id, String image_product_cart, String name_product_cart, double price_product_cart, int quantity_product_cart, double rating_product_cart, boolean selected, String size_product_cart) {
        this.color_product_cart = color_product_cart;
        this.describe_product_cart = describe_product_cart;
        this.id = id;
        this.image_product_cart = image_product_cart;
        this.name_product_cart = name_product_cart;
        this.price_product_cart = price_product_cart;
        this.quantity_product_cart = quantity_product_cart;
        this.rating_product_cart = rating_product_cart;
        this.selected = selected;
        this.size_product_cart = size_product_cart;
    }

    public String getColor_product_cart() {
        return color_product_cart;
    }

    public void setColor_product_cart(String color_product_cart) {
        this.color_product_cart = color_product_cart;
    }

    public String getSize_product_cart() {
        return size_product_cart;
    }

    public void setSize_product_cart(String size_product_cart) {
        this.size_product_cart = size_product_cart;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getDescribe_product_cart() {
        return describe_product_cart;
    }

    public void setDescribe_product_cart(String describe_product_cart) {
        this.describe_product_cart = describe_product_cart;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage_product_cart() {
        return image_product_cart;
    }

    public void setImage_product_cart(String image_product_cart) {
        this.image_product_cart = image_product_cart;
    }

    public String getName_product_cart() {
        return name_product_cart;
    }

    public void setName_product_cart(String name_product_cart) {
        this.name_product_cart = name_product_cart;
    }

    public double getPrice_product_cart() {
        return price_product_cart;
    }

    public void setPrice_product_cart(double price_product_cart) {
        this.price_product_cart = price_product_cart;
    }

    public int getQuantity_product_cart() {
        return quantity_product_cart;
    }

    public void setQuantity_product_cart(int quantity_product_cart) {
        this.quantity_product_cart = quantity_product_cart;
    }

    public double getRating_product_cart() {
        return rating_product_cart;
    }

    public void setRating_product_cart(double rating_product_cart) {
        this.rating_product_cart = rating_product_cart;
    }
}
