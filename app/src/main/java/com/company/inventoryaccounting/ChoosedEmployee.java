package com.company.inventoryaccounting;

import java.util.Comparator;

public class ChoosedEmployee {
    String employeeID, employeeName, employeePosition, employeeTeam, employeePhone;
    // Constructor
    public ChoosedEmployee(String employeeID, String employeeName,  String employeePhone, String employeePosition, String employeeTeam) {
        this.employeeID = employeeID;
        this.employeeName = employeeName;
        this.employeePhone = employeePhone;
        this.employeePosition = employeePosition;
        this.employeeTeam = employeeTeam;
    }
}


class SortStaffById implements Comparator<ChoosedEmployee>
{
    public int compare(ChoosedEmployee a, ChoosedEmployee b)// Used for sorting in ascending order of id number
    {
        return Integer.parseInt(a.employeeID) - Integer.parseInt(b.employeeID);
    }
}

class SortStaffByName implements Comparator<ChoosedEmployee>
{
    public int compare(ChoosedEmployee a, ChoosedEmployee b)// Used for sorting in ascending order of toolName
    {
        return a.employeeName.compareTo(b.employeeName);
    }
}