package restaurantrush.entity;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

/**
 * The restaurant's state: tables, the waiting queue, and the running totals.
 * Totals only change through the record... methods, so revenue can
 * never include an unpaid or abandoned order.
 */
public class Restaurant {

    private final Menu menu = new Menu();
    private final List<Table> tables = new ArrayList<>();
    private final Deque<Customer> queue = new ArrayDeque<>();
    private final List<Customer> paidCustomers = new ArrayList<>();
    private int revenueCents;
    private int servedCount;
    private int departures;

    // === STAGE 3 FEATURE: HAPPY HOUR INSTANCE ===
    private final HappyHour happyHour = new HappyHour();

    /**
     * Creates a restaurant with empty tables.
     * @param tableCount number of tables (2 in the base setting)
     */
    public Restaurant(int tableCount) {
        for (int i = 1; i <= tableCount; i++) {
            tables.add(new Table(i));
        }
    }

    /**
     * Adds a newly arrived customer to the back of the waiting queue.
     * @param customer the customer who arrived
     */
    public void addArrival(Customer customer) {
        queue.addLast(customer);
    }

    /**
     * Moves a waiting customer from the queue to a table.
     * @param customer a customer in the queue
     * @param table    a free table
     * @throws IllegalStateException if the customer is not in the queue or the table is occupied
     */
    public void seat(Customer customer, Table table) {
        if (!queue.contains(customer)) {
            throw new IllegalStateException("C" + customer.getId() + " is not in the queue");
        }
        table.seat(customer);
        customer.seat();
        queue.remove(customer);
    }

    /**
     * Counts one dish served.
     */
    public void recordServed() {
        servedCount++;
    }

    /**
     * Records a payment: adds the bill to revenue, keeps the customer's
     * satisfaction for the average, and frees their table.
     * @param customer a customer who has just paid
     */
    public void recordPayment(Customer customer) {
        revenueCents += customer.getOrder().getBillCents();
        paidCustomers.add(customer);
        tableOf(customer).ifPresent(Table::clear);
    }

    /**
     * Records an unhappy departure: removes the customer from the queue or
     * frees their table. No revenue is added.
     * @param customer a customer who has just left
     */
    public void recordDeparture(Customer customer) {
        queue.remove(customer);
        tableOf(customer).ifPresent(Table::clear);
        departures++;
    }

    /**
     * Finds a customer who is waiting or seated.
     * @param id customer number
     * @return the customer, or empty if nobody with that number is here
     */
    public Optional<Customer> findPresentCustomer(int id) {
        return presentCustomers().stream().filter(c -> c.getId() == id).findFirst();
    }

    /**
     * Returns everyone currently in the restaurant: the queue first (oldest
     * first), then seated customers in table order.
     * @return a new list of present customers
     */
    public List<Customer> presentCustomers() {
        List<Customer> present = new ArrayList<>(queue);
        for (Table t : tables) {
            if (!t.isFree()) {
                present.add(t.getOccupant());
            }
        }
        return present;
    }

    /**
     * Finds the first free table.
     * @return a free table, or empty if all are occupied
     */
    public Optional<Table> freeTable() {
        return tables.stream().filter(Table::isFree).findFirst();
    }

    /**
     * Finds the table a customer is sitting at.
     * @param customer the customer
     * @return their table, or empty if they are not seated
     */
    public Optional<Table> tableOf(Customer customer) {
        return tables.stream().filter(t -> t.getOccupant() == customer).findFirst();
    }

    /**
     * Returns the average satisfaction recorded when each customer paid.
     * @return the average, or empty if nobody has paid yet
     */
    public OptionalDouble averageSatisfaction() {
        return paidCustomers.stream().mapToInt(Customer::getSatisfaction).average();
    }

    /**
     * Returns the menu customers order from.
     * @return the menu
     */
    public Menu getMenu() {
        return menu;
    }

    /**
     * Returns the waiting queue, oldest first.
     * @return a read-only view of the queue
     */
    public List<Customer> getQueue() {
        return List.copyOf(queue);
    }

    /**
     * Returns the tables in number order.
     * @return a read-only list of tables
     */
    public List<Table> getTables() {
        return Collections.unmodifiableList(tables);
    }

    /**
     * Returns total revenue from paid customers.
     * @return revenue in cents
     */
    public int getRevenueCents() {
        return revenueCents;
    }

    /**
     * Returns how many customers have paid.
     * @return paid customer count
     */
    public int getPaidCount() {
        return paidCustomers.size();
    }

    /**
     * Returns how many dishes have been served.
     * @return served count
     */
    public int getServedCount() {
        return servedCount;
    }

    /**
     * Returns how many customers left unhappy.
     * @return departure count
     */
    public int getDepartures() {
        return departures;
    }

    // === STAGE 3 FEATURE: HAPPY HOUR GETTER ===
    /**
     * Returns the global Happy Hour policy manager.
     * @return the happy hour instance
     */
    public HappyHour getHappyHour() {
        return this.happyHour;
    }
}