package com.islavdroid.blogapp;


public class Blog {
    private String tittle, desc, image;

    public Blog() {

    }

    public Blog(String tittle, String desc, String image) {
        this.tittle = tittle;
        this.desc = desc;
        this.image = image;
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
