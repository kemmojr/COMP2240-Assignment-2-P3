/*
COMP2240 Assignment 2 Problem 3
File: Restaurant.java
Author: Timothy Kemmis
Std no. c3329386
*/

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class Restaurant {
    private static int servedCustomers;//Shared time variable and variable for tracking the number of customers that have entered the store
    private static AtomicBoolean full;//Atomic boolean used for tracking if the restaurant has reached max capacity and requires cleaning
    private static AtomicInteger availableSeats, time;//Static variables for tracking the number of seats in use and the global time of the program. They are Atomic to avoid concurrency issues
    private int totalCustomers;//The total number of customers to be served from the input file
    private static int threadTimeRemaining;//The amount of time remaining until the last running thread finishes
    private static ArrayList<CustomerThread> finishedCustomers;//An ArrayList used for holding the threads that have been executed

    //Constructor for the restaurant that initialises the restaurant variables, the global time and adds the total number of customers from the input file
    public Restaurant(int numSeats, int numTotalCustomers){
        time = new AtomicInteger(0);
        availableSeats = new AtomicInteger(numSeats);
        totalCustomers = numTotalCustomers;
        servedCustomers = 0;
        full = new AtomicBoolean(false);
        threadTimeRemaining = 0;
        finishedCustomers = new ArrayList<>();
    }

    //Getter
    public static synchronized int getTime() {
        return time.get();
    }

    //increments the time and negates the passed time from the time left to finish the longest running thread. Is a synchronised method to avoid concurrency errors
    public static synchronized void incrementTime(){
        time.getAndIncrement();
        threadTimeRemaining--;
    }

    //Gets the total number of seats in use. Is a synchronised method to avoid concurrency errors
    public static synchronized int getAvailableSeats(){
        return availableSeats.get();
    }

    //Negates the number of available seats by "Taking" one. Using synchronized stops multiple threads taking seats at once removing concurrency errors
    public static synchronized void takeSeat(){
        availableSeats.getAndDecrement();
    }

    //Increases the number of available seats by "Leaving" one. Using synchronized stops multiple threads leaving seats at once removing concurrency errors
    public static synchronized void leaveSeat(){
        availableSeats.getAndIncrement();
    }

    //Updates the time left to finish the longest running thread if the argument (the time to finish the current thread) will finish at a later time
    public synchronized void updateThreadTimeRemaining(int remainingTime){
        if (remainingTime > threadTimeRemaining)
            threadTimeRemaining = remainingTime;
    }

    //The function that is called repeatedly that checks the list of all CustomerThreads and starts the Thread if the CustomerThread start time is the current time.
    public synchronized void check(ArrayList<CustomerThread> customers){
        try {
            TimeUnit.MILLISECONDS.sleep(2);//Wait to avoid time increase/ sync errors
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (full.get()){//If the restaurant is full then don't start any threads until all current customers have finished eating and the 5 minute cleaning has occurred
            if (availableSeats.get()==5) {
                time.getAndAdd(5);
                full.set(false);

            } else
                return;
        }
        int size = customers.size();

        for(int i=size; i > 0; i--){//Iterates through all the waiting customers and adds them to the finished customers and then enters them into the restaurant
            CustomerThread c = customers.get(0);
            if (c.getArriveTime()<=time.get()){
                if (getAvailableSeats()>0){
                    customers.remove(c);
                    updateThreadTimeRemaining(c.getEatingLen());
                    finishedCustomers.add(c);//An arraylist for storing the references to all the threads that are started. So when the program is finished all the threads will have metrics that can be outputted
                    enter(c);//enters the threads into the restaurant
                    if (getAvailableSeats()==0){//If the last seat has been taken set the restaurant to full
                        full.set(true);
                        break;
                    }
                }
            }
        }

        try {
            TimeUnit.MILLISECONDS.sleep(2);//Wait to avoid time increase/ sync errors
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Method that negates the available seats and starts a CustomerThread as they "enter" the restaurant
    public synchronized void enter(CustomerThread c){
        try {
            takeSeat();
            c.start();
            servedCustomers++;
        } catch (Exception e){
            System.out.println("Entering failed");
        }
    }

    //Check to see if all of the customers have been served and have finished executing
    public boolean allServed(){
        if (servedCustomers==totalCustomers && threadTimeRemaining <= 0){
            return true;
        }
        return false;
    }

    //Ouputs the statistics of the customers
    public static void outputStats(){
        try {
            TimeUnit.MILLISECONDS.sleep(4);//A wait to ensure that all threads have finished executing
        } catch (Exception e){

        }
        System.out.println("Customer\tArrives\t\tSeats\tLeaves");//Ouput formatting as per spec
        for (CustomerThread c:finishedCustomers){//Iterates through all of the customers displaying their statistics
            System.out.println(c);
        }
    }
}
