package interfaces;

/**
 * Interface defining completion logic contract
 * Demonstrates the use of interfaces in Java
 *
 * Any class implementing this interface must provide
 * methods to check and mark completion status
 *
 * Interfaces provide a contract that classes must follow,
 * enabling polymorphism and loose coupling
 */
public interface Completable {

    /**
     * Check if the item is completed
     * @return true if completed, false otherwise
     */
    boolean isCompleted();

    /**
     * Mark the item as completed
     * @return true if successfully marked as completed
     */
    boolean markAsCompleted();

    /**
     * Get the completion percentage
     * @return percentage value between 0 and 100
     */
    double getCompletionPercentage();

    /**
     * Get the completion status as a string
     * @return String representation of status
     */
    String getCompletionStatus();
}