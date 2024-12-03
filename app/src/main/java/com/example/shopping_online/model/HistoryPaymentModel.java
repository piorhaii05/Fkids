package com.example.shopping_online.model;

public class HistoryPaymentModel {
    String id, date_history, infor_payment_history;
    double price_history;

    public HistoryPaymentModel() {
    }

    public HistoryPaymentModel(String date_history, String id, String infor_payment_history, double price_history) {
        this.date_history = date_history;
        this.id = id;
        this.infor_payment_history = infor_payment_history;
        this.price_history = price_history;
    }

    public String getDate_history() {
        return date_history;
    }

    public void setDate_history(String date_history) {
        this.date_history = date_history;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInfor_payment_history() {
        return infor_payment_history;
    }

    public void setInfor_payment_history(String infor_payment_history) {
        this.infor_payment_history = infor_payment_history;
    }

    public double getPrice_history() {
        return price_history;
    }

    public void setPrice_history(double price_history) {
        this.price_history = price_history;
    }
}
