package com.example.myapplication.models;

public class Product {
    private String product_code;
    private String product_name, product_price, product_description;
    private String ImageUri;

    public Product(String product_code, String product_name, String product_price, String product_description, String imageUri) {
        this.product_code = product_code;
        this.product_name = product_name;
        this.product_price = product_price;
        this.product_description = product_description;
        ImageUri = imageUri;
    }

    public Product() {
        // Required for Firebase
    }

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    public String getImageUri() {
        return ImageUri;
    }

    public void setImageUri(String imageUri) {
        ImageUri = imageUri;
    }

}
