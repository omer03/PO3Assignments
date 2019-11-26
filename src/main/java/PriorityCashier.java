import java.util.Comparator;
import java.util.PriorityQueue;

public class PriorityCashier extends FIFOCashier {
    private int maxPriorityItems;

    public PriorityCashier(String name, int maxPriority) {
        super(name);
        this.maxPriorityItems = maxPriorityItems;
        //waitingQueue = new PriorityQueue<>(new PriorityComparator(maxPriorityItems));
    }

    @Override
    public int expectedWaitingTime(Customer customer) {
        int wt = 12; // de tijd die we de huidige klant nog moeten helpen // to change
        for (Customer cust : waitingQueue) {
            if (customer.getNumberOfItems() <= 5) {
                // groene klant
                if (cust.getNumberOfItems() > 5) {
                    break;
                }
            }
            wt += expectedCheckOutTime(cust.getNumberOfItems());
        }
        return wt;
    }

    @Override
    public void add(Customer customer) {
        this.waitingQueue.add(customer);
    }


}