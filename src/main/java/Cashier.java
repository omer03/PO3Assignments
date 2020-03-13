/**
 * Supermarket Customer check-out and Cashier simulation
 * @author  hbo-ict@hva.nl
 */

import java.time.LocalTime;
import java.util.Queue;

public abstract class Cashier {

    private String name;                    // name of the cashier, for results identification
    protected Queue<Customer> waitingQueue; // queue of waiting customers
    protected LocalTime currentTime;        // tracks time for the cashier during simulation
    protected Customer currentCustomer;     // the customer that is currently being served by the cashier
    protected int totalIdleTime;            // tracks cumulative seconds when there was no work for the cashier
    protected int maxQueueLength;           // tracks the maximum number of customers at the cashier at any time
    private int amountOfServedCustomers;    // counts the amount of customers that the cashier has helped
    private double totalCheckoutTime;       // total checkout time of the cashier
    private double totalWaitingTime;        // sum of the waiting time of all the customers
    private double maxWaitingTime;          // maximum waiting time for a specific customer
    protected final int itemScanTime = 2;
    protected final int customerInteractionTime = 20;


    protected Cashier(String name) {
        this.name = name;
    }

    /**
     * restart the state if simulation of the cashier to initial time
     * with empty queues
     * @param currentTime
     */
    public void reStart(LocalTime currentTime) {
        this.waitingQueue.clear();
        this.currentTime = currentTime;
        this.totalIdleTime = 0;
        this.totalWaitingTime = 0;
        this.totalCheckoutTime = 0;
        this.maxWaitingTime = 0;
        this.maxQueueLength = 0;
        this.amountOfServedCustomers = 0;
    }

    /**
     * calculate the expected nett checkout time of a customer with a given number of items
     * this may be different for different types of Cashiers
     * @param numberOfItems
     * @return
     */
    public abstract int expectedCheckOutTime(int numberOfItems);

    /**
     * calculate the currently expected waiting time of a given customer for this cashier.
     * this may depend on:
     * a) the type of cashier,
     * b) the remaining work of the cashier's current customer(s) being served
     * c) the position that the given customer may obtain in the queue
     * d) and the workload of the customers in the waiting queue in front of the given customer
     * @param customer
     * @return
     */
    public abstract int expectedWaitingTime(Customer customer);

    /**
     * proceed the cashier's work until the given targetTime has been reached
     * this work may involve:
     * a) continuing or finishing the current customer(s) begin served
     * b) serving new customers that are waiting on the queue
     * c) sitting idle, taking a break until time has reached targetTime,
     *      after which new customers may arrive.
     * @param targetTime
     */
    public abstract void doTheWorkUntil(LocalTime targetTime);

    /**
     * add a new customer to the queue of the cashier
     * the position of the new customer in the queue will depend on the priority configuration of the queue
     *
     * @param customer A new customer
     */
    public void add(Customer customer) {
        // Only add customer if customer has items
        if (customer.getNumberOfItems() > 0) {
            // Calculate the duration of the checkout time for the customer
            customer.setActualCheckOutTime(expectedCheckOutTime(customer.getNumberOfItems()));
            this.totalCheckoutTime += customer.getActualCheckOutTime();
            waitingQueue.add(customer);

            // Check if Cashier is currently handling a Customer
            final int queueSize = waitingQueue.size() + (currentCustomer == null ? 0 : 1);
            // Assign the new maxQueueLength if bigger:
            maxQueueLength = Math.max(maxQueueLength, queueSize);
            amountOfServedCustomers++;
        }
    }

    public int getAmountOfServedCustomers() {
        return amountOfServedCustomers;
    }

    public double getAverageWaitingTime() {
        return totalWaitingTime / amountOfServedCustomers;
    }

    public void addWaitingTime(double customerWaitTime) {
        totalWaitingTime += customerWaitTime;
    }

    public double getAverageCheckOutTime() {
        return totalCheckoutTime / amountOfServedCustomers;
    }

//    public void addCheckoutTime(double customerCheckoutTime) {
//        totalCheckoutTime += customerCheckoutTime;
//    }

    public double getMaxWaitingTime() {
        return maxWaitingTime;
    }

    public void setMaxWaitingTime(double value) {
        maxWaitingTime = value;
    }

    public int getTotalIdleTime() {
        return totalIdleTime;
    }

    public LocalTime getCurrentTime() {
        return currentTime;
    }

    public String getName() {
        return name;
    }

    public int getMaxQueueLength() {
        return maxQueueLength;
    }

    public void setCurrentTime(LocalTime currentTime) {
        this.currentTime = currentTime;
    }
    public void setTotalIdleTime(int totalIdleTime) {
        this.totalIdleTime = totalIdleTime;
    }

    public Queue<Customer> getWaitingQueue() {
        return waitingQueue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\n");
        for (Customer customer : waitingQueue) {
            sb.append(customer.toString()).append("\n");
        }
        return sb.toString();
    }
}
