package com.example.dwelventory;

import android.util.Log;

import org.checkerframework.common.aliasing.qual.Unique;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/***
 * A list editor for Tag ArrayLists that only update array lists based on tags that need to be
 * unspecified from an item, or new tags that need to be added to an item from an array list. This class
 * also handles the deletion of tags from a user database of specified tags and removes such tags from
 * all items containing it.
 * @Author
 *      Ethan Keys
 * @see
 *      Item, Tag
 */
public class TagListEditor {
    public TagListEditor(){};

    /***
     * A method to edit a current array list of tags associated with an Item after a tag has been deleted
     * from the set of user defined tags as stored in the database.
     * @param currentTags
     *      An ArrayList of Tags specifying the set of Tags currently associated with the item.
     * @param tagToDelete
     *      A Tag representing the Tag that needs to be deleted from the ArrayList of Tags for a specified
     *      Item if the Item contains the Tag.
     */
    public void checkDeletion(ArrayList<Tag> currentTags, Tag tagToDelete) {
        Iterator<Tag> tagIterator = currentTags.iterator();
        while (tagIterator.hasNext()){
            if (tagIterator.next().getTagName().equals(tagToDelete.getTagName())){
                tagIterator.remove();
                return;
            }
        }
    }

    /***
     * Adds unique Tags to a set of items in which Tags that are not specified in the current Item being applied
     * Tags to, it wont be readded.
     * @param currentTags
     *      An ArrayList of Tags representing the current Tags already associated with the Item
     * @param tagsToAdd
     *      An ArrayList of Tags representing the set of Tags we want to mass add to the Item
     * @return
     *      An ArrayList of Tags representing the updated Tag array list for the current Item.
     */
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

    /***
     * Adds a set of Tags to a single Item. If an Item has a current Tag that is not specified in the
     * ArrayList of Tags to be added, then it it removed. Otherwise the Tag is either added if it
     * does not contain it, or kept the same (not removed) if it is already specified.
     * @param currentTags
     *      The ArrayList of current Tags associated with the specified Item
     * @param tagsToAdd
     *      The ArrayList of Tags to update the set of Tags associated to an Item to.
     */
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


