package yada.cli;

import yada.database.FoodDatabase;
import yada.diet.CalorieTargetStrategy;
import yada.diet.DietProfile;
import yada.diet.Gender;
import yada.diet.ActivityLevel;
import yada.diet.HarrisBenedictStrategy;
import yada.diet.MifflinStJeorStrategy;
import yada.model.BasicFood;
import yada.model.CompositeFoodItem;
import yada.model.Food;
import yada.model.FoodServing;
import yada.logging.DailyFoodLog;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Command-line interface for the Yet Another Diet Assistant (YADA) application.
 * Provides interactive menu-driven interaction for managing foods and diet tracking.
 */
public class YadaCLI {
    private final Scanner scanner;
    private final FoodDatabase foodDatabase;
    private DietProfile userProfile;
    private CalorieTargetStrategy calorieStrategy;
    private List<CalorieTargetStrategy> availableStrategies;
    private final DailyFoodLog dailyFoodLog;
    private LocalDate currentlySelectedDate;


    /**
     * Constructs the CLI and initializes necessary components.
     */
    public YadaCLI() {
        scanner = new Scanner(System.in);
        foodDatabase = new FoodDatabase();
        dailyFoodLog = new DailyFoodLog();
        currentlySelectedDate = LocalDate.now();
        
        // Initialize available calorie calculation strategies
        availableStrategies = Arrays.asList(
            new HarrisBenedictStrategy(),
            new MifflinStJeorStrategy()
        );
        
        // Default to first strategy
        calorieStrategy = availableStrategies.get(0);
    }

    /**
     * Starts the main application loop.
     */
    public void start() {
        setupUserProfile();
        
        while (true) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ", 1, 8);
            
            switch (choice) {
                case 1 -> manageFoods();
                case 2 -> searchFoods();
                case 3 -> createCompositeFood();
                case 4 -> updateDietProfile();
                case 5 -> viewCalorieTarget();
                case 6 -> saveDatabase();
                case 7 -> {
                    System.out.println("Exiting YADA. Goodbye!");
                    return;
                }
                case 8 -> foodLoggingMenu();
            }
        }
    }

    /**
     * Displays the main menu options.
     */
    private void displayMainMenu() {
        System.out.println("\n--- YADA: Yet Another Diet Assistant ---");
        System.out.println("1. Manage Foods");
        System.out.println("2. Search Foods");
        System.out.println("3. Create Composite Food");
        System.out.println("4. Update Diet Profile");
        System.out.println("5. View Daily Calorie Target");
        System.out.println("6. Save Database");
        System.out.println("7. Exit");
        System.out.println("8. Food Logging");
    }

    /**
     * Provides a submenu for food management.
     */
    private void manageFoods() {
        while (true) {
            System.out.println("\n--- Food Management ---");
            System.out.println("1. Add Basic Food");
            System.out.println("2. List Basic Foods");
            System.out.println("3. Return to Main Menu");
            
            int choice = getIntInput("Enter your choice: ", 1, 3);
            
            switch (choice) {
                case 1 -> addBasicFood();
                case 2 -> listBasicFoods();
                case 3 -> {
                    return;
                }
            }
        }
    }

    /**
     * Adds a new basic food to the database.
     */
    private void addBasicFood() {
        System.out.println("\n--- Add Basic Food ---");
        
        // Get food identifier
        String identifier = getStringInput("Enter food identifier (e.g., 'Whole Milk'): ");
        
        // Get keywords
        String keywordsInput = getStringInput("Enter search keywords (comma-separated): ");
        List<String> keywords = Arrays.stream(keywordsInput.split(","))
            .map(String::trim)
            .collect(Collectors.toList());
        
        // Get calories
        double calories = getDoubleInput("Enter calories per serving: ", 0, 10000);
        
        // Create and add food
        BasicFood newFood = new BasicFood(identifier, keywords, calories);
        try {
            foodDatabase.addBasicFood(newFood);
            System.out.println("Food added successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Lists all basic foods in the database.
     */
    private void listBasicFoods() {
        System.out.println("\n--- Basic Foods ---");
        // Implementation would depend on how foods are stored in the database
        System.out.println("Function to be implemented");
    }

    /**
     * Searches for foods based on keywords.
     */
    private void searchFoods() {
        System.out.println("\n--- Search Foods ---");
        String keywordsInput = getStringInput("Enter search keywords (comma-separated): ");
        List<String> keywords = Arrays.stream(keywordsInput.split(","))
            .map(String::trim)
            .collect(Collectors.toList());
        
        boolean matchAll = getYesNoInput("Match ALL keywords? (Y/N): ");
        
        List<Food> results = foodDatabase.searchFoods(keywords, matchAll);
        
        if (results.isEmpty()) {
            System.out.println("No foods found.");
            return;
        }
        
        System.out.println("\nSearch Results:");
        for (int i = 0; i < results.size(); i++) {
            Food food = results.get(i);
            System.out.printf("%d. %s (Calories: %.2f)\n", 
                i + 1, food.getIdentifier(), food.getCaloriesPerServing());
        }
    }

    /**
     * Creates a new composite food from existing foods.
     */
    private void createCompositeFood() {
        System.out.println("\n--- Create Composite Food ---");
        
        // Get composite food identifier
        String identifier = getStringInput("Enter composite food name: ");
        
        // Get keywords
        String keywordsInput = getStringInput("Enter search keywords (comma-separated): ");
        List<String> keywords = Arrays.stream(keywordsInput.split(","))
            .map(String::trim)
            .collect(Collectors.toList());
        
        // Create list of food servings
        List<FoodServing> components = new ArrayList<>();
        
        while (true) {
            // Search for foods to add
            System.out.println("\nSearch for a food to add to the composite:");
            String searchInput = getStringInput("Enter search keywords (or 'done' to finish): ");
            
            if (searchInput.equalsIgnoreCase("done")) {
                break;
            }
            
            List<String> searchKeywords = Arrays.stream(searchInput.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
            
            List<Food> searchResults = foodDatabase.searchFoods(searchKeywords, false);
            
            if (searchResults.isEmpty()) {
                System.out.println("No foods found.");
                continue;
            }
            
            // Display search results
            System.out.println("Search Results:");
            for (int i = 0; i < searchResults.size(); i++) {
                Food food = searchResults.get(i);
                System.out.printf("%d. %s (Calories: %.2f)\n", 
                    i + 1, food.getIdentifier(), food.getCaloriesPerServing());
            }
            
            // Select food
            int foodIndex = getIntInput("Enter the number of the food to add: ", 1, searchResults.size()) - 1;
            Food selectedFood = searchResults.get(foodIndex);
            
            // Get number of servings
            double servings = getDoubleInput("Enter number of servings: ", 0, 100);
            
            // Add to components
            components.add(new FoodServing(selectedFood, servings));
        }
        
        // Create and add composite food
        if (!components.isEmpty()) {
            CompositeFoodItem compositeFood = new CompositeFoodItem(identifier, keywords, components);
            foodDatabase.addCompositeFood(compositeFood);
            System.out.println("Composite food created successfully!");
        } else {
            System.out.println("No components added. Composite food creation cancelled.");
        }
    }

    /**
     * Sets up initial user profile.
     */
    private void setupUserProfile() {
        System.out.println("\n--- Initial User Profile Setup ---");
        
        // Gender selection
        Gender gender = getGenderInput();
        
        // Height input
        double height = getDoubleInput("Enter height (in cm): ", 50, 250);
        
        // Weight input
        double weight = getDoubleInput("Enter weight (in kg): ", 20, 500);
        
        // Age input
        int age = getIntInput("Enter age: ", 1, 120);
        
        // Activity level selection
        ActivityLevel activityLevel = getActivityLevelInput();
        
        // Create profile
        userProfile = new DietProfile(gender, weight, height, age, activityLevel);
    }

    /**
     * Updates the user's diet profile.
     */
    private void updateDietProfile() {
        System.out.println("\n--- Update Diet Profile ---");
        
        while (true) {
            System.out.println("1. Weight");
            System.out.println("2. Height");
            System.out.println("3. Age");
            System.out.println("4. Activity Level");
            System.out.println("5. Calorie Calculation Strategy");
            System.out.println("6. Return to Main Menu");
            
            int choice = getIntInput("Enter your choice: ", 1, 6);
            
            switch (choice) {
                case 1 -> userProfile.setWeight(getDoubleInput("Enter new weight (kg): ", 20, 500));
                case 2 -> userProfile.setHeight(getDoubleInput("Enter new height (cm): ", 50, 250));
                case 3 -> userProfile.setAge(getIntInput("Enter new age: ", 1, 120));
                case 4 -> userProfile.setActivityLevel(getActivityLevelInput());
                case 5 -> selectCalorieStrategy();
                case 6 -> {
                    return;
                }
            }
            
            System.out.println("Profile updated successfully!");
        }
    }
    private void foodLoggingMenu() {
        while (true) {
            System.out.println("\n--- Food Logging ---");
            System.out.printf("Current Date: %s\n", currentlySelectedDate);
            System.out.println("1. Add Food to Log");
            System.out.println("2. View Daily Log");
            System.out.println("3. Remove Food from Log");
            System.out.println("4. Undo Last Action");
            System.out.println("5. Change Selected Date");
            System.out.println("6. View Calorie Summary");
            System.out.println("7. Return to Main Menu");
            
            int choice = getIntInput("Enter your choice: ", 1, 7);
            
            switch (choice) {
                case 1 -> logFood();
                case 2 -> viewDailyLog();
                case 3 -> removeFoodFromLog();
                case 4 -> undoLastLogAction();
                case 5 -> changeSelectedDate();
                case 6 -> viewCalorieSummary();
                case 7 -> {
                    return;
                }
            }
        }
    }
    /**
     * Logs a food item to the current date's log.
     */
    private void logFood() {
        // Search for food
        System.out.println("\n--- Log Food ---");
        String searchInput = getStringInput("Enter search keywords for food: ");
        
        List<Food> searchResults = foodDatabase.searchFoods(
            List.of(searchInput.split(",")), false);
        
        if (searchResults.isEmpty()) {
            System.out.println("No foods found.");
            return;
        }
        
        // Display search results
        System.out.println("Search Results:");
        for (int i = 0; i < searchResults.size(); i++) {
            Food food = searchResults.get(i);
            System.out.printf("%d. %s (Calories: %.2f)\n", 
                i + 1, food.getIdentifier(), food.getCaloriesPerServing());
        }
        
        // Select food
        int foodIndex = getIntInput("Enter the number of the food to log: ", 1, searchResults.size()) - 1;
        Food selectedFood = searchResults.get(foodIndex);
        
        // Get number of servings
        double servings = getDoubleInput("Enter number of servings: ", 0, 100);
        
        // Create and add food serving to log
        FoodServing foodServing = new FoodServing(selectedFood, servings);
        dailyFoodLog.addFoodServing(currentlySelectedDate, foodServing);
        
        System.out.println("Food logged successfully!");
    }
    /**
     * Views the log for the currently selected date.
     */
    private void viewDailyLog() {
        List<FoodServing> dayLog = dailyFoodLog.getFoodServingsForDate(currentlySelectedDate);
        
        if (dayLog.isEmpty()) {
            System.out.println("No foods logged for this date.");
            return;
        }
        
        System.out.printf("\n--- Food Log for %s ---\n", currentlySelectedDate);
        double totalCalories = 0;
        
        for (int i = 0; i < dayLog.size(); i++) {
            FoodServing serving = dayLog.get(i);
            double calories = serving.getFood().getCaloriesPerServing() * serving.getServings();
            
            System.out.printf("%d. %s - %.2f servings (%.2f calories)\n", 
                i + 1, 
                serving.getFood().getIdentifier(), 
                serving.getServings(), 
                calories);
            
            totalCalories += calories;
        }
        
        System.out.printf("\nTotal Calories: %.2f\n", totalCalories);
        
        // If a calorie target is set, show the difference
        if (userProfile != null && calorieStrategy != null) {
            double target = calorieStrategy.calculateDailyTarget(userProfile);
            double difference = target - totalCalories;
            
            System.out.printf("Calorie Target: %.2f\n", target);
            System.out.printf("Calories %s Target: %.2f\n", 
                difference >= 0 ? "Below" : "Above", 
                Math.abs(difference));
        }
    }
    /**
     * Removes a food item from the current date's log.
     */
    private void removeFoodFromLog() {
        List<FoodServing> dayLog = dailyFoodLog.getFoodServingsForDate(currentlySelectedDate);
        
        if (dayLog.isEmpty()) {
            System.out.println("No foods logged for this date.");
            return;
        }
        
        // Display current log
        System.out.printf("\n--- Food Log for %s ---\n", currentlySelectedDate);
        for (int i = 0; i < dayLog.size(); i++) {
            FoodServing serving = dayLog.get(i);
            System.out.printf("%d. %s - %.2f servings\n", 
                i + 1, 
                serving.getFood().getIdentifier(), 
                serving.getServings());
        }
        
        // Select food to remove
        int index = getIntInput("Enter the number of the food to remove: ", 1, dayLog.size()) - 1;
        
        FoodServing servingToRemove = dayLog.get(index);
        
        if (dailyFoodLog.removeFoodServing(currentlySelectedDate, servingToRemove)) {
            System.out.println("Food removed from log successfully!");
        } else {
            System.out.println("Failed to remove food from log.");
        }
    }
    /**
     * Undoes the last logged action.
     */
    private void undoLastLogAction() {
        if (dailyFoodLog.undo()) {
            System.out.println("Last action undone successfully!");
        } else {
            System.out.println("No actions to undo.");
        }
    }

    /**
     * Changes the currently selected date for logging.
     */
    private void changeSelectedDate() {
        while (true) {
            try {
                String dateInput = getStringInput("Enter date (YYYY-MM-DD): ");
                currentlySelectedDate = LocalDate.parse(dateInput);
                System.out.println("Date changed successfully!");
                return;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }
    }
    /**
     * Views a summary of calorie intake over a date range.
     */
    private void viewCalorieSummary() {
        System.out.println("\n--- Calorie Summary ---");
        
        // Get start date
        LocalDate startDate = getDateInput("Enter start date (YYYY-MM-DD): ");
        
        // Get end date
        LocalDate endDate = getDateInput("Enter end date (YYYY-MM-DD): ");
        
        // Ensure start date is before or equal to end date
        if (startDate.isAfter(endDate)) {
            System.out.println("Start date must be before or equal to end date.");
            return;
        }
        
        // Get calorie summary
        Map<LocalDate, Double> calorieSummary = 
            dailyFoodLog.getCalorieSummary(startDate, endDate);
        
        if (calorieSummary.isEmpty()) {
            System.out.println("No logs found in the specified date range.");
            return;
        }
        
        System.out.println("\nCalorie Summary:");
        calorieSummary.forEach((date, calories) -> 
            System.out.printf("%s: %.2f calories\n", date, calories));
        
        // Calculate average if multiple days
        if (calorieSummary.size() > 1) {
            double averageCalories = calorieSummary.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
            
            System.out.printf("\nAverage Daily Calories: %.2f\n", averageCalories);
        }
    }
    /**
     * Helper method to get a date input from the user.
     * 
     * @param prompt message to display
     * @return parsed LocalDate
     */
    private LocalDate getDateInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String dateInput = scanner.nextLine().trim();
                return LocalDate.parse(dateInput);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }
    }


    /**
     * Allows user to select a calorie calculation strategy.
     */
    private void selectCalorieStrategy() {
        System.out.println("\n--- Select Calorie Calculation Strategy ---");
        
        for (int i = 0; i < availableStrategies.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, availableStrategies.get(i).getStrategyName());
        }
        
        int choice = getIntInput("Select strategy: ", 1, availableStrategies.size());
        calorieStrategy = availableStrategies.get(choice - 1);
        
        System.out.println("Strategy changed to: " + calorieStrategy.getStrategyName());
    }

    /**
     * Views the daily calorie target based on current profile and strategy.
     */
    private void viewCalorieTarget() {
        if (userProfile == null) {
            System.out.println("Please set up user profile first.");
            return;
        }
        
        double target = calorieStrategy.calculateDailyTarget(userProfile);
        
        System.out.println("\n--- Daily Calorie Target ---");
        System.out.printf("Calculation Method: %s\n", calorieStrategy.getStrategyName());
        System.out.printf("Daily Calorie Target: %.2f calories\n", target);
    }

    /**
     * Saves the current food database.
     */
    private void saveDatabase() {
        foodDatabase.saveDatabase();
        dailyFoodLog.saveLogs();
        System.out.println("Database saved successfully!");
    }

    // Input helper methods

    /**
     * Gets a valid integer input within specified range.
     * @param prompt message to display
     * @param min minimum allowed value
     * @param max maximum allowed value
     * @return validated integer input
     */
    private int getIntInput(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int input = Integer.parseInt(scanner.nextLine().trim());
                
                if (input >= min && input <= max) {
                    return input;
                }
                
                System.out.printf("Please enter a number between %d and %d.\n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    /**
     * Gets a validated double input within specified range.
     * @param prompt message to display
     * @param min minimum allowed value
     * @param max maximum allowed value
     * @return validated double input
     */
    private double getDoubleInput(String prompt, double min, double max) {
        while (true) {
            try {
                System.out.print(prompt);
                double input = Double.parseDouble(scanner.nextLine().trim());
                
                if (input >= min && input <= max) {
                    return input;
                }
                
                System.out.printf("Please enter a number between %.2f and %.2f.\n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    /**
     * Gets a non-empty string input.
     * @param prompt message to display
     * @return validated string input
     */
    private String getStringInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            
            if (!input.isEmpty()) {
                return input;
            }
            
            System.out.println("Input cannot be empty.");
        }
    }

    /**
     * Gets a yes/no input from the user.
     * @param prompt message to display
     * @return true for yes, false for no
     */
    private boolean getYesNoInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toUpperCase();
            
            if (input.equals("Y")) return true;
            if (input.equals("N")) return false;
            
            System.out.println("Please enter Y or N.");
        }
    }

    /**
     * Gets gender input from the user.
     * @return selected gender
     */
    private Gender getGenderInput() {
        while (true) {
            System.out.println("Select Gender:");
            System.out.println("1. Male");
            System.out.println("2. Female");
            
            int choice = getIntInput("Enter your choice: ", 1, 2);
            
            return choice == 1 ? Gender.MALE : Gender.FEMALE;
        }
    }

    /**
     * Gets activity level input from the user.
     * @return selected activity level
     */
    private ActivityLevel getActivityLevelInput() {
        System.out.println("Select Activity Level:");
        ActivityLevel[] levels = ActivityLevel.values();
        
        for (int i = 0; i < levels.length; i++) {
            System.out.printf("%d. %s\n", i + 1, 
                levels[i].toString().replace("_", " "));
        }
        
        int choice = getIntInput("Enter your choice: ", 1, levels.length);
        return levels[choice - 1];
    }

    /**
     * Main method to launch the YADA application.
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        YadaCLI cli = new YadaCLI();
        cli.start();
    }
}