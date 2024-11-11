package com.example.myapplication.models;

public class ProductInUserDelivery {
    private String delivery_address;
    private String product_name, product_price, product_description;
    private String ImageUri;
    private String quantity;
    private String UserUid;
    private String DeliveryNumber;
    private String product_code;

    public ProductInUserDelivery(String delivery_address, String product_name, String product_price, String product_description,
                                 String imageUri, String quantity, String userUid, String deliveryNumber, String product_code) {
        this.delivery_address = delivery_address;
        this.product_name = product_name;
        this.product_price = product_price;
        this.product_description = product_description;
        ImageUri = imageUri;
        this.quantity = quantity;
        UserUid = userUid;
        DeliveryNumber = deliveryNumber;
        this.product_code = product_code;
    }

    public ProductInUserDelivery(){

    }

    public String getDelivery_address() {
        return delivery_address;
    }

    public void setDelivery_address(String delivery_address) {
        this.delivery_address = delivery_address;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUserUid() {
        return UserUid;
    }

    public void setUserUid(String userUid) {
        UserUid = userUid;
    }

    public String getDeliveryNumber() {
        return DeliveryNumber;
    }

    public void setDeliveryNumber(String deliveryNumber) {
        DeliveryNumber = deliveryNumber;
    }

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }


}
