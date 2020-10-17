

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CustomerThread extends Thread {
    private String ID;
    private int arriveTime, eatingLen;
    private int seatTime, leaveTime;

    public CustomerThread(String id, int aTime, int eLen, int t){
        ID = id;
        arriveTime = aTime;
        eatingLen = eLen;
    }

    public int getArriveTime(){
        return arriveTime;
    }

    public int getEatingLen(){
        return eatingLen;
    }

    @Override
    public void run() {

        seatTime = Restaurant.getTime();
        System.out.println(ID + " Entering restaurant at time: " + Restaurant.getTime());
        while (true){
            if (Restaurant.getTime()-seatTime==eatingLen)
                break;
        }
        Restaurant.leaveSeat();
        leaveTime = Restaurant.getTime();
        System.out.println(ID + " leaving restaurant at time: " + Restaurant.getTime());
    }

    @Override
    public String toString() {
        return  ID  + "\t\t\t" + arriveTime + "\t\t\t" + seatTime + "\t\t" + leaveTime;
    }
}
