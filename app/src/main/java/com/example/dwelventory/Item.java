package com.example.dwelventory;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
/**
 * An object class that stores the user-defined information
 * of the household item
 * @author Maggie Lacson
 * */
public class Item implements Parcelable {
    private String description;
    private Date date;
    private String make;
    private String model;
    // serial number optional
//    private int serialNumber;
    private BigInteger serialNumber;
    private int estValue;
    // comment optional ??? ask ta
    private String comment;
    // optional
    private List photos;

    private ArrayList<Tag> tags;

    private boolean selected;
    UUID itemRefID;
            //= UUID.randomUUID();

/**
 * The base constructor of only the required attributes
 * @param description (String) the string name of the item
 * @param date (Date) a date object in the form of "MM-dd-yyyy"
 * @param make (String) the string make of the item
 * @param model (String) the string model of the item
 * @param estValue (int) an integer representing the estimated value of the item
 * */
    // base constructor
    public Item(String description, Date date, String make, String model, int estValue ) {
        this.description = description;
        this.date = date;
        this.make = make;
        this.model = model;
        this.estValue = estValue;
//        this.selected = false;
    }
    public Item(String description, Date date, String make, String model, BigInteger serial, int estValue, String comment ) {
        this.description = description;
        this.date = date;
        this.make = make;
        this.model = model;
        this.serialNumber = serial;
        this.estValue = estValue;
        this.comment = comment;
//        this.selected = false;
    }
    /**
     * The full constructor of all the Item attributes the user can access in the AddEdit Activity
     * Does not include tags as a parameter, tags are set after initialization
     * @param description (String) the string name of the item
     * @param date (Date) a date object in the form of "MM-dd-yyyy"
     * @param make (String) the string make of the item
     * @param model (String) the string model of the item
     * @param serialNumber (int) an integer representing the item's serial number
     * @param estValue (int) an integer representing the estimated value of the item
     * @param comment (String) a string comment of the item
     * @param photos (List) a list containing the photos associated with the item
     * */
    // full constructor
    public Item(String description, Date date, String make, String model, BigInteger serialNumber, int estValue, String comment, List photos) {
        this.description = description;
        this.date = date;
        this.make = make;
        this.model = model;
        this.serialNumber = serialNumber;
        this.estValue = estValue;
        this.comment = comment;
        this.photos = photos;
    }
    /**
     * The parceable constructor required from the parceable interface
     * @param in (Parcel) the parcel object that contains the information for the constructor
     * @see Parcelable
     * */
    // parcelable constructor
    protected Item(Parcel in) {
        description = in.readString();
        make = in.readString();
        model = in.readString();
        try {
            serialNumber = new BigInteger(in.readString());
        } catch (Exception e){
            Log.d("Item Class", "fuck "+e);
        }
        estValue = in.readInt();
        comment = in.readString();
        selected = in.readByte() != 0;
    }
    /**
     * The interface that generates the items from the parcel
     * @see Parcelable
     * */
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
    /**
     * Gets the item's description
     * @return description (String) the string name of the item
     * */
    public String getDescription() {
        return description;
    }
    /**
     * Sets the item's description
     * @param description (String) the string name of the item
     * */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * Gets the item's date
     * @return date (Date) the item's date of purchase
     * */
    public Date getDate() {
        return date;
    }
    /**
     * Sets the item's date
     * @param date (Date) the item's date of purchase
     * */
    public void setDate(Date date) {
        this.date = date;
    }
    /**
     * Gets the item's make
     * @return make (String) the string make of the item
     * */
    public String getMake() {
        return make;
    }
    /**
     * Sets the item's make
     * @param make (String) the string make of the item
     * */
    public void setMake(String make) {
        this.make = make;
    }
    /**
     * Gets the item's model
     * @return model (String) the string model of the item
     * */
    public String getModel() {
        return model;
    }
    /**
     * Sets the item's model
     * @param model (String) the string model of the item
     * */
    public void setModel(String model) {
        this.model = model;
    }
    /**
     * Gets the item's serial number
     * @return serialNumber (int) the int value of the serial number associated with the item
     * */
    public BigInteger getSerialNumber() {
        return serialNumber;
    }
    /**
     * Sets the item's serial number
     * @param serialNumber (int) the int value of the serial number associated with the item
     * */
    public void setSerialNumber(BigInteger serialNumber) {
        this.serialNumber = serialNumber;
    }
    /**
     * Gets the item's estimated value
     * @return estValue (int) the int value of the estimated cost of the item
     * */
    public int getEstValue() {
        return estValue;
    }
    /**
     * Sets the item's estimated value
     * @param estValue (int) the int value of the estimated cost of the item
     * */
    public void setEstValue(int estValue) {
        this.estValue = estValue;
    }
    /**
     * Gets the item's model
     * @return model (String) the string comment of the item
     * */
    public String getComment() {
        return comment;
    }
    /**
     * Sets the item's model
     * @param comment (String) the string comment of the item
     * */
    public void setComment(String comment) {
        this.comment = comment;
    }
    /**
     * Gets the item's photos
     * @return photos (List) the list of photos associated with the item
     * */
    public List getPhotos() {
        return photos;
    }
    /**
     * Sets the item's photos
     * @param photos (List) the list of photos associated with the item
     * */
    public void setPhotos(List photos) {
        this.photos = photos;
    }
    /**
     * Sets the itemRefId to be used for firebase
     * This takes no parameters and generates a new UUID
     * */
    // for initializing new items
    public void setItemRefID() {
        this.itemRefID = UUID.randomUUID();
    }
    /**
     * Sets the itemRefId to be used for firebase. This is for when an item has
     * been created and then edited. A new item object must be created and it's
     * itemRefID must be set to its original one.
     * @param itemRefID (UUID) a UUID representing the itemRefID
     * */
    // for editing items
    public void setItemRefID(UUID itemRefID) {
        this.itemRefID = itemRefID;
    }
    /**
     * Gets the itemRefId to be used for firebase
     * @return itemRefID (UUId) a UUID representing the itemRefID
     * */
    public UUID getItemRefID() {
        return itemRefID;
    }
    /**
     * Sets the tags for the items
     * @param tags (ArrayList<Tag>) an arraylist of tag objects that are associated with the item
     * @see Tag object class
     * */
    public void setTags(ArrayList<Tag> tags){
        this.tags = tags;
    }
    /**
     * Gets the tags for the items
     * @return tags (ArrayList<Tag>) an arraylist of tag objects that are associated with the item
     * @see Tag object class
     * */
    public ArrayList<Tag> getTags(){
        return this.tags;
    }
    /**
     * As required from Parceable
     * @return 0 (int)
     * @see Parcelable
     */
    @Override
    public int describeContents() {
        return 0;
    }
    /**
     * Takes the item objects and writes it to parcel
     * @param dest (Parcel)
     * @param flags (int)
     * @see Parcelable
     * */
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(make);
        dest.writeString(model);
        dest.writeString(serialNumber.toString());
        dest.writeInt(estValue);
        dest.writeString(comment);
        dest.writeByte((byte) (selected ? 1 : 0));
    }
    /**
     * Checks whether the item is selected
     * @return selected (boolean) describes whether or not the item has been selected
     * */
    public boolean isSelected() {
        return selected;
    }
    /**
     * Sets the item to be selected or not
     * @param selected (boolean) describes whether or not the item has been selected
     * */
    public void setSelected(boolean selected) {
       this.selected = selected;

    }
    /**
     * This creates a hashmap of the item. To be used for firebase.
     * @return map (Map<String>) a hashmap of the item
     * */
    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("description", description);
        map.put("date", date);
        map.put("make", make);
        map.put("model", model);
        map.put("serialNumber", serialNumber);
        map.put("estValue", estValue);
        map.put("comment", comment);
        map.put("tags", makeStringTagList(tags));
        return map;
    }

    /**
     * This creates an arraylist of strings of tag names from an
     * arraylist of tag objects
     * @param tags (ArrayList<Tags>) an arraylist of tag objects
     * @return stringTags (ArrayList<String>) an arraylist of strings of the tag objects names
     * */
    private ArrayList<String> makeStringTagList(ArrayList<Tag> tags){
        ArrayList<String> stringTags = new ArrayList<>();
        for (int i =0; i < tags.size();i++){
            stringTags.add(tags.get(i).getTagName());
        }
        return stringTags;
    }

}
