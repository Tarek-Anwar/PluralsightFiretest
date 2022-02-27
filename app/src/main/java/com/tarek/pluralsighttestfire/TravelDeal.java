package com.tarek.pluralsighttestfire;

import java.io.Serializable;

public class TravelDeal implements Serializable {
    private String id;
    private String title;
    private String descrption;
    private String price;
    private String imageUrl;
    private String imageName;

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public TravelDeal(String title, String descrption, String price, String imageUrl , String imageName) {
        this.title = title;
        this.descrption = descrption;
        this.price = price;
        this.imageUrl = imageUrl;
        this.imageName = imageName;
    }
    public TravelDeal(){}
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescrption() {
        return descrption;
    }

    public void setDescrption(String descrption) {
        this.descrption = descrption;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
