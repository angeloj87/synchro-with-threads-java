import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public class table_employee extends Thread {

    private ArrayList<Customer> tables[];

    private Queue<Integer> nextTable;

    private ArrayList<Integer> currentTables;

    private ArrayList<Customer> needsAttention;

    private int seatCount;

    public int id;

    private int available;

    private AtomicBoolean seatingAtomicBoolean;
    public table_employee(int id, int table, int count, AtomicBoolean seatingAtomicBoolean) {
        this.id = id;
        tables = new ArrayList[table];
        nextTable = new LinkedList<>();
        for (int i = 0; i < table; i++) {
           tables[i] = new ArrayList<>();
            nextTable.add(i);
        }

        for (int i = 0; i < table; i++){
            currentTables = new ArrayList<>();
            needsAttention = new ArrayList<>(); }

        seatCount = count;

        available = table;

        this.seatingAtomicBoolean = seatingAtomicBoolean;
    }



    public void requireAttention(Customer c) {
        this.needsAttention.add(c);
    }

    private void sendMessage(Object msg) {
        System.out.println("[" + (System.currentTimeMillis() - Customer.time) + "] TAble Employee " + id + " " + msg);
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

    public void run() {
        sendMessage("I am here now and reporting to work");
        while (Restaraunt.isStoreOpen) {

            if (this.nextTable.size() > 0) {

                if (seatingAtomicBoolean.compareAndSet(false, true)) {
                    /* I FOUND NEW SET OF PPL THAT ARE READY TO EAT */
                    if (Restaraunt.dineInQueue.size() >= this.seatCount || Restaraunt.validCustomers < this.seatCount) {
                        // it thinks pickup line is valid TODO: this is bad workaround bc the last group HAS to wait for
                        // the pickup ppl to finish
                        if (Restaraunt.validCustomers < this.seatCount && Restaraunt.dineInQueue.size() != Restaraunt.validCustomers) {
                            continue;
                        }
                        sendMessage("The dine in queue has enough ppl AND i have available tables ");
                        int amount;
                        if (Restaraunt.dineInQueue.size() >= this.seatCount) {
                            amount = this.seatCount;
                        }else{
                            amount = Restaraunt.validCustomers;
                        }
        
                        //sendMessage(amount + " " + Main.dineInQueue.size() + " " + Main.validCustomers);
        
                        int table_num = nextTable.poll();
                        ArrayList<Customer> table = tables[table_num];
                        currentTables.add(table_num);
                        // SEAT THE CUSTOMERS
                        for (int i = 0; i < amount; i++) {
        
                            Customer customer = Restaraunt.dineInQueue.poll();
                            sendMessage("Taking this custonmer out of queue " + customer.customerNumber);
                            customer.seat(table_num);
                            table.add(customer);
        
                        }
                    }
        
                    seatingAtomicBoolean.set(false);
                }
        
        
            }

            checkcurrenttable();
           checkpayingcustomers();
            waitForExact(100);
        }
        sendMessage("Finished work going home now");
    }

public void checkdineinqueue(){
    
}

public void checkcurrenttable(){

    if (this.currentTables.size() > 0) {

        for (int table_num : currentTables) {

            ArrayList<Customer> check_table = tables[table_num];

            boolean everyoneReady = true;
            for (Customer customer : check_table) {
                if (!customer.isReadyToOrder()) {
                    everyoneReady = false;
                }
            }

            // letting them order one by one
            if (everyoneReady) {
                for (Customer customer : check_table) {
                    customer.takeOrder(this);
                }
            }else{
                // do nothing
            }

        }

    }

}
    
public void checkpayingcustomers(){
    if (this.needsAttention.size() > 0 ) {

        for (Customer customer : needsAttention) {

            customer.pay();

            int table_num = customer.getTableNum();

            tables[table_num].remove(customer);
            sendMessage("customer " + customer.customerNumber +" paid and is being removed");
            if (tables[table_num].size() == 0) {
                nextTable.add(table_num);
                sendMessage("bc the "+table_num+" table num freed up, adding to availbale tables");
            }
        }
        this.needsAttention.clear();
    }

}
}