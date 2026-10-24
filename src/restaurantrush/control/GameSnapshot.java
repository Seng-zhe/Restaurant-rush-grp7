package restaurantrush.control;

import java.util.List;
import java.util.OptionalDouble;

/**
 * Everything the UI needs to draw the start-of-turn and end-of-turn screens,
 * taken at one moment.
 *
 * @param turn                 current turn number, starting at 1
 * @param maxTurns             last turn of the game (18 in the base setting)
 * @param revenueCents         revenue from paid customers, in cents
 * @param averageSatisfaction  average recorded satisfaction of paid
 *                             customers, empty if nobody has paid
 * @param paidCount            number of customers who have paid
 * @param servedCount          number of dishes served so far
 * @param departures           number of customers who left unhappy
 * @param waitingQueue         customers waiting for a table, oldest first
 * @param tables               all tables, in number order
 */
public record GameSnapshot(int turn, int maxTurns, int revenueCents,
                           OptionalDouble averageSatisfaction, int paidCount,
                           int servedCount, int departures,
                           List<CustomerView> waitingQueue,
                           List<TableView> tables) {
}
