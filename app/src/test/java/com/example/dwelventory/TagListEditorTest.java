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

        // Add Some New Tags to the Second List and see if the list contains them when Added.
        Tag mockTag1 = new Tag("Knife");
        Tag mockTag2 = new Tag("Spoon");
        Tag mockTag3 = new Tag("Fork");

        tagList2.add(mockTag1);
        tagList2.add(mockTag2);
        tagList2.add(mockTag3);

        editor.checkSingleItemTagAddition(tagList,tagList2);

        // Check if the tagList contains all 3 new mock tags.
        assertEquals(true,tagList.contains(mockTag1));
        assertEquals(true,tagList.contains(mockTag2));
        assertEquals(true,tagList.contains(mockTag3));

        // check size has increased
        assertEquals(4,tagList.size());

        // Next test is to try and remove the tags from the item by 'deselecting them'
        tagList2.remove(mockTag1);

        editor.checkSingleItemTagAddition(tagList,tagList2);
        assertEquals(3,tagList.size());
        assertEquals(false,tagList.contains(mockTag1));

        // Removing all items from the tag list
        editor.checkSingleItemTagAddition(tagList, new ArrayList<Tag>());
        assertEquals(0,tagList.size());

        // Check That Adding a 1 additional item while keeping the same item holds true
        tagList2 = mockTagList();
        tagList2.add(mockTag1);
        tagList = mockTagList();
        editor.checkSingleItemTagAddition(tagList,tagList2);

        assertEquals(2,tagList.size());
        assertEquals("Bathroom",tagList.get(0).getTagName());
        assertEquals("Knife",tagList.get(1).getTagName());
        assertEquals(true,tagList.contains(mockTag1));
    }

    @Test
    public void testCheckMultipleItemTagAddition(){
        // Create the mocks for the tests
        TagListEditor editor = mockTagListEditor();
        ArrayList<Tag> tagList1 = mockTagList();
        ArrayList<Tag> tagList2 = mockTagList();

        // Test to see if no tag is added since both lists are the same
        // Both contain "Bathroom"
        editor.checkMultipleItemTagAddition(tagList1,tagList2);
        assertEquals(1,tagList1.size());
        assertEquals("Bathroom",tagList1.get(0).getTagName());

        // Check if having unique tags in both lists doesnt remove the tag
        // from the first list, but rather adds the tag from the second
        tagList2.remove(0);
        tagList2.add(mockTag2());

        editor.checkMultipleItemTagAddition(tagList1,tagList2);
        assertEquals(2,tagList1.size());
        assertEquals("Bathroom",tagList1.get(0).getTagName());
        assertEquals("Kitchen",tagList1.get(1).getTagName());

        // Check that the list contains the exact same object reference.
        Tag mockTag1 = new Tag("Mouth");
        tagList2.clear();
        tagList2.add(mockTag1);

        editor.checkMultipleItemTagAddition(tagList1,tagList2);
        assertEquals(true,tagList1.contains(mockTag1));

        // Assert that tags cant be cleared.
        editor.checkMultipleItemTagAddition(tagList1, new ArrayList<Tag>());
        assertEquals(3,tagList1.size());

    }
}
