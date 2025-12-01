package interfaces;

/**
 * Contract for objects that support completion tracking.
 */
public interface Completable {
    boolean isCompleted();
    boolean markAsCompleted();
    double getCompletionPercentage();
    String getCompletionStatus();
}