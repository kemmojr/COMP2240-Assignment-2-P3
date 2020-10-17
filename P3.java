

import java.io.FileInputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


public class P3 {
    public static void main(String args[]){
        int availableSeats = 5;//AtomicInteger to manage the number of empty/filled seats
        int time = 0;

        ArrayList<CustomerThread> customers = new ArrayList<>();//Arraylist for storing the customers read in from the file as threads
        int numCustomers = 0;
        try {
            Scanner testReader = new Scanner(new FileInputStream(args[0]));
            Scanner reader = new Scanner(new FileInputStream(args[0]));
            int numItemsRead = 0;
            while (testReader.hasNext()){
                //While loop to work out exactly how many customers there are by counting the total number of separate strings
                String currentItem = testReader.next();
                if (currentItem.equalsIgnoreCase("END"))
                    break;
                numItemsRead++;
            }
            numCustomers = numItemsRead/3;//How many customers there are in the file. I have assumed that the input files used will be without formatting mistakes
            for (int i = 0; i < numCustomers; i++){
                //Creates all the customer objects from the input file
                int arriveTime = reader.nextInt();
                String id = reader.next();
                int eatingLength = reader.nextInt();
                customers.add(new CustomerThread(id,arriveTime,eatingLength,time));
            }

        } catch (Exception e){
            System.out.println("Reading from file failed");
        }

        Restaurant restaurant = new Restaurant(availableSeats, numCustomers);//A restaurant class for managing the seating in the restaurant

        while (!restaurant.allServed()){//Continues until all customers have been served

            restaurant.check(customers);//Enters and waits the customer threads as necessary
            Restaurant.incrementTime();//Increments the time
        }
        Restaurant.outputStats();//Outputs the customer statistics as per the spec
    }
}
