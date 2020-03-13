public class PriorityCashier extends FIFOCashier {

    private int maxPriorityItems; // the limit to where you can go first in the PriorityCashier waitingQueue

    public PriorityCashier(String name, int maxPriorityItems) {
        super(name);
        this.maxPriorityItems = maxPriorityItems;
        //waitingQueue = new PriorityQueue<>(new PriorityComparator(maxPriorityItems));
    }

    @Override
    public int expectedWaitingTime(Customer customer) {
        int waitingTime = 0;

        if (currentCustomer != null) {
            waitingTime += remainingTimeHandlingPreviousCustomers;
        }

        for (Customer queuedCustomer : waitingQueue) {
            if (customer.getNumberOfItems() > maxPriorityItems || queuedCustomer.getNumberOfItems() <= maxPriorityItems) {
                waitingTime += expectedCheckOutTime(queuedCustomer.getNumberOfItems());
            }
        }

        return waitingTime;
    }

}