package com.example.shopping_online.model;

import java.io.Serializable;

public class CardModel implements Serializable {
    String id, number_acount_card, name_acount_card, date_card, CVV_card, four_number_card;

    public CardModel() {
    }

    public CardModel(String CVV_card, String date_card, String four_number_card, String id, String name_acount_card, String number_acount_card) {
        this.CVV_card = CVV_card;
        this.date_card = date_card;
        this.four_number_card = four_number_card;
        this.id = id;
        this.name_acount_card = name_acount_card;
        this.number_acount_card = number_acount_card;
    }

    public String getFour_number_card() {
        return four_number_card;
    }

    public void setFour_number_card(String four_number_card) {
        this.four_number_card = four_number_card;
    }

    public String getCVV_card() {
        return CVV_card;
    }

    public void setCVV_card(String CVV_card) {
        this.CVV_card = CVV_card;
    }

    public String getDate_card() {
        return date_card;
    }

    public void setDate_card(String date_card) {
        this.date_card = date_card;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName_acount_card() {
        return name_acount_card;
    }

    public void setName_acount_card(String name_acount_card) {
        this.name_acount_card = name_acount_card;
    }

    public String getNumber_acount_card() {
        return number_acount_card;
    }

    public void setNumber_acount_card(String number_acount_card) {
        this.number_acount_card = number_acount_card;
    }
}
