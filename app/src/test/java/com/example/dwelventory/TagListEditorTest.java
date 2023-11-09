package com.example.dwelventory;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;

public class TagListEditorTest {


    private TagListEditor mockTagListEditor(){
        return new TagListEditor();
    }

    private ArrayList<Tag> mockTagList(){
        ArrayList<Tag> tagList = new ArrayList<>();
        tagList.add(mockTag());
        return tagList;
    }

    private Tag mockTag(){
        return new Tag("Bathroom");
    }

    private Tag mockTag2(){
        return new Tag("Kitchen");
    }

    @Test
    public void testCheckDeletion(){
        // check if can remove the tag of the only tag in the list.
        TagListEditor editor = mockTagListEditor();
        ArrayList<Tag> tagListApplied = mockTagList();

        editor.checkDeletion(tagListApplied,mockTag());
        assertEquals(0,tagListApplied.size());

        // check if can pass a tag not in the tag list and size stays the same
        Tag newMockTag = mockTag2();
        ArrayList<Tag> mockTagListApplied = mockTagList();
        mockTagListApplied.add(newMockTag);

        editor.checkDeletion(mockTagListApplied,new Tag("Mattress"));
        assertEquals(2,mockTagListApplied.size());

        // Check looping through all items and removing all mock tags.
        editor.checkDeletion(mockTagListApplied,mockTag2());
        editor.checkDeletion(mockTagListApplied,mockTag());

        assertEquals(0,mockTagListApplied.size());
    }

    @Test
    public void testCheckSingleItemTagAddition(){

        TagListEditor editor = mockTagListEditor();
        ArrayList<Tag> tagList = mockTagList();
        ArrayList<Tag> tagList2 = mockTagList();

        // Test to see if passing the same items in both array lists results
        // In nothing being added to the first (ie) stays the same

        editor.checkSingleItemTagAddition(tagList,tagList2);
        assertEquals(1,tagList.size());

        // Check to see if the first Tag and only Tag is the mock tag name
        assertEquals("Bathroom",tagList.get(0).getTagName());


    }



}
