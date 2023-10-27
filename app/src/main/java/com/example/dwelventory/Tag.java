package com.example.dwelventory;

public class Tag {
    String tagName;

    public void Tag(String tagName){
        this.tagName = tagName;
    }

    public String getTagName(){
        return tagName;
    }

    public void setTagName(String newName){
        tagName = newName;
    }
}
