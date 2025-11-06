package com.example.firebaseapp.models;

public class Club {
    private String name;
    private String description;
    private String imageName;
    private String bannerUrl;

    public Club(String name, String description, String imageName, String bannerUrl) {
        this.name = name;
        this.description = description;
        this.imageName = imageName;
        this.bannerUrl = bannerUrl;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageName() {
        return imageName;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }
}
