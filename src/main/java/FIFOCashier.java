import java.time.LocalTime;
import java.util.ArrayDeque;

public class FIFOCashier extends Cashier {

    // The remaining seconds to handle the previous customer
    protected int remainingTimeHandlingPreviousCustomers;

    public FIFOCashier (String name) {
        super(name);
        waitingQueue = new ArrayDeque<>();
        remainingTimeHandlingPreviousCustomers = 0;
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
        return numberOfItems > 0 ? numberOfItems * itemScanTime + customerInteractionTime : 0;
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
        int waitingTime = 0;

        if (currentCustomer != null) {
            waitingTime += remainingTimeHandlingPreviousCustomers;
        }

        for (Customer queuedCustomer : waitingQueue) {
            waitingTime += expectedCheckOutTime(queuedCustomer.getNumberOfItems());
        }

        return waitingTime;
    }


    /**
     * proceed the cashier's work until the given targetTime has been reached
     * this work may involve:
     * a) continuing or finishing the current customer(s) begin served
     * b) serving new customers that are waiting on the queue
     * c) sitting idle, taking a break until time has reached targetTime, after which new customers may arrive.
     *
     * @param targetTime
     */
    @Override
    public void doTheWorkUntil(LocalTime targetTime) {
        // Obtain the duration between previous customer arrival (If any, since the first time there is no previous) and new customer arrival:
        final long durationBetweenCustomers = targetTime.toSecondOfDay() - currentTime.toSecondOfDay();

        // Check if there is a customer that is still being handled since this method is re-triggered once a new customer is queued to a cashier.
        if (currentCustomer == null) {
            // No current customer, so check if the queue contains a customer now (Since it  might be queued at a different cashier):
            if (waitingQueue.size() > 0) {
                // There is a customer in the queue so start handling it:
                currentCustomer = waitingQueue.poll();

                // Calculate the checkout time of current customer since there is no unfinished previous customer:
                final int checkoutDurationSeconds = currentCustomer.getActualCheckOutTime();
                // Calculate the LocalTime in seconds when this customer would have been handled:
                final int checkoutSecondsOfDay = currentTime.toSecondOfDay() + checkoutDurationSeconds;
                // Check if there is time enough to handle this customer:
                final int arrivalTimeFutureCustomer = targetTime.toSecondOfDay();
                if (checkoutSecondsOfDay <= arrivalTimeFutureCustomer) {
                    // There was enough time to handle the current customer:
                    // So this cashier will be idle for x amount OR can start handling the next customer
                    // Handle that by recursion, since resetting the currentCustomer will ensure that either the next customer is handled, or the idle time is updated:
                    // Update the currentTime since we handled this customer
                    currentTime = currentTime.plusSeconds(checkoutDurationSeconds);
                    currentCustomer = null;
                    doTheWorkUntil(targetTime);
                    return;
                } else {
                    // Next customer arrives before current Customer is handled (Might even be another customer in the queue, so all customers waiting times should be increased):
                    // Obtain the exceeding duration in seconds that is required to finish this customer after targetTime
                    remainingTimeHandlingPreviousCustomers = checkoutSecondsOfDay - arrivalTimeFutureCustomer;
                    // Update waiting time of customers in the queue:
                    for (Customer customer : waitingQueue) {
                        customer.setActualWaitingTime(customer.getActualWaitingTime() + remainingTimeHandlingPreviousCustomers);
                    }
                }

                this.addWaitingTime(currentCustomer.getActualWaitingTime());
                if(currentCustomer.getActualWaitingTime() > this.getMaxWaitingTime()){
                    this.setMaxWaitingTime(currentCustomer.getActualWaitingTime());
                }

            } else {
                // There is no (current)Customer that the Cashier can help, so the Cashier is idle until a new Customer joins the queue
                setTotalIdleTime((int) (getTotalIdleTime() + durationBetweenCustomers));
            }
        } else {
            // There is a customer that is unfinished so check if that can be finished now:
            if (remainingTimeHandlingPreviousCustomers <= durationBetweenCustomers) {
                // Increase the current time and let recursion handle the next Customer:
                currentTime = currentTime.plusSeconds(remainingTimeHandlingPreviousCustomers);
                currentCustomer = null;
                // Finished the previous customer:
                remainingTimeHandlingPreviousCustomers = 0;
                // Start handling the next (if any)
                doTheWorkUntil(targetTime);
                return;
            }
            // Not enough time to handle the current customer, so handle it partially by decreasing the remaining time
            remainingTimeHandlingPreviousCustomers -= durationBetweenCustomers;
        }
        // Update the current time:
        currentTime = targetTime;
    }

}