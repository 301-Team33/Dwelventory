package com.example.dwelventory;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ItemSorterTest {

    private ArrayList<Item> itemList;
    private ArrayList<Item> mockItemList() {
        ArrayList<Item> list = new ArrayList<>();
        list.add(mockItem());
        return list;
    }

    private Item mockItem() {
        Calendar cal = Calendar.getInstance();
        cal.set(2023, 10, 31);
        Date date = cal.getTime();
        return new Item("Imaginary Object", date, "Non-existent", "Fictitious", 5);
    }

    @Before
    public void setUp() throws Exception {
        itemList = mockItemList();
    }

    @Test
    public void testSortDate() {
        // set up data
        Item mockItem = itemList.get(0);

        Calendar cal1 = Calendar.getInstance();
        cal1.set(2023, 10, 20);
        Date date1 = cal1.getTime();
        Item item1 = new Item("Real Object", date1, "Real", "Material", 0);
        itemList.add(item1);

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2023, 11, 1);
        Date date2 = cal2.getTime();
        Item item2 = new Item("Bucket", date2, "Amazon", "Metallic", 200);
        itemList.add(item2);

        Calendar cal3 = Calendar.getInstance();
        cal3.set(2022, 10, 31);
        Date date3 = cal3.getTime();
        Item item3 = new Item("Bottle of Water", date3, "Dasani", "Purified Water", 30);
        itemList.add(item3);

        ItemSorter.sortDate(itemList, false);

        assertEquals(item3, itemList.get(0));
        assertEquals(item1, itemList.get(1));
        assertEquals(mockItem, itemList.get(2));
        assertEquals(item2, itemList.get(3));
    }

    @Test
    public void testSortDateReverseOrder() {
        // set up data
        Item mockItem = itemList.get(0);

        Calendar cal1 = Calendar.getInstance();
        cal1.set(2023, 10, 20);
        Date date1 = cal1.getTime();
        Item item1 = new Item("Real Object", date1, "Real", "Material", 0);
        itemList.add(item1);

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2023, 11, 5);
        Date date2 = cal2.getTime();
        Item item2 = new Item("Bucket", date2, "Amazon", "Metallic", 200);
        itemList.add(item2);

        Calendar cal3 = Calendar.getInstance();
        cal3.set(2022, 10, 31);
        Date date3 = cal3.getTime();
        Item item3 = new Item("Bottle of Water", date3, "Dasani", "Purified Water", 30);
        itemList.add(item3);

        ItemSorter.sortDate(itemList, true);

        assertEquals(item2, itemList.get(0));
        assertEquals(mockItem, itemList.get(1));
        assertEquals(item1, itemList.get(2));
        assertEquals(item3, itemList.get(3));
    }

    @Test
    public void testSortDescription() {
        // set up data
        Item mockItem = itemList.get(0);

        Calendar cal1 = Calendar.getInstance();
        cal1.set(2023, 10, 20);
        Date date1 = cal1.getTime();
        Item item1 = new Item("Real Object", date1, "Real", "Material", 0);
        itemList.add(item1);

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2023, 11, 1);
        Date date2 = cal2.getTime();
        Item item2 = new Item("Bucket", date2, "Amazon", "Metallic", 200);
        itemList.add(item2);

        Calendar cal3 = Calendar.getInstance();
        cal3.set(2022, 10, 31);
        Date date3 = cal3.getTime();
        Item item3 = new Item("Bottle of Water", date3, "Dasani", "Purified Water", 30);
        itemList.add(item3);

        ItemSorter.sortDescription(itemList, false);

        assertEquals(item3, itemList.get(0));
        assertEquals(item2, itemList.get(1));
        assertEquals(mockItem, itemList.get(2));
        assertEquals(item1, itemList.get(3));
    }

    @Test
    public void testSortDescriptionReversedOrder() {
        // set up data
        Item mockItem = itemList.get(0);

        Calendar cal1 = Calendar.getInstance();
        cal1.set(2023, 10, 20);
        Date date1 = cal1.getTime();
        Item item1 = new Item("Real Object", date1, "Real", "Material", 0);
        itemList.add(item1);

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2023, 11, 1);
        Date date2 = cal2.getTime();
        Item item2 = new Item("Bucket", date2, "Amazon", "Metallic", 200);
        itemList.add(item2);

        Calendar cal3 = Calendar.getInstance();
        cal3.set(2022, 10, 31);
        Date date3 = cal3.getTime();
        Item item3 = new Item("Bottle of Water", date3, "Dasani", "Purified Water", 30);
        itemList.add(item3);

        ItemSorter.sortDescription(itemList, true);

        assertEquals(item1, itemList.get(0));
        assertEquals(mockItem, itemList.get(1));
        assertEquals(item2, itemList.get(2));
        assertEquals(item3, itemList.get(3));
    }

    @Test
    public void testSortMake() {
        // set up data
        Item mockItem = itemList.get(0);

        Calendar cal1 = Calendar.getInstance();
        cal1.set(2023, 10, 20);
        Date date1 = cal1.getTime();
        Item item1 = new Item("Real Object", date1, "Real", "Material", 0);
        itemList.add(item1);

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2023, 11, 1);
        Date date2 = cal2.getTime();
        Item item2 = new Item("Bucket", date2, "Amazon", "Metallic", 200);
        itemList.add(item2);

        Calendar cal3 = Calendar.getInstance();
        cal3.set(2022, 10, 31);
        Date date3 = cal3.getTime();
        Item item3 = new Item("Bottle of Water", date3, "Dasani", "Purified Water", 30);
        itemList.add(item3);

        ItemSorter.sortMake(itemList, false);

        assertEquals(item2, itemList.get(0));
        assertEquals(item3, itemList.get(1));
        assertEquals(mockItem, itemList.get(2));
        assertEquals(item1, itemList.get(3));
    }

    @Test
    public void testSortMakeReverseOrder() {
        // set up data
        Item mockItem = itemList.get(0);

        Calendar cal1 = Calendar.getInstance();
        cal1.set(2023, 10, 20);
        Date date1 = cal1.getTime();
        Item item1 = new Item("Real Object", date1, "Real", "Material", 0);
        itemList.add(item1);

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2023, 11, 1);
        Date date2 = cal2.getTime();
        Item item2 = new Item("Bucket", date2, "Amazon", "Metallic", 200);
        itemList.add(item2);

        Calendar cal3 = Calendar.getInstance();
        cal3.set(2022, 10, 31);
        Date date3 = cal3.getTime();
        Item item3 = new Item("Bottle of Water", date3, "Dasani", "Purified Water", 30);
        itemList.add(item3);

        ItemSorter.sortMake(itemList, true);

        assertEquals(item1, itemList.get(0));
        assertEquals(mockItem, itemList.get(1));
        assertEquals(item3, itemList.get(2));
        assertEquals(item2, itemList.get(3));
    }

    @Test
    public void testSortEstValue() {
        // set up data
        Item mockItem = itemList.get(0);

        Calendar cal1 = Calendar.getInstance();
        cal1.set(2023, 10, 20);
        Date date1 = cal1.getTime();
        Item item1 = new Item("Real Object", date1, "Real", "Material", 0);
        itemList.add(item1);

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2023, 11, 1);
        Date date2 = cal2.getTime();
        Item item2 = new Item("Bucket", date2, "Amazon", "Metallic", 200);
        itemList.add(item2);

        Calendar cal3 = Calendar.getInstance();
        cal3.set(2022, 10, 31);
        Date date3 = cal3.getTime();
        Item item3 = new Item("Bottle of Water", date3, "Dasani", "Purified Water", 30);
        itemList.add(item3);

        ItemSorter.sortEstValue(itemList, false);

        assertEquals(item1, itemList.get(0));
        assertEquals(mockItem, itemList.get(1));
        assertEquals(item3, itemList.get(2));
        assertEquals(item2, itemList.get(3));
    }

    @Test
    public void testSortEstValueReverseOrder() {
        // set up data
        Item mockItem = itemList.get(0);

        Calendar cal1 = Calendar.getInstance();
        cal1.set(2023, 10, 20);
        Date date1 = cal1.getTime();
        Item item1 = new Item("Real Object", date1, "Real", "Material", 0);
        itemList.add(item1);

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2023, 11, 1);
        Date date2 = cal2.getTime();
        Item item2 = new Item("Bucket", date2, "Amazon", "Metallic", 200);
        itemList.add(item2);

        Calendar cal3 = Calendar.getInstance();
        cal3.set(2022, 10, 31);
        Date date3 = cal3.getTime();
        Item item3 = new Item("Bottle of Water", date3, "Dasani", "Purified Water", 30);
        itemList.add(item3);

        ItemSorter.sortEstValue(itemList, true);

        assertEquals(item2, itemList.get(0));
        assertEquals(item3, itemList.get(1));
        assertEquals(mockItem, itemList.get(2));
        assertEquals(item1, itemList.get(3));
    }

}
