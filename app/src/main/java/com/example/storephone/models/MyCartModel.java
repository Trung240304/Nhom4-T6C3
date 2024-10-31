package com.example.storephone.models;

public class MyCartModel {
    String Img_url;
    String productName;
    String productPrice;
    String currentDate;
    String currentTime;
    String totalQuantity;
    int totalPrice;
    String documentId;

    public MyCartModel() {

    }

    public MyCartModel(String documentId, int totalPrice, String totalQuantity, String currentTime, String currentDate, String productPrice, String productName, String img_url) {
        this.documentId = documentId;
        this.totalPrice = totalPrice;
        this.totalQuantity = totalQuantity;
        this.currentTime = currentTime;
        this.currentDate = currentDate;
        this.productPrice = productPrice;
        this.productName = productName;
        Img_url = img_url;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getImg_url() {
        return Img_url;
    }

    public void setImg_url(String productImageUrl) {
        this.Img_url = productImageUrl;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(String totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
