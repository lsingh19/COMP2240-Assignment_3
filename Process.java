/**
 * File Name: Process.java
 * Course: COMP2240 - Operating Systems
 * Assessment: Assignment 3
 */
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Process {
    private int pId;            // a variable to store the id of the process
    private String pName;       // a variable to store the name of the process (file name)

    private ArrayList<Integer> faultTimes;                              // a variable to store the fault times of the process
    private Queue<Integer> pRequests = new LinkedList<Integer>();       // a queue to store the page requests by the process
    private Queue<Integer> storedPageRequests = new LinkedList<>();     // a queue to store the page requests by the process (this is kinda like a backup page requests queue).
                                                                        // and is used when the process is reset.
    private ArrayList<Page> listOfPages = new ArrayList<Page>();   // an array list of pages that represents the pages in virtual memory for the process
    private ArrayList<Page> pageFrames = new ArrayList<Page>();    // an array list of pages that represents the page in main memory
                                                                // (Note that the size never goes over the set limit frame for the process in main memory)
    private int totalNumberOfFrames;    // a variable to store the total number of frames allocated to the process in main memory
    private boolean pageFault;          // a variable is used by the program to check if the process encountered a page fault on the previous run

    private LinkedList<Page> leastUsed = new LinkedList<Page>();        // a linked list of the recently used pages
                                                                    // (Note that the size never goes over the set limit frames for the process in main memory)
    private int blocked = 0;            // a variable that is used to store the time that the process is blocked for
    private boolean LRU = false;        // a boolean variable that is used to check if the LRU algorithm should be executed to replace a page in allocated main memory frames
    private int finishedTime = 0;       // a variable to store the finished time for the process
    private ArrayList<Page> clockPagesFrames = new ArrayList<Page>();       // an arraylist used by the process when using the clock algorithm
    private int clockPointer = 0;       // a variable to represent the clock pointer in the clock algorithm

    /*
        Purpose: a constructor for the Process class
        Pre-Condition: valid inputs are provided to the function
        Post-Condition: an instance of Process is created with the provided inputs
     */
    public Process(int pId, String pName, ArrayList<Integer> pRequests, int AllocatedFrames)
    {
        this.pId = pId;
        this.pName = pName;
        // going through the input page requests and adding them to both the pRequests and storedPageRequest variable
        for (int i = 0; i < pRequests.size(); i++)
        {
            this.pRequests.add(pRequests.get(i));
            storedPageRequests.add(pRequests.get(i));
        }

        faultTimes = new ArrayList<Integer>();  // initialising the faultTimes variable
        pageFault = false;      // setting the page fault boolean variable to false
        totalNumberOfFrames = AllocatedFrames;      // assigning the totalNumberOfFrames to the value of the AllocatedFrames input variable

        ArrayList<Integer> listOfJobs = new ArrayList<Integer>();       // creating a local variable to store the distinct page number in the page requests queue
        for (int i = 0; i < pRequests.size(); i++)
        {
            boolean found = false;      // a boolean used to check if the page number was found
            for (int z = 0; z < listOfJobs.size(); z++)
            {
                // checking to see if the page number already exists within the listOfJobs array list
                if (pRequests.get(i) == listOfJobs.get(z))
                {
                    found = true;
                    break;
                }
            }
            // check to see if the page was found within the listOfJobs
            if (!found)
            {
                // the page was not found wihtin the listOfJobs hence it is added to the listOfJobs
                listOfJobs.add(pRequests.get(i));
            }
        }

        if (listOfJobs.size() > 50)
        {
            System.out.println("Process " + pId + " has more pages than the maximum number of pages 50 allowed -- it has "+ listOfJobs.size());
            System.exit(0);
        }

        // a for loop to create a page for each page numbers in listOfJobs
        for (int i = 0; i < listOfJobs.size(); i++)
        {
            Page tempPage = new Page(listOfJobs.get(i));    // creating a page object using the listOfJobs
            listOfPages.add(tempPage);      // adding the page object to the listOfPages variable
        }
    }

    /*
        Purpose: return the name of the process
        Pre-Condition: an instance of Process exists
        Post-Condition: the name of the process object is returned
     */
    public String getpName()
    {
        return pName;
    }

    /*
        Purpose: to return the finished time for the process
        Pre-Condition: an instance of Process exists
        Post-Condition: the finished time of the process object is returned
     */
    public int getFinishedTime()
    {
        return finishedTime;
    }

    /*
        Purpose: to return the faultTimes array list
        Pre-Condition: an instance of Process exists
        Post-Condition: the faultTimes arraylist is returned
     */
    public ArrayList<Integer> getFaultTimes()

    {
        return faultTimes;
    }

    /*
        Purpose: return the id of the process object
        Pre-Condition: an instance of Process exists
        Post-Condition: the id of the process object is returned
     */
    public int getId()
    {
        return pId;
    }

    /*
        Purpose: set the blocked time for the process object
        Pre-Condition: an instance of Process exists and a valid input is provided
        Post-Condition: the blocked variable inside the process object is set to the input parameter
     */
    public void isBeingBlocked(int blocked)
    {
        this.blocked = blocked;
    }

    /*
        Purpose: decrease the blocked variable inside the process object
        Pre-Condition: an instance of Process exists
        Post-Condition: the blocked variable inside the process object is decreased
     */
    public void decreaseBlockedTime()
    {
        blocked--;
    }

    /*
        Purpose: to return the blocked time for the process
        Pre-Condition: an instance of Process exists
        Post-Condition: the value of the 'blocked' variable is returned
     */
    public int getBlocked()
    {
        return blocked;
    }

    /*
        Purpose: to return the value of the pageFault variable
        Pre-Condition: an instance of Process exists
        Post-Condition: a value of pageFault is returned
     */
    public boolean isPageFault()
    {
        return pageFault;
    }

    /*
        Purpose: to simulate the process being run when using the LRU page replacement algorithm
        Pre-Condition: an instance of Process exists and a valid input is provided
        Post-Condition: the process is executed and a boolean value is returned to indicated whether the process is finished or not
     */
    public boolean runLRU(int clock)
    {
        // check to see there was a page fault in the previous run of the program
        if (pageFault)
        {
            pageFault = false;
            // check to see if the page allocation for the process is full or not
            if (pageFrames.size() == totalNumberOfFrames)
            {
                // check to see if the LRU variable is set to false
                if (!LRU)
                {
                    LRU = true;
                    pageFault = true;       // the page doesn't exists in the main memory hence a page fault has occurred
                    faultTimes.add(clock);  // adding the page fault time to the faultTimes variable
                }
                else
                {
                    LRU = false;                    // setting the page fault value to false
                    lruAlg(pRequests.remove());     // add the page request to the allocated page frames using the LRU algorithm
                    // check to see if the page request queue is empty
                    if (pRequests.size() == 0)
                    {
                        finishedTime = clock + 1;       // setting the finished time for the process
                        return true;
                    }
                }
            }
            // the allocated page frames are not full in main memory for the process
            else
            {
                // removing the page request from pRequests and using the function findPage to get a page reference to the page in virtual memory
                Page insertingPage = findPage(pRequests.remove());
                pageFrames.add(insertingPage);      // adding the pages to the allocated page frames for the process in main memory
                // updating the last used list
                leastUsed.addFirst(insertingPage);
                // check to see if page requests have been fulfill
                if (pRequests.size() == 0)
                {
                    finishedTime = clock + 1;       // setting the finished time for the process
                    return true;
                }
            }
        }

        // there was no page fault in the previous run of the program
        else
        {
            // check to see if the page is in the allocated pages from the process
            boolean pageFound = checkAllocatedPages(pRequests.peek());
            // the page exists in main memory
            if (pageFound)
            {
                // updating the least used list
                setLastUsed(pRequests.remove());

                // check to see if the page requests have been fulfilled
                if (pRequests.size() == 0)
                {
                    finishedTime = clock + 1;   // setting the finished time for the process
                    return true;
                }
            }
            // page wasn't found in the allocated page frames for the process
                else
                {
                    faultTimes.add(clock);      // adding the fault time to the faultTime variable
                    pageFault = true;           //setting the page fault variable to true
                    // if the allocated page frames are taken up then in the next iteration the lru algorithm is used to
                    // replace the least used page from the allocated frames.
                    if (pageFrames.size() == totalNumberOfFrames)
                    {
                        LRU = true;
                    }
                }
        }
        return false;
    }

    /*
        Purpose: check the allocated page frames to see if the page exists within the frames
        Pre-Condition: an instance of Process exists and a valid input is provided
        Post-Condition: a boolean value is returned which is determined on whether the page was found or not
     */
    private boolean checkAllocatedPages(int pageNumber)
    {
        for (int i = 0; i < pageFrames.size(); i++)
        {
            // checking to see if the page frame element id is equal to the page number
            if (pageFrames.get(i).getId() == pageNumber)
            {
                return true;        // returning true as the page was found in main memory
            }
        }
        return false;
    }

    /*
        Purpose: to find the page in virtual memory
        Pre-Condition: an instance of Process exists and a valid input is provided
        Post-Condition: a reference to page that has the same id as the input value is returned
     */
    private Page findPage(int pageId)
    {
        int index = -1;     // stores the index of the found page
        for (int i = 0; i < listOfPages.size(); i++)
        {
            // checking to see if the page frame element id is equal to the page id
            if (listOfPages.get(i).getId() == pageId)
            {
                index = i;      // the index is set to the value of 'i'
                break;
            }
        }
        return listOfPages.get(index);      // returns a reference to the page in virtual memory
    }

    /*
        Purpose: to move the page in the leastUsed variable to the head of list
        Pre-Condition: an instance of Process exists and a valid input is provided
        Post-Condition: the page (whose if value matches that of input parameter) that of inside the leastUsed variable is moved to the head of list
     */
    private void setLastUsed(int pageNumber)
    {
        // finding the page in the least used linked list
        for (int i = 0; i < leastUsed.size(); i++)
        {
            // when found it is move to the head of the linked list meaning it is least used
            if (leastUsed.get(i).getId() == pageNumber)
            {
                // moving the page to the head off the linked list as it is last used
                leastUsed.addFirst(leastUsed.remove(i));
                break;
            }
        }
    }

    /*
        Purpose: to simulate the LRU algorithm to insert a page into the allocated page frames in main memory for the process
        Pre-Condition: an instance of Process exists and a valid input is provided
        Post-Condition: the least used page is replaced with the new page (page request) in the allocated page frames for the process
     */
    private void lruAlg(int pageNumber)
    {
        Page insertPage = findPage(pageNumber);     // a variable to store the page reference that is returned when using the findPage function
        Page removedPage = leastUsed.removeLast();  // a variable to store the removed page from the tail of the leastUsed linked list
        leastUsed.addFirst(insertPage);             // inserting the new page into the leastUsed linked list

        // finding and removing the page from the main memory allocated frames
        for (int i = 0; i < pageFrames.size(); i++)
        {
            // check to see if the id of removedPage matches that of the pageFrame elements id
            if (removedPage.getId() == pageFrames.get(i).getId())
            {
                // replacing the page within the pageFrames variable with the new page in the allocated page frame for the process
                pageFrames.set(i,insertPage);
                break;
            }
        }
    }

    /*
        Purpose: to simulate execution of the process when using the clock page replacement algorithm
        Pre-Condition: an instance of Process exists and a valid input is provided
        Post-Condition: the process is executed and a boolean value is returned to indicated whether the process is finished or not
     */
    public boolean runClock(int clock)
    {
        // check to see if there was a page fault in teh previous run of the process
        if (pageFault)
        {
            pageFault = false;      // setting the page fault variable to false
            // check to see if the clockPage frames are full
            if (clockPagesFrames.size() == totalNumberOfFrames)
            {
                // using the clock algorithm to insert a page into the allocated page frames.
                clockAlgorithm(pRequests.remove());
            }
            // the number of frames allocated are free for stuff to be added into it
            else
            {
                // finding the page in virtual memory and adding it to the allocated page frames for the process
                addClock(findPage(pRequests.remove()));
            }

            // check to see if the process is finished
            if (pRequests.size() == 0)
            {
                finishedTime = clock + 1;       // setting the finished time for the process
                return true;
            }
        }
        // there was no page fault in the previous run of the function
        else
        {
            // check to see if requested page exists within the allocated page frames by using the findPageClock function
            boolean found = findPageClock(pRequests.peek());
            // the page wasn't found in the allocated page frames hence we've encountered a page fault
            if (!found)
            {
                faultTimes.add(clock);      // adding the page fault time to the faultTimes array list
                pageFault = true;
            }
            else
            {
                // the page was found in the main memory so we delete the page request from the queue
                pRequests.remove();
                // check to see if the page requests have been fulfilled
                if (pRequests.size() == 0)
                {
                    finishedTime = clock + 1;       // setting the finished time for the process
                    return true;
                }
            }

        }
        return false;
    }

    /*
        Purpose: to check if the requested page (input) exists within the clock page frames
        Pre-Condition: an instance of Process exists
        Post-Condition: if the page exists within the clock page frames then it has its useBit set to 1 and a boolean value of true is returned
     */
    private boolean findPageClock(int pageID)
    {
        for (int i = 0; i < clockPagesFrames.size(); i++)
        {
            // check to see if the clockPageFrames element has the same id value as the input
            if (clockPagesFrames.get(i).getId() == pageID)
            {
                clockPagesFrames.get(i).setUseBit(1);       // setting the useBit of the page to 1
                return true;
            }
        }
        return false;
    }

    /*
        Purpose: add the input into the clockPageFrames variable
        Pre-Condition: an instance of Process exists and a valid input is provided
        Post-Condition: the input page is added to the clockPageFrames variable
     */
    private void addClock(Page p)
    {
        clockPagesFrames.add(p);        // adding the page to clockPageFrames
        clockPagesFrames.get(clockPointer).setUseBit(1);    // setting the inserted page's use bit to 1
        clockPointer++;     // incrementing the clock pointer

        // if the clock pointer has reached the bottom of the clockPageFrames (the total number of frames) then it is moved to the head of the list aka to 0
        if (clockPointer == totalNumberOfFrames)
        {
            clockPointer = 0;
        }
    }

    /*
        Purpose: to replace a page in clockPageFrames with the page requested
        Pre-Condition: an instance of Process exists and a valid input is provided
        Post-Condition: a page in clockPageFrames is replaced with the page requested by the process
     */
    private void clockAlgorithm(int pageNumber)
    {
        Page newPage = findPage(pageNumber);        // a variable that stores a reference to the page in virtual memory that is returned from the find page function
        newPage.setUseBit(1);           // setting the used bit of the page that is being inserted to 1

        // iterating through clock until a page is replaced with the page that is requested
        while (true)
        {
            // if the clock pointer has reached the bottom of the clockPageFrames (the total number of frames) then it is moved to the head of the list aka to 0
            if (clockPointer == totalNumberOfFrames)
            {
                clockPointer = 0;
            }

            // if the current page frame has its use bit set to '0', we replace it with newPage that is requested
            if (clockPagesFrames.get(clockPointer).getUseBit() == 0)
            {
                clockPagesFrames.set(clockPointer, newPage);        // replacing the page with the requested page
                clockPointer++;     // incrementing the clock pointer
                break;      // breaking while loop
            }
            // if the current page frame has its use bit set to '1' so we set it to '0' as the new page hasn't been inserted yet
            else
            {
                clockPagesFrames.get(clockPointer).setUseBit(0);    // setting the useBit
                clockPointer++;     // incrementing the clock pointer
            }
        }
    }

    /*
        Purpose: To reset the Process object back to its initial state (the state set in the constructor)
        Pre-Condition: an instance of Process exists
        Post-Condition: the Process object is set back to its initial state (the state set in the constructor)
     */
    public void reset()
    {
        while(true)
        {
            faultTimes.remove(0);   // removing all the elements within the faultTimes variables
            // if the fault times variable size is 0 (it is empty) then we break the while loop
            if (faultTimes.size() == 0)
            {
                break;
            }
        }
        // the pRequests is set to the value of storedPageRequests
        pRequests = storedPageRequests;
        pageFault = false;      // setting the page Fault variable to false
    }

}