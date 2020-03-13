import java.util.Comparator;

class PriorityCashierComparator implements Comparator<Customer> {

    private int maxNumberOfItemsForPriority;

    PriorityCashierComparator(int maxItems) {
        this.maxNumberOfItemsForPriority = maxItems;
    }

    @Override
    public int compare(Customer c1, Customer c2) {
        // Give customer Priority when items less than maxItems
        return Integer.compare(c1.getNumberOfItems(), maxNumberOfItemsForPriority);
    }
}
