package com.example.dwelventory;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.List;

public class Item implements Parcelable {
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

    // base constructor
    public Item(String description, Date date, String make, String model, int estValue ) {
        this.description = description;
        this.date = date;
        this.make = make;
        this.model = model;
        this.estValue = estValue;
    }

    // full constructor
    public Item(String description, Date date, String make, String model, int serialNumber, int estValue, String comment, List photos) {
        this.description = description;
        this.date = date;
        this.make = make;
        this.model = model;
        this.serialNumber = serialNumber;
        this.estValue = estValue;
        this.comment = comment;
        this.photos = photos;
    }

    // parcelable constructor
    protected Item(Parcel in) {
        description = in.readString();
        make = in.readString();
        model = in.readString();
        serialNumber = in.readInt();
        estValue = in.readInt();
        comment = in.readString();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(make);
        dest.writeString(model);
        dest.writeInt(serialNumber);
        dest.writeInt(estValue);
        dest.writeString(comment);
    }
}
