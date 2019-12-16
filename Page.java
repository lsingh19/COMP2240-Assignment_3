/**
 * File Name: Page.java
 * Course: COMP2240 - Operating Systems
 * Assessment: Assignment 3
 */

public class Page
{
    private int id;         // a variable to store page number
    private int useBit;     // a variable used by the clock algorithm to check if the page was used

    /*
        Purpose: a constructor for the Page class
        Pre-Condition: a valid input are provided to the function
        Post-Condition: an instance of Page is created with the provided inputs
     */
    public Page(int id)
    {
        this.id = id;
        useBit = 0;
    }

    /*
        Purpose: to return the id value of the page object
        Pre-Condition: an instance of Page exists
        Post-Condition: the value of id is returned
     */
    public int getId()
    {
        return id;
    }

    /*
        Purpose: to return the useBit value of the page object
        Pre-Condition: an instance of Page exists
        Post-Condition: the value of useBit is returned
     */
    public int getUseBit()
    {
        return useBit;
    }

    /*
        Purpose: to set the useBit variable of the page object
        Pre-Condition: an instance of Page exists and a valid input is provided
        Post-Condition: the useBit variable of the page object is set to the input parameter
     */
    public void setUseBit(int useBit)
    {
        this.useBit = useBit;
    }
}
