package com.example.vuhung.video10minutes.Model;

public class Child {
    private int id;
    private String name;
    private String photo;

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Child() {
    }

    public Child(String name, String photo) {
        this.name = name;
        this.photo = photo;
    }

    public Child(int id, String name, String photo) {
        this.id = id;
        this.name = name;
        this.photo = photo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
