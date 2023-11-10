package com.example.dwelventory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class ItemTest {
    private Item mockItem(){
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH);
        String date22 = "2-8-2023";
        Date date2;
        try {
            date2 = formatter.parse(date22);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return new Item("Jinora", date2, "Pygmy Goat", "Caramel w/ Black Markings",200);
    }
    @Test
    public void testGetDescription(){
        Item item = mockItem();
        assertEquals("Jinora", item.getDescription());
    }
    @Test
    public void testSetComment(){
        Item item = mockItem();
        item.setComment("Purchased at 8 weeks old");
        assertEquals("Purchased at 8 weeks old", item.getComment());
    }
    @Test
    public void testSetItemRefID(){
        Item item = mockItem();
        // no ID yet
        assertNull(item.getItemRefID());
        // generating new ID
        item.setItemRefID();
        assertNotNull(item.getItemRefID());
        // set ID from UUID for editing items
        UUID id = UUID.randomUUID();
        item.setItemRefID(id);
        assertEquals(id, item.getItemRefID());
    }


}
