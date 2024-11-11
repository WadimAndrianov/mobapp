package com.example.myapplication.models;

public class DeliveryForItem {
    String deliveryAddress;

    String deliveryNumber;

    public DeliveryForItem(){

    }

    public DeliveryForItem(String deliveryAddress, String deliveryNumber) {
        this.deliveryAddress = deliveryAddress;
        this.deliveryNumber = deliveryNumber;
    }

    public String getDeliveryNumber() {
        return deliveryNumber;
    }

    public void setDeliveryNumber(String deliveryNumber) {
        this.deliveryNumber = deliveryNumber;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

}
