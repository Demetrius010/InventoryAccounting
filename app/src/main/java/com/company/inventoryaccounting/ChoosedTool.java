package com.company.inventoryaccounting;

import java.util.Comparator;

public class ChoosedTool {// A class to represent a ChoosedTool
    String toolData, toolName, toolID;
    // Constructor
    public ChoosedTool(String toolData, String toolName, String toolID) {
        this.toolData = toolData;
        this.toolName = toolName;
        this.toolID = toolID;
    }

    // Used to print chosedData
    /*public String toString(){
        return this.toolData + " " + this.toolName + " " + this.toolID;
    }*/
}

class SortById implements Comparator<ChoosedTool>
{
    public int compare(ChoosedTool a, ChoosedTool b)// Used for sorting in ascending order of id number
    {
        return Integer.parseInt(a.toolID) - Integer.parseInt(b.toolID);
    }
}

class SortByName implements Comparator<ChoosedTool>
{
    public int compare(ChoosedTool a, ChoosedTool b)// Used for sorting in ascending order of toolName
    {
        return a.toolName.compareTo(b.toolName);
    }
}