package com.islavdroid.blogapp;


public class Blog {
    private String tittle;
    private String desc;
    private String image;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;

    public Blog() {

    }

    public Blog(String tittle, String desc, String image,String username) {
        this.tittle = tittle;
        this.desc = desc;
        this.image = image;
        this.username =username;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
