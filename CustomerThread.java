/*
COMP2240 Assignment 2 Problem 3
File: CustomerThread.java
Author: Timothy Kemmis
Std no. c3329386
*/

public class CustomerThread extends Thread {//custom thread class from the original java thread class
    private String ID;//Thread ID
    private int arriveTime, eatingLen;//Thread attributes from input file
    private int seatTime, leaveTime;//Thread metric tracking variables

    //CustomerThread constructor that initialises all of the information about the thread from the input file
    public CustomerThread(String id, int aTime, int eLen, int t){
        ID = id;
        arriveTime = aTime;
        eatingLen = eLen;
    }

    //getters
    public int getArriveTime(){
        return arriveTime;
    }

    public int getEatingLen(){
        return eatingLen;
    }

    @Override//The overridden run method of the thread
    public void run() {
        seatTime = Restaurant.getTime();//Metric tracking for when the customer took a seat using the shared time variable in Restaurant
        while (true){//While method that continues until the customer has been seated for their eating length
            if (Restaurant.getTime()-seatTime==eatingLen)
                break;
        }
        Restaurant.leaveSeat();//update the number of taken seats in restaurant
        leaveTime = Restaurant.getTime();//metric tracking for when the customer leaves the restaurant
    }

    @Override//Outputs the ID, arrival time, seated time and left time of the thread
    public String toString() {
        return  ID  + "\t\t\t" + arriveTime + "\t\t\t" + seatTime + "\t\t" + leaveTime;
    }
}
