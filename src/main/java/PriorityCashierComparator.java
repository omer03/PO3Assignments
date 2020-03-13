import java.util.Comparator;

class PriorityCashierComparator implements Comparator<Customer> {

    private int maxNumberOfItemsForPriority;

    PriorityCashierComparator(int maxNumberOfItemsForPriority) {
        this.maxNumberOfItemsForPriority = maxNumberOfItemsForPriority;
    }

    @Override
    public int compare(Customer c1, Customer c2) {
        // Customer gets priority when her/his items don't exceed maxNumberOfItemsForPriority
        return Integer.compare(c1.getNumberOfItems(), maxNumberOfItemsForPriority);
    }
}
