import java.time.LocalTime;
import java.util.*;

public class FIFOCashier extends Cashier {

    public int checkoutTimePerCustomer;
    public int checkoutTimePerItem;

    public FIFOCashier(String name) {
        super(name);
        this.checkoutTimePerCustomer = 20;
        this.checkoutTimePerItem = 2;
    }

    /**
     * calculate the expected nett checkout time of a customer with a given number of items
     * this may be different for different types of Cashiers
     *
     * @param numberOfItems
     * @return
     */
    @Override
    public int expectedCheckOutTime(int numberOfItems) {
        if (numberOfItems == 0) {
            return 0;
        }

        return checkoutTimePerCustomer + (numberOfItems * checkoutTimePerItem);
    }

    /**
     * calculate the currently expected waiting time of a given customer for this cashier.
     * this may depend on:
     * a) the type of cashier,
     * b) the remaining work of the cashier's current customer(s) being served
     * c) the position that the given customer may obtain in the queue
     * d) and the workload of the customers in the waiting queue in front of the given customer
     *
     * @param customer
     * @return waitingTime
     */
    @Override
    public int expectedWaitingTime(Customer customer) {
        int wt = 0;
        for (Customer cust : waitingQueue) {
            wt += expectedCheckOutTime(cust.getNumberOfItems());
        }
        return wt;
    }


    @Override
    public void add(Customer customer) {
        this.waitingQueue.add(customer);
    }

    /**
     * proceed the cashier's work until the given targetTime has been reached
     * this work may involve:
     * a) continuing or finishing the current customer(s) begin served
     * b) serving new customers that are waiting on the queue
     * c) sitting idle, taking a break until time has reached targetTime,
     * after which new customers may arrive.
     *
     * @param targetTime
     */
    @Override
    public void doTheWorkUntil(LocalTime targetTime) {
        maxQueueLength = waitingQueue.size();
        int amountOfCustomersHandled = 0;

        while (currentTime.isBefore(targetTime)) {
            while (waitingQueue.isEmpty() && currentTime.isBefore(targetTime)) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) { // Sleep for 1 sec before executing next count
                    e.printStackTrace();
                }
                totalIdleTime++; // count idle time of FIFOCashier
            }

            Customer customerBeingHandled = waitingQueue.peek();
            customerBeingHandled.setActualCheckOutTime(expectedCheckOutTime(customerBeingHandled.getNumberOfItems()));

            if (waitingQueue.size() > maxQueueLength) { maxQueueLength = waitingQueue.size(); }
            waitingQueue.remove(customerBeingHandled);
            amountOfCustomersHandled++;
        }
    }

    @Override
    public void reStart(LocalTime currentTime) {
        this.waitingQueue.clear();
        this.currentTime = currentTime;
        this.totalIdleTime = 0;
        this.maxQueueLength = 0;
    }


}