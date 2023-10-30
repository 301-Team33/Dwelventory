package com.example.dwelventory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Item {
    private String description;
    private Date date;
    private String make;
    private String model;
    // serial number optional
    private int serialNumber;
    private int estValue;
    // comment optional ??? ask ta
    private String comment;
    // optional
    private List photos;
    private ArrayList<Tag> tags;

    // base constructor
    public Item(String description, Date date, String make, String model, int estValue ) {
        this.description = description;
        this.date = date;
        this.make = make;
        this.model = model;
        this.estValue = estValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public int getEstValue() {
        return estValue;
    }

    public void setEstValue(int estValue) {
        this.estValue = estValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List getPhotos() {
        return photos;
    }

    public void setPhotos(List photos) {
        this.photos = photos;
    }

    public void setTags(ArrayList<Tag> tags){
        this.tags = tags;
    }
}
