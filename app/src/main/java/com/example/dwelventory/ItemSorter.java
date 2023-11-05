package com.example.dwelventory;

import java.util.*;

public class ItemSorter {

    /**
     * This method will sort an ArrayList of Items in place by their dates
     * @param itemList
     *     The list of Items to be sorted
     * @param reverseOrder
     *     If set to True will sort in reverse order
     */
    static void sortDate(ArrayList<Item> itemList, boolean reverseOrder) {
        if (reverseOrder) {
            Collections.sort(itemList, Comparator.comparing(Item::getDate).reversed());
        }
        else {
            Collections.sort(itemList, Comparator.comparing(Item::getDate));
        }
    }

    /**
     * This method will sort an ArrayList of Items in place by their descriptions
     * @param itemList
     *     The list of Items to be sorted
     * @param reverseOrder
     *     If set to True will sort in reverse order
     */
    static void sortDescription(ArrayList<Item> itemList, boolean reverseOrder) {
        if (reverseOrder) {
            Collections.sort(itemList, Comparator.comparing(Item::getDescription).reversed());
        }
        else {
            Collections.sort(itemList, Comparator.comparing(Item::getDescription));
        }
    }

    /**
     * This method will sort an ArrayList of Items in place by their make
     * @param itemList
     *     The list of Items to be sorted
     * @param reverseOrder
     *     If set to True will sort in reverse order
     */
    static void sortMake(ArrayList<Item> itemList, boolean reverseOrder) {
        if (reverseOrder) {
            Collections.sort(itemList, Comparator.comparing(Item::getMake).reversed());
        }
        else {
            Collections.sort(itemList, Comparator.comparing(Item::getMake));
        }
    }

    /**
     * This method will sort an ArrayList of Items in place by estimated value
     * @param itemList
     * @param reverseOrder
     */
    static void sortEstValue(ArrayList<Item> itemList, boolean reverseOrder) {
        if (reverseOrder) {
            Collections.sort(itemList, Comparator.comparing(Item::getEstValue).reversed());
        }
        else {
            Collections.sort(itemList, Comparator.comparing(Item::getEstValue));
        }
    }
}
