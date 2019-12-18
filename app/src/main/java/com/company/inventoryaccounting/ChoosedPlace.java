package com.company.inventoryaccounting;

import java.util.Comparator;

public class ChoosedPlace {
    String addresID, shortAddresses, fullAddresses;
    // Constructor
    public ChoosedPlace(String addresID, String shortAddresses, String fullAddresses ) {
        this.shortAddresses = shortAddresses;
        this.fullAddresses = fullAddresses;
        this.addresID = addresID;
    }
}


class SortPlaceById implements Comparator<ChoosedPlace>
{
    public int compare(ChoosedPlace a, ChoosedPlace b)// Used for sorting in ascending order of id number
    {
        return Integer.parseInt(a.addresID) - Integer.parseInt(b.addresID);
    }
}

class SortPlaceByShortAdr implements Comparator<ChoosedPlace>
{
    public int compare(ChoosedPlace a, ChoosedPlace b)// Used for sorting in ascending order of toolName
    {
        return a.shortAddresses.compareTo(b.shortAddresses);
    }
}