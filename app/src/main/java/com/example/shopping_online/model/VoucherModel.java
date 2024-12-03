package com.example.shopping_online.model;

import java.io.Serializable;

public class VoucherModel implements Serializable {
    private String id, date_voucher, image_voucher, title_voucher;
    private boolean status_voucher;
    private double value_voucher;

    public VoucherModel() {
    }

    public VoucherModel(String date_voucher, String id, String image_voucher, boolean status_voucher, String title_voucher, double value_voucher) {
        this.date_voucher = date_voucher;
        this.id = id;
        this.image_voucher = image_voucher;
        this.status_voucher = status_voucher;
        this.title_voucher = title_voucher;
        this.value_voucher = value_voucher;
    }

    public double getValue_voucher() {
        return value_voucher;
    }

    public void setValue_voucher(double value_voucher) {
        this.value_voucher = value_voucher;
    }

    public String getTitle_voucher() {
        return title_voucher;
    }

    public void setTitle_voucher(String title_voucher) {
        this.title_voucher = title_voucher;
    }

    public boolean isStatus_voucher() {
        return status_voucher;
    }

    public void setStatus_voucher(boolean status_voucher) {
        this.status_voucher = status_voucher;
    }

    public String getImage_voucher() {
        return image_voucher;
    }

    public void setImage_voucher(String image_voucher) {
        this.image_voucher = image_voucher;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate_voucher() {
        return date_voucher;
    }

    public void setDate_voucher(String date_voucher) {
        this.date_voucher = date_voucher;
    }
}
