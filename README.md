# COMP2240-Assignment_3
**_Mark_**: 100 / 100

## Comments by Marker: 
**Marks**: 
- I/O (valid input and output) : 10/10 
- Code Clarity (code structure, code comments) : 10/10 
- Implementation: 
    - 1: Round Robin : 10/10 
    - 2: LRU : 30/30 
    - 3: CLOCK : 30/30 

- Report:         10/10
 
- **Deductions**: 
    - Late (-10 per day late) : /0 
    - Coversheet (-5 if missing) : /0 
    - Other (see Feedback) : /0 
 
**_Total_**: 100/100 

**Notes**:    
I/O FORM: Good.   
RR: Good.     
LRU: Good.    
CLOCK: Good.    
REPORT: Good.     
CODE: Good.     
NOTES: Excellent - thanks :)

## Assignment Specifications
In [Assignment 1](https://github.com/lsingh19/COMP2240-Assignment_1), we assumed that the system had an infinite amount of memory. In this assignment, the operating system has a limited amount of memory, and this needs to be managed to meet process demands. You will write a program to simulate a system that uses paging with virtual memory. The characteristics of the system are described below: 

- **Memory**: The system has F frames available in main memory. During execution, the processor will determine if the page required for the currently running process is in main memory.    
    - If the page is in main memory, the processor will access the instruction and continue.     
    - If the page is not in main memory, the processor will issue a page fault and block the process until the page has been transferred to main memory.     
    - Initially no page is in the memory. ie: the simulation will be strictly demand paging, where pages are only brought into main memory when they are requested.    
    - In fixed allocation scheme frames are equally divided among processes, additional frames remain unused.    

- **Paging and virtual memory**: The system uses paging (with no segmentation).  
    - Each process can have a maximum 50 pages where each page contains a single instruction. 
    - For page replacement you will need to apply least recently used (LRU) and clock policy. 
    - Resident set is managed using ‘Fixed Allocation with Local Replacement Scope’ strategy. ie: you can assume, frames allocated to a process do not change over the simulation period and page replacements (if necessary) will occur within the allocated frames. 
    - All pages are read-only, so no page needs to be written back to disk. 

- **Page fault handling**: When a page fault occurs, the interrupt routine will handle the fault by placing an I/O request into a queue, which will later be processed by the I/O controller to bring the page into main memory. This may require replacing a page in main memory using a page replacement policy (LRU or clock). Other processes should be scheduled to run while such an I/O operation is occurring. 
    - Issuing a page fault and blocking a process takes no time. If a page fault occurs, then another ready process can run immediately at the same time unit. 
    - Swapping in a page takes 6 units of time (if a page required by a process is not in main memory, the process must be put into its blocked state until the required page is available).  

- **Scheduling**: The system is to use a Round Robin short-term scheduling algorithm with time a quantum of Q. 
    - Executing a single instruction (ie: a page) takes 1 unit of time. 
    - Switching the processes does not take any time. 
    - If multiple process becomes ready at the same time, then they enter the ready queue in the order of their ID.  
    - All the processes start execution at time t=0. 
    - If a process becomes ready at time unit t then execution of that process can occur in the same time unit t without any delay

You are to compare the performance of the LRU and Clock page replacement algorithms for the fixed allocation with local replacement strategy. Please use the basic versions of the policies introduced in lectures. 
