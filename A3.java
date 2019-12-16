/**
 * File Name: A3.java
 * Course: COMP2240 - Operating Systems
 * Assessment: Assignment 3
 */
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class A3
{
    public static void main(String[] args)
    {
        int totalNumberOfFrames = Integer.parseInt(args[0]);        // a variable to store the total number of frames in main memory
        int timeQuantum = Integer.parseInt(args[1]);                // a variable to store the time quantum for the round robin algorithm
        int NumOfProcesses = args.length - 2;                       // a variable to store the total number of processes
        int allocatedFrames = totalNumberOfFrames/NumOfProcesses;   // a variable to store the calculated allocated frames for each process

        Queue<Process> readyQ = new LinkedList<Process>();          // a queue to store all the creating processes
        int id = 1;                 // a variable used to assign each process an id
        int fileDone = 0;         // a counter variable to count the number of file processed
        int fileNumber = 2;      // a variable that is used an an index to get files from args
        while (fileDone < NumOfProcesses)
        {
            File file = new File(args[fileNumber]);
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = br.readLine();
                line = br.readLine();
                ArrayList<Integer> pageRequests = new ArrayList<Integer>();         // variable to store the page requests
                while (!line.equals("end"))
                {
                    pageRequests.add(Integer.parseInt(line));   // adding the number to the pageRequests array list
                    line = br.readLine();
                }

                // creating a process with the retrieved data from the file
                Process temp = new Process(id, args[fileNumber], pageRequests, allocatedFrames);
                // adding the process to the readyQueue
                readyQ.add(temp);
                id++;               // incrementing id variable
                fileNumber++;       // incrementing fileNumber variable
                fileDone++;         // incrementing fileDone variable

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("LRU - Fixed:");
        Processor algorithm = new Processor(readyQ, timeQuantum);
        // running the processor and use the algorithm for LRU page replacement
        ArrayList<Process> output = algorithm.run(true);
        printResults(output);           // outputting the results

        System.out.println("\n-------------------------------------------------------------------------------\n");
        algorithm.reset();
        System.out.println("Clock - Fixed:");
        // running the processor and use the algorithm for clock page replacement
        output = algorithm.run(false);
        printResults(output);           // outputting the results

    }

    /*
        Purpose: to print the results from the processor output
        Pre-Condition: an instance of A3 exists and a valid input is provided
        Post-Condition: the results from the processor output is printed
     */
    public static void printResults(ArrayList<Process> output)
    {
        System.out.println("PID\tProcess Name\tTurnaround Time\t# Faults\tFaultTimes");
        for (int i = 1; i < 5; i++)
        {
            for (int z = 0 ; z < output.size(); z++)
            {
                if (output.get(z).getId() == i)
                {
                    System.out.print(output.get(z).getId() + "\t" + output.get(z).getpName() + "\t" + output.get(z).getFinishedTime() + "\t\t" + output.get(z).getFaultTimes().size() + "\t\t{");

                    // getting the faultTimes arraylist from the Processor
                    ArrayList<Integer> faultTimes = output.get(z).getFaultTimes();
                    for (int q = 0; q < faultTimes.size(); q++)
                    {
                        if (q == faultTimes.size() - 1)
                        {
                            System.out.println(faultTimes.get(q) + "}");
                        }
                        else
                        {
                            System.out.print(faultTimes.get(q) + ", ");
                        }
                    }
                }
            }
        }
    }
}
