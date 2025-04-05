package yada.logging;

import yada.model.FoodServing;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages daily food logs, tracking food consumption and providing 
 * comprehensive logging and analysis capabilities.
 */
public class DailyFoodLog {
    // Stores daily logs, with date as key and list of food servings as value
    private final Map<LocalDate, List<FoodServing>> dailyLogs;
    
    // Command history for undo functionality
    private final Deque<LogCommand> commandHistory;
    
    // File for persistent storage of logs
    private static final String LOG_FILE = "daily_food_logs.txt";

    /**
     * Constructs a new DailyFoodLog and loads existing logs.
     */
    public DailyFoodLog() {
        dailyLogs = new HashMap<>();
        commandHistory = new LinkedList<>();
        loadLogs();
    }

    /**
     * Represents a command in the food log for undo functionality.
     */
    private interface LogCommand {
        void execute();
        void undo();
    }

    /**
     * Command for adding a food serving to the log.
     */
    private class AddFoodCommand implements LogCommand {
        private final LocalDate date;
        private final FoodServing foodServing;

        public AddFoodCommand(LocalDate date, FoodServing foodServing) {
            this.date = date;
            this.foodServing = foodServing;
        }

        @Override
        public void execute() {
            dailyLogs.computeIfAbsent(date, k -> new ArrayList<>()).add(foodServing);
        }

        @Override
        public void undo() {
            List<FoodServing> dayLog = dailyLogs.get(date);
            if (dayLog != null) {
                dayLog.remove(foodServing);
                if (dayLog.isEmpty()) {
                    dailyLogs.remove(date);
                }
            }
        }
    }

    /**
     * Command for removing a food serving from the log.
     */
    private class RemoveFoodCommand implements LogCommand {
        private final LocalDate date;
        private final FoodServing foodServing;
        private final int index;

        public RemoveFoodCommand(LocalDate date, FoodServing foodServing, int index) {
            this.date = date;
            this.foodServing = foodServing;
            this.index = index;
        }

        @Override
        public void execute() {
            List<FoodServing> dayLog = dailyLogs.get(date);
            if (dayLog != null) {
                dayLog.remove(foodServing);
                if (dayLog.isEmpty()) {
                    dailyLogs.remove(date);
                }
            }
        }

        @Override
        public void undo() {
            dailyLogs.computeIfAbsent(date, k -> new ArrayList<>())
                .add(index, foodServing);
        }
    }

    /**
     * Adds a food serving to the log for a specific date.
     * 
     * @param date date of consumption
     * @param foodServing food serving to log
     */
    public void addFoodServing(LocalDate date, FoodServing foodServing) {
        AddFoodCommand command = new AddFoodCommand(date, foodServing);
        command.execute();
        commandHistory.push(command);
    }

    /**
     * Removes a specific food serving from a date's log.
     * 
     * @param date date of log
     * @param foodServing food serving to remove
     * @return true if removal was successful, false otherwise
     */
    public boolean removeFoodServing(LocalDate date, FoodServing foodServing) {
        List<FoodServing> dayLog = dailyLogs.get(date);
        if (dayLog != null) {
            int index = dayLog.indexOf(foodServing);
            if (index != -1) {
                RemoveFoodCommand command = new RemoveFoodCommand(date, foodServing, index);
                command.execute();
                commandHistory.push(command);
                return true;
            }
        }
        return false;
    }

    /**
     * Undoes the last command in the log.
     * 
     * @return true if an action was undone, false if no actions to undo
     */
    public boolean undo() {
        if (!commandHistory.isEmpty()) {
            LogCommand lastCommand = commandHistory.pop();
            lastCommand.undo();
            return true;
        }
        return false;
    }

    /**
     * Calculates total calories consumed on a specific date.
     * 
     * @param date date to calculate calories for
     * @return total calories consumed
     */
    public double calculateTotalCalories(LocalDate date) {
        List<FoodServing> dayLog = dailyLogs.get(date);
        if (dayLog == null) {
            return 0.0;
        }
        
        return dayLog.stream()
            .mapToDouble(serving -> 
                serving.getFood().getCaloriesPerServing() * serving.getServings())
            .sum();
    }

    /**
     * Retrieves all food servings for a specific date.
     * 
     * @param date date to retrieve logs for
     * @return list of food servings, or empty list if no logs exist
     */
    public List<FoodServing> getFoodServingsForDate(LocalDate date) {
        return dailyLogs.getOrDefault(date, Collections.emptyList());
    }

    /**
     * Saves the current food logs to a persistent storage file.
     */
    public void saveLogs() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE))) {
            for (Map.Entry<LocalDate, List<FoodServing>> entry : dailyLogs.entrySet()) {
                LocalDate date = entry.getKey();
                for (FoodServing serving : entry.getValue()) {
                    writer.println(String.format("%s|%s|%f", 
                        date.toString(), 
                        serving.getFood().getIdentifier(), 
                        serving.getServings()));
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving food logs: " + e.getMessage());
        }
    }

    /**
     * Loads food logs from persistent storage.
     */
    private void loadLogs() {
        dailyLogs.clear();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    try {
                        LocalDate date = LocalDate.parse(parts[0]);
                        // Note: In a full implementation, you'd need to retrieve the actual Food object 
                        // from your food database. This is a simplification.
                        // Food food = foodDatabase.findFoodByIdentifier(parts[1]);
                        // FoodServing serving = new FoodServing(food, Double.parseDouble(parts[2]));
                        // addFoodServing(date, serving);
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing log entry: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading food logs: " + e.getMessage());
        }
    }

    /**
     * Generates a summary of food consumption for a given date range.
     * 
     * @param startDate start of date range
     * @param endDate end of date range
     * @return summary of calories consumed per day
     */
    public Map<LocalDate, Double> getCalorieSummary(LocalDate startDate, LocalDate endDate) {
        return dailyLogs.entrySet().stream()
            .filter(entry -> 
                !entry.getKey().isBefore(startDate) && 
                !entry.getKey().isAfter(endDate))
            .collect(Collectors.toMap(
                Map.Entry::getKey, 
                entry -> entry.getValue().stream()
                    .mapToDouble(serving -> 
                        serving.getFood().getCaloriesPerServing() * serving.getServings())
                    .sum()
            ));
    }
}