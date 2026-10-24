package restaurantrush.control;

/**
 * The outcome of one manager choice, such as seating a customer or advancing
 * an order.
 *
 * <p>Controller methods return an {@code ActionResult} instead of printing, so
 * game rules stay out of the user interface. When {@link #ok()} is
 * {@code false}, the staff task has not been used and the UI asks again.</p>
 *
 * @param ok      whether the action was carried out
 * @param message what happened, or why the action was refused
 */
public record ActionResult(boolean ok, String message) {

    /**
     * Creates a successful result.
     *
     * @param message description of what happened
     * @return a result with {@code ok == true}
     */
    public static ActionResult success(String message) {
        return new ActionResult(true, message);
    }

    /**
     * Creates a refused result. The staff task is not used up.
     *
     * @param reason why the action could not be carried out
     * @return a result with {@code ok == false}
     */
    public static ActionResult failure(String reason) {
        return new ActionResult(false, reason);
    }
}
