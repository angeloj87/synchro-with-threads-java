import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Restaraunt extends Thread{

    public static Customer c[]; //array of customers
    public static table_employee t_e[]; //array of table employees

    public static pickup_order_employee pickup_employee;

    public static boolean isStoreOpen;

    public static Queue<Customer> dineInQueue; //creation of queue for the dine in
    private static AtomicBoolean dineInAtomicBoolean; // "one at a time"

    private static AtomicBoolean seatingAtomicBoolean; // ^^

    public static int validCustomers;
    public static void main(String[] args) throws InterruptedException {
        isStoreOpen = true;
             // setting the vars
        dineInAtomicBoolean = new AtomicBoolean(false);
        seatingAtomicBoolean = new AtomicBoolean(false);
        dineInQueue = new LinkedList<>();

        //int cust_count = Integer.parseInt(args[0]);
        int cust_count = 17;

        validCustomers = cust_count; //keep count of the customers whom are good to go
        int table_count = 3;
        int table_seats = 3;
        int num_emp = 2;

        pickup_employee = new pickup_order_employee();
        pickup_employee.start(); //declare the thread and start it

        t_e = new table_employee[num_emp];

        int table_per_emp[] = new int[num_emp];
        while (table_count > 0) {
            for (int i = 0; i < num_emp && table_count > 0; i++) {
                table_per_emp[i]++;
                table_count--;
            }
        }

        for (int i = 0; i < t_e.length; i++) {
            t_e[i] = new table_employee(i+1,table_per_emp[i], table_seats, seatingAtomicBoolean);
            t_e[i].start();
        }

        c = new Customer[cust_count];

        for (int i = 0; i < c.length; i++) {
            Customer customer = new Customer(i + 1, dineInAtomicBoolean);
            customer.start();

            c[i] = customer;
        }

        while (validCustomers > 0 || pickup_employee.pickupCustomers.size() > 0) {
            try{
                Thread.sleep(100);
            }catch (Exception e){

            }

        }
        System.out.println("FINISHED MAIN");
        isStoreOpen = false;
    }
}
