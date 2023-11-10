package com.example.dwelventory;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;


/***
 * A specified object to depict a defining characteristic for a specific Item. Can be associated with
 * multiple Items and an Item can have many Tag objects. (Basically acts as a descriptor//identifier//classifier)
 * @Author
 *      Ethan Keys
 */
public class Tag implements Parcelable {
    String tagName;

    /***
     * Constructor to set the name of the currently created Tag object
     * @param tagName
     *      String, representing the name of the Tag we want associated with the Object
     */
    public Tag(String tagName){
        this.tagName = tagName;
    }

    protected Tag(Parcel in) {
        tagName = in.readString();
    }

    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    /***
     * Get the specified Tag Name field of the object
     * @return
     *      String representing the tag name associated to the Tag object.
     */
    public String getTagName(){
        return tagName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(tagName);
    }
}
