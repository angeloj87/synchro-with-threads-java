import java.util.LinkedList;
import java.util.Queue;

public class pickup_order_employee extends  Thread{

    public Queue<Customer> pickupCustomers;

    public pickup_order_employee() {
        pickupCustomers = new LinkedList<>();
    }

    @Override
    public void run() {
        sendMessage("Hi i started");
        while (Restaraunt.isStoreOpen) { //mutual exclusion

            if (pickupCustomers.size() > 0) { //check to see if there are customers in the line
                sendMessage("There are customers on the line");
                waitFor(5000);

                Customer customer = pickupCustomers.poll();
                sendMessage("Puled out custtomer " + customer.customerNumber);

                customer.interrupt();
            }

            waitForExact(500);
        }
        sendMessage("Finsihed serving everyone. gonna go home now");
    }

    public void getOnLine(Customer customer){
        pickupCustomers.add(customer);
    }



    private void sendMessage(Object msg) {
        System.out.println("[" + (System.currentTimeMillis() - Customer.time) + "] Pickup " + msg);
    }

    private void waitForExact(long delay) { //this is where the tread sleeps and gets interrupted as per the project standards
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void waitFor(long max) {
        waitForExact((long) (Math.random() * max));
    } //this is only an addition that would benifit the threads for our use


}

