package com.example.dwelventory;

import android.util.Log;

import org.checkerframework.common.aliasing.qual.Unique;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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

    // Used only for adding to multiple items since we can't remove previously added on tags for
    // Multiple items
    public ArrayList<Tag> checkMultipleItemTagAddition(ArrayList<Tag> currentTags, ArrayList<Tag> tagsToAdd){
        // First check all tags. any tags that Are currently not in current tags need to be
        // added from tagsToAdd
//        if ((currentTags==null) || (currentTags.size()==0)){
//            currentTags = new ArrayList<>();
//            currentTags = tagsToAdd;
//        }
        ArrayList<Tag> uniqueAdditionTags = new ArrayList<>();
        HashSet<String> tagSet = new HashSet<>();
        for (Tag t : currentTags) {
            tagSet.add(t.getTagName());
            uniqueAdditionTags.add(t);
        }
        for (Tag addingTag : tagsToAdd) {
            if (!tagSet.contains(addingTag.getTagName())) {
                uniqueAdditionTags.add(addingTag);
            }

        }
        return uniqueAdditionTags;
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
            }
            else{
                tagIterator.remove();
            }
        }
    }
}


