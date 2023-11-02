package com.example.dwelventory;

import java.io.Serializable;

public class Tag implements Serializable {
    String tagName;

    public Tag(String tagName){
        this.tagName = tagName;
    }

    public String getTagName(){
        return tagName;
    }
}
