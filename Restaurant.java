

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class Restaurant {
    private static int servedCustomers;
    private static AtomicBoolean full;
    private static AtomicInteger avaliableSeats, time;
    private int totalCustomers;
    private static int threadTimeRemaining;
    private static ArrayList<CustomerThread> finishedCustomers;
    
    public Restaurant(int numSeats, int numTotalCustomers){
        time = new AtomicInteger(0);
        avaliableSeats = new AtomicInteger(numSeats);
        totalCustomers = numTotalCustomers;
        servedCustomers = 0;
        full = new AtomicBoolean(false);
        threadTimeRemaining = 0;
        finishedCustomers = new ArrayList<>();
    }

    public static synchronized int getTime() {
        return time.get();
    }

    public static synchronized void incrementTime(){
        time.getAndIncrement();
        threadTimeRemaining--;
    }

    public static synchronized int getAvaliableSeats(){
        return avaliableSeats.get();
    }

    public static synchronized void takeSeat(){
        avaliableSeats.getAndDecrement();
    }

    public static synchronized void leaveSeat(){
        avaliableSeats.getAndIncrement();
    }

    public synchronized void updateThreadTimeRemaining(int remainingTime){
        if (remainingTime > threadTimeRemaining)
            threadTimeRemaining = remainingTime;
    }

    public synchronized void check(ArrayList<CustomerThread> customers){
        if (full.get()){
            if (avaliableSeats.get()==5) {
                time.getAndAdd(5);
                full.set(false);

            } else
                return;
        }
        int size = customers.size();

        for(int i=size; i > 0; i--){
            CustomerThread c = customers.get(0);
            if (c.getArriveTime()==time.get()){
                if (getAvaliableSeats()>0){
                    customers.remove(c);
                    updateThreadTimeRemaining(c.getEatingLen());
                    finishedCustomers.add(c);
                    enter(c);
                } else {
                    full.set(true);
                    System.out.println("Restaurant full");
                    break;
                }
            }
        }
    }

    public synchronized void enter(CustomerThread c){
        try {
            takeSeat();
            c.start();
            servedCustomers++;
        } catch (Exception e){
            System.out.println("Entering failed");
        }
    }

    public boolean allServed(){//Check to see if all of the customers have been served and have finished executing
        if (servedCustomers==totalCustomers && threadTimeRemaining <= 0){
            return true;
        }
        return false;
    }

    public static void outputStats(){//Ouputs the statistics of the customers

        try {
            TimeUnit.MILLISECONDS.sleep(4);//A wait to ensure that all threads have finished executing
        } catch (Exception e){

        }
        System.out.println("Customer\tArrives\t\tSeats\tLeaves");//Ouput formatting as per spec
        for (CustomerThread c:finishedCustomers){
            System.out.println(c);
        }
    }
}
