package com.example.dwelventory;

import android.util.Log;

import org.checkerframework.common.aliasing.qual.Unique;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class TagListEditor {

    public TagListEditor(){};

    public void checkDeletion(ArrayList<Tag> currentTags, Tag tagToDelete) {
        Iterator<Tag> tagIterator = currentTags.iterator();
        while (tagIterator.hasNext()){
            if (tagIterator.next().getTagName().equals(tagToDelete.getTagName())){
                tagIterator.remove();
                return;
            }
        }
    }

    public void checkSingleItemTagAddition(ArrayList<Tag> currentTags, ArrayList<Tag> tagsToAdd) {

        // First check all tags. any tags that Are currently not in current tags need to be
        // added from tagsToAdd
        ArrayList<Tag> uniqueAdditionTags = new ArrayList<>();
        boolean needToAdd;
        for (Tag addingTag : tagsToAdd) {
            needToAdd = true;
            for (Tag checkingTag : currentTags) {
                if (checkingTag.getTagName().equals(addingTag.getTagName())) {
                    needToAdd = false;
                    break;
                }
            }
            if (needToAdd) uniqueAdditionTags.add(addingTag);
        }

        // add all the uniquely added tags
        for (Tag addTheseTags : uniqueAdditionTags) {
            currentTags.add(addTheseTags);
        }

        Log.d("", "checkSingleItemTagAddition: MY CURRENT APPLIED TAGS" + currentTags);
        Log.d("", "checkSingleItemTagAddition: MY CURRENT TAGS TO ADD" + tagsToAdd);
        // Next check all tags in the current tags to see if they are in the tags we want to add.
        // If they are not, we must remove them.
        Iterator<Tag> tagIterator = currentTags.iterator();
        HashMap<String,String> myStrings = new HashMap<>();
        ArrayList<Tag> uniqueRemainingTags = new ArrayList<>();
        Tag myTag;
        for (Tag myStringTags : tagsToAdd){
            myStrings.put(myStringTags.getTagName(),"1");
        }
        while(tagIterator.hasNext()){
            myTag = tagIterator.next();
            if(myStrings.containsKey(myTag.getTagName()) == true){
                Log.d("", "checkSingleItemTagAddition: ADDED TAQG HERE");
            }
            else{
                tagIterator.remove();
            }
        }
    }
}


