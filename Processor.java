/**
 * File Name: Processor.java
 * Course: COMP2240 - Operating Systems
 * Assessment: Assignment 3
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class Processor
{
    private Queue<Process> readyQueue = new LinkedList<Process >();                // a variable to store the queue of ready processes
    private ArrayList<Process> blockedProcesses = new ArrayList<>();        // a variable to store the blocked processes
    private ArrayList<Process> finishedProcesses = new ArrayList<>();       // a variable to store the finished processes
    private int numOfProcess;           // a variable to store the number of processes
    private int clock;                  // a variable that represents time
    private int quantumSize;            // a variable to store the time quantum in the round robin algorithm

    /*
        Purpose: a constructor for Processor object
        Pre-Condition: valid inputs are provided to the function
        Post-Condition: an instance of Processor is created with the provided inputs
     */
    public Processor(Queue<Process> readyQueue, int quantumSize)
    {
        this.readyQueue = readyQueue;
        numOfProcess =  readyQueue.size();      // the number of processes
        this.quantumSize = quantumSize;
        clock = 0;
    }

    /*
        Purpose: to simulate the processes being executed
        Pre-Condition: an instance of Processor exists and a valid input is provided
        Post-Condition: the processor are executed in a round robin algorithm and the array list of finished processes is returned
     */
    public ArrayList<Process> run(boolean useLRU)
    {
        Process currentProcess;     // a variable to store the current process being executed
        // continues until all process are finished
        while (finishedProcesses.size() < numOfProcess)
        {
            // check to see if the ready queue has a process that is ready to be executed.
            if (readyQueue.size() > 0)
            {
                // assigning the first process in the ready queue as the current running process
                currentProcess = readyQueue.remove();
                boolean isDone = false;                 // a boolean that is used to check if the process has been finished
                // running the process for the time quantum given
                for (int i = 0; i < quantumSize; i++)
                {
                    // the process is executed and returns an boolean value which lets the algorithm know if the process is finished
                    // the input boolean is checked to determine whether or not to use the LRU algorithm.
                    if (useLRU)
                    {
                    isDone = currentProcess.runLRU(clock);
                    }
                    else
                    {
                        isDone = currentProcess.runClock(clock);
                    }

                    // check to see if the process is finished. If so it is placed inside the finished array list.
                    if (isDone)
                    {
                        finishedProcesses.add(currentProcess);
                        clock++;                // incrementing the clock variable
                        checkedBlocked();       // checking the blocked array list to see which process is ready to be executed
                        break;                      // breaking the 'for' loop
                    }

                    // check to see if there was a page fault when the process was executed.
                    if (currentProcess.isPageFault())
                    {
                        currentProcess.isBeingBlocked(6);       // Setting the blocked time in the process for 6 time units
                        blockedProcesses.add(currentProcess);   // the process is added to the blocked array list
                        break;
                    }

                    clock++;                // increment the clock variable
                    checkedBlocked();       // check to see if any process are ready to be put into a ready queue

                    // check to see if the process has been reached its final quantum time allowed
                    if (i == (quantumSize - 1))
                    {
                        readyQueue.add(currentProcess);     // the process is added back into ready queue
                    }
                }
            }
            else
            {
                clock++;            // incrementing the clock variable
                checkedBlocked();   // checking to see if any of the process are ready to be unblocked yet
            }
        }
        return finishedProcesses;       // return in the finished processes array list
    }

    /*
        Purpose: To check the blocked queue to see if there is any processes ready to be added to the Ready Queue
        Pre-Condition: an instance of Processor exists
        Post-Condition: if there is any processes that become ready then it is added into the Blocked Queue
     */
    private void checkedBlocked()
    {
        // check to see if there is any process inside the blocked queue
        if (blockedProcesses.size() > 0)
        {
            // A Priority Queue to store and sort out the process based on their id. (lowest id goes to the front of the Queue)
            PriorityQueue<Integer> processQueue = new PriorityQueue<>();
            for (int i = 0; i < blockedProcesses.size(); i++)
            {
                // decreasing the blocked time for each process in the Blocked Queue
                blockedProcesses.get(i).decreaseBlockedTime();
                // check to see if the process' blocked time is equal to 0 meaning it is no longer blocked
                if (blockedProcesses.get(i).getBlocked() == 0)
                {
                    // adding the id of the process to the Priority Queue
                    processQueue.add(blockedProcesses.get(i).getId());
                    i--;        // decrementing i
                }
            }

            while (processQueue.size() > 0)
            {
                // searching through the blocked queue to find the process with the id that matches the id at the front of the queue
                for (int i = 0; i < blockedProcesses.size(); i++)
                {
                    // check to see if the id matches the integer at the front of the queue
                    if (blockedProcesses.get(i).getId() == processQueue.peek())
                    {
                        // adding the process back onto the Ready Queue
                        readyQueue.add(blockedProcesses.remove(i));
                        // removing the element at the front of the Priority Queue
                        processQueue.remove();
                        break;
                    }
                }
            }
        }
    }

    /*
        Purpose: To reset the Processor object back to its initial state (the state set in the constructor)
        Pre-Condition: an instance of Processor exists
        Post-Condition: The processor object is reset to its initial state (the state set in the constructor)
     */
    public void reset()
    {
        int id = 1;      // a variable that is used as an id counter
        while(true)
        {
            // checking through the finished array list to find the process with the id that matches the 'i' variable value
            for (int z = 0; z < finishedProcesses.size(); z++)
            {
                // comparing the id of the process and 'i' value
                if (finishedProcesses.get(z).getId() == id)
                {
                    finishedProcesses.get(z).reset();               // calling the reset function in the process object
                    readyQueue.add(finishedProcesses.remove(z));    // adding the process back into the ready queue and removing it from the finished array list
                    break;
                }
            }
            id++;        // incrementing the id variable
            // check to see if there is anything inside the finished array list
            if (finishedProcesses.size() == 0)
            {
                break;
            }
        }
        clock = 0;          // assigning the clock variable to a value of 0
    }
}
