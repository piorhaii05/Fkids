package com.example.shopping_online;

public class ClothesModel {

    String _id, name, date, brand, image;
    int price;

    public ClothesModel(String _id, String brand, String date, String image, String name, int price) {
        this._id = _id;
        this.brand = brand;
        this.date = date;
        this.image = image;
        this.name = name;
        this.price = price;
    }

    public ClothesModel() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
