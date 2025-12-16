package utils;

import java.util.Scanner;

/**
 * Utility class for input validation
 * Demonstrates validation best practices and error handling

 * This class provides reusable validation methods to ensure
 * data integrity throughout the application
 */
public class ValidationUtils {

    /**
     * Validate that a string is not null or empty
     */
    public static boolean isValidString(String input) {
        return input != null && !input.trim().isEmpty();
    }

    /**
     * Validate email format
     * Simple validation - checks for @ and .
     */
    public static boolean isValidEmail(String email) {
        if (!isValidString(email)) {
            return false;
        }
        return email.contains("@") && email.contains(".");
    }

    /**
     * Validate date format (YYYY-MM-DD)
     */
    public static boolean isValidDate(String date) {
        if (!isValidString(date)) {
            return false;
        }

        // Simple format validation
        String[] parts = date.split("-");
        if (parts.length != 3) {
            return false;
        }

        try {
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            return year > 2000 && year < 2100 &&
                    month >= 1 && month <= 12 &&
                    day >= 1 && day <= 31;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate integer input
     */
    public static boolean isValidInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate positive integer
     */
    public static boolean isValidPositiveInteger(String input) {
        try {
            int value = Integer.parseInt(input);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate choice from menu options
     */
    public static boolean isValidChoice(int choice, int min, int max) {
        return choice >= min && choice <= max;
    }

    /**
     * Validate status value
     */
    public static boolean isValidStatus(String status) {
        if (!isValidString(status)) {
            return false;
        }

        String[] validStatuses = {"Active", "Completed", "On Hold",
                "Pending", "In Progress"};
        for (String validStatus : validStatuses) {
            if (validStatus.equalsIgnoreCase(status)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validate priority value
     */
    public static boolean isValidPriority(String priority) {
        if (!isValidString(priority)) {
            return false;
        }

        String[] validPriorities = {"High", "Medium", "Low"};
        for (String validPriority : validPriorities) {
            if (validPriority.equalsIgnoreCase(priority)) {
                return true;
            }
        }
        return false;
    }
    public static boolean isValidTextField(String input) {

        if(!isValidString(input)) {
            return false;
        }
        String trimmed = input.trim();
        if (trimmed.length()<3 || trimmed.length()>50){
            return false;
        }


        return input.matches(".*[A-Za-z].*");
    }

    public static String getValidatedTextField(Scanner scanner, String prompt, String fieldName) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (!isValidTextField(input)) {
                System.out.println("❌ " + fieldName + " must be at least 3 characters and contain letters (not only numbers).");

            }
        } while (!isValidTextField(input));
        return input;
    }
    /**
     * Get validated string input from user
     */
    public static String getValidatedString(Scanner scanner, String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (!isValidString(input)) {
                System.out.println("❌ Invalid input! Please enter a valid value.");
            }
        } while (!isValidString(input));
        return input;
    }

    /**
     * Get validated integer input from user
     */
    public static int getValidatedInteger(Scanner scanner, String prompt) {
        int value;
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (isValidInteger(input)) {
                value = Integer.parseInt(input);
                break;
            } else {
                System.out.println("❌ Invalid input! Please enter a valid number.");
            }
        }
        return value;
    }

    /**
     * Get validated positive integer input
     */
    public static int getValidatedPositiveInteger(Scanner scanner, String prompt) {
        int value;
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (isValidPositiveInteger(input)) {
                value = Integer.parseInt(input);
                break;
            } else {
                System.out.println("❌ Invalid input! Please enter a positive number.");
            }
        }
        return value;
    }

    /**
     * Get validated date input
     */
    public static String getValidatedDate(Scanner scanner, String prompt) {
        String date;
        do {
            System.out.print(prompt);
            date = scanner.nextLine().trim();
            if (!isValidDate(date)) {
                System.out.println("❌ Invalid date! Use format YYYY-MM-DD.");
            }
        } while (!isValidDate(date));
        return date;
    }

    /**
     * Get validated email input
     */
    public static String getValidatedEmail(Scanner scanner, String prompt) {
        String email;
        do {
            System.out.print(prompt);
            email = scanner.nextLine().trim();
            if (!isValidEmail(email)) {
                System.out.println("❌ Invalid email format!");
            }
        } while (!isValidEmail(email));
        return email;
    }

    /**
     * Get validated choice from menu
     */
    public static int getValidatedChoice(Scanner scanner, String prompt, int min, int max) {
        int choice;
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (isValidInteger(input)) {
                choice = Integer.parseInt(input);
                if (isValidChoice(choice, min, max)) {
                    break;
                } else {
                    System.out.printf("❌ Please enter a number between %d and %d.%n", min, max);
                }
            } else {
                System.out.println("❌ Invalid input! Please enter a number.");
            }
        }
        return choice;
    }
    public static void requireNonEmpty(String input, String fieldName) {
        if (!isValidString(input)) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty.");
        }
    }

    public static void requirePositive(int number, String fieldName) {
        if (number <= 0) {
            throw new IllegalArgumentException(fieldName + " must be a positive integer.");
        }
    }

    public static void requireValidPriority(String priority) {
        if (!isValidPriority(priority)) {
            throw new IllegalArgumentException("Invalid priority value: " + priority);
        }
    }

    public static void requireValidStatus(String status) {
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }
    }
}