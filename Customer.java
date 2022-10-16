import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Customer extends Thread {

    private table_employee host;

    private int table_num;

    public int customerNumber;
    private AtomicBoolean dineInAtomicBoolean;
    private boolean isSeated;
    private boolean orderTaken;
    private boolean paid;
    private boolean readyToOrder;

    public Customer(int customerNumber, AtomicBoolean dineInAtomicBoolean) {
        this.customerNumber = customerNumber;
        this.dineInAtomicBoolean = dineInAtomicBoolean;
        this.isSeated = false;
        this.orderTaken = false;
        this.readyToOrder = false;
        this.paid = false;
    }

    public static long time = System.currentTimeMillis();


    @Override
    public void run() {

        goToResturant();

        decideWhere();

        sendMessage("I finished eating, going home now");

    }

    private void goToResturant() {

        waitFor(2000);

        sendMessage("is commuting to the restaurant");

    }

    private void sendMessage(Object msg) {
        System.out.println("[" + (System.currentTimeMillis() - time) + "] Customer " + customerNumber + " " + msg);
    }

    private void waitForExact(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void waitFor(long max) {
        waitForExact((long) (Math.random() * max));
    }

    public void decideWhere() {
        int choice = new Random().nextInt(11);
        if (choice > 2) {
            sendMessage("has decided to dine in");
            dineIn();
        } else {
            sendMessage("has decided to take out/pick up");
            pickupOrder();
        }
    }

    private  void dineIn() {

        // enter the queue with mutual exclusion
        sendMessage("I am trying to enter the queue");
        while(true) {
            if (dineInAtomicBoolean.compareAndSet(false, true)) {
                sendMessage("i was able to access the queue");
                Restaraunt.dineInQueue.add(this);
                dineInAtomicBoolean.set(false);
                break;
            }
            waitForExact(100);
        }
        sendMessage("okay so i am inside the queue. i will busy wait until i am interrupted");

        // BW until they are actually seated
        while (!isSeated) { //M. E.
            waitForExact(100);
        }

        sendMessage("I am seated, i am no longer inside the bw");

        // Looking through the menu
        this.setPriority(new Random().nextInt(7, 10));
        this.waitFor(3000);
        this.setPriority(5);

        sendMessage("I am ready to order");
        this.readyToOrder = true;

        // BW until ur order is taken
        while (!orderTaken) { //mutual exclusion
            waitFor(100);
        }
        sendMessage("got out of busy wait. now enjoying food");
        waitFor(5000);
        this.yield();
        this.yield();
        sendMessage("I finished eating and yeilding twice. i am now ready to pay");

        this.host.requireAttention(this);
        while(!paid) {
            waitFor(100);
        }
        sendMessage("I finished paying. i am going home ");

        sendMessage("Go to home now");

        while(true) {
            if (dineInAtomicBoolean.compareAndSet(false, true)) {
                sendMessage("i was able to leave the queue");
            
                //Restaraunt.dineInQueue.remove();
                
                dineInAtomicBoolean.set(false);
                Restaraunt.validCustomers--;
                break;
                
            }
           
            waitForExact(100);
        }


         //TODO: this needs to be surrounded in a mutual exclusions
        sendMessage("the amount of customers left is: " + Restaraunt.validCustomers);
    }

    public void pay() {// pay
        sendMessage(host.id+" just asked me to pay");
        this.paid = true;
    }
//calls this method when the custy is ready to order
    public boolean isReadyToOrder() {
        return this.readyToOrder && !this.orderTaken;
    }

    public void takeOrder(table_employee emp) { //takes the order
        host = emp;
        sendMessage("My order was taken by employee " + emp.id);
        this.orderTaken = true;
    }

    public void seat(int num) { //seats the custy
        sendMessage("I Got seated by some employee");
        isSeated = true;
        this.table_num = num;
    }

    public int getTableNum() {
        return this.table_num;
    }
    private void pickupOrder() {
        Restaraunt.pickup_employee.getOnLine(this);
        Restaraunt.validCustomers--;
        sendMessage("i am not a valid cust so  decrement. LEFT IS: " + Restaraunt.validCustomers);

        sendMessage("got on the line, staying in Busy wait");
        while (!this.isInterrupted()) {
            try {
                Thread.sleep(100);

            }catch (InterruptedException e) {
                sendMessage("interrupt occured");
                break;
            }
        }
        sendMessage("i got off the busy wait. bye VC:" + Restaraunt.validCustomers);


    }

}

