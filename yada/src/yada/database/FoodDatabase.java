package yada.database;

import yada.model.Food;
import yada.model.BasicFood;
import yada.model.CompositeFoodItem;
import yada.model.FoodServing;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages the food database, handling loading, saving, and searching of foods.
 */
public class FoodDatabase {
    private static final String BASIC_FOODS_FILE = "basic_foods.txt";
    private static final String COMPOSITE_FOODS_FILE = "composite_foods.txt";

    private final Map<String, BasicFood> basicFoods;
    private final Map<String, CompositeFoodItem> compositeFoods;

    /**
     * Constructs a new FoodDatabase and loads existing foods.
     */
    public FoodDatabase() {
        basicFoods = new HashMap<>();
        compositeFoods = new HashMap<>();
        loadDatabase();
    }

    /**
     * Adds a new basic food to the database.
     * @param food the basic food to add
     * @throws IllegalArgumentException if food with same identifier already exists
     */
    public void addBasicFood(BasicFood food) {
        if (basicFoods.containsKey(food.getIdentifier())) {
            throw new IllegalArgumentException("Food already exists: " + food.getIdentifier());
        }
        basicFoods.put(food.getIdentifier(), food);
    }

    /**
     * Adds a new composite food to the database.
     * @param food the composite food to add
     * @throws IllegalArgumentException if food with same identifier already exists
     */
    public void addCompositeFood(CompositeFoodItem food) {
        if (compositeFoods.containsKey(food.getIdentifier())) {
            throw new IllegalArgumentException("Composite food already exists: " + food.getIdentifier());
        }
        compositeFoods.put(food.getIdentifier(), food);
    }

    /**
     * Searches for foods matching all or any of the given keywords.
     * @param keywords list of keywords to search
     * @param matchAll if true, matches foods with ALL keywords; if false, matches ANY keywords
     * @return list of matching foods
     */
    public List<Food> searchFoods(List<String> keywords, boolean matchAll) {
        List<Food> allFoods = new ArrayList<>(basicFoods.values());
        allFoods.addAll(compositeFoods.values());

        return allFoods.stream()
            .filter(food -> matchKeywords(food, keywords, matchAll))
            .collect(Collectors.toList());
    }

    /**
     * Checks if food matches given keywords based on match strategy.
     * @param food food to check
     * @param keywords keywords to match
     * @param matchAll if true, require ALL keywords; if false, require ANY keyword
     * @return true if food matches keywords, false otherwise
     */
    private boolean matchKeywords(Food food, List<String> keywords, boolean matchAll) {
        if (keywords == null || keywords.isEmpty()) {
            return true;
        }

        List<String> foodKeywords = food.getKeywords().stream()
            .map(String::toLowerCase)
            .collect(Collectors.toList());

        List<String> searchKeywords = keywords.stream()
            .map(String::toLowerCase)
            .collect(Collectors.toList());

        if (matchAll) {
            return foodKeywords.containsAll(searchKeywords);
        } else {
            return searchKeywords.stream().anyMatch(foodKeywords::contains);
        }
    }

    /**
     * Loads food database from files.
     */
    private void loadDatabase() {
        loadBasicFoods();
        loadCompositeFoods();
    }

    /**
     * Saves the entire food database.
     */
    public void saveDatabase() {
        saveBasicFoods();
        saveCompositeFoods();
    }

    /**
     * Loads basic foods from file.
     * This is a simplified implementation. In a real-world scenario, 
     * you'd want more robust parsing and error handling.
     */
    private void loadBasicFoods() {
        try (BufferedReader reader = new BufferedReader(new FileReader(BASIC_FOODS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 3) {
                    String identifier = parts[0].trim();
                    List<String> keywords = Arrays.asList(parts[1].split(","));
                    double calories = Double.parseDouble(parts[2]);
                    
                    BasicFood food = new BasicFood(identifier, keywords, calories);
                    basicFoods.put(identifier, food);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading basic foods: " + e.getMessage());
        }
    }

    /**
     * Saves basic foods to file.
     */
    private void saveBasicFoods() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BASIC_FOODS_FILE))) {
            for (BasicFood food : basicFoods.values()) {
                String keywords = String.join(",", food.getKeywords());
                writer.println(String.format("%s|%s|%.2f", 
                    food.getIdentifier(), 
                    keywords, 
                    food.getCaloriesPerServing()));
            }
        } catch (IOException e) {
            System.err.println("Error saving basic foods: " + e.getMessage());
        }
    }

    /**
     * Loads composite foods from file.
     * This is a simplified implementation that would need 
     * to handle more complex scenarios in a real application.
     */
    private void loadCompositeFoods() {
        try (BufferedReader reader = new BufferedReader(new FileReader(COMPOSITE_FOODS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 3) {
                    String identifier = parts[0].trim();
                    List<String> keywords = Arrays.asList(parts[1].split(","));
                    
                    // Parse component foods
                    List<FoodServing> components = new ArrayList<>();
                    String[] componentParts = parts[2].split(";");
                    for (String componentPart : componentParts) {
                        String[] componentDetails = componentPart.split(":");
                        if (componentDetails.length == 2) {
                            String foodId = componentDetails[0];
                            double servings = Double.parseDouble(componentDetails[1]);
                            
                            // Look up the base food
                            Food baseFood = basicFoods.get(foodId);
                            if (baseFood != null) {
                                components.add(new FoodServing(baseFood, servings));
                            }
                        }
                    }
                    
                    CompositeFoodItem food = new CompositeFoodItem(identifier, keywords, components);
                    compositeFoods.put(identifier, food);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading composite foods: " + e.getMessage());
        }
    }

    /**
     * Saves composite foods to file.
     */
    private void saveCompositeFoods() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(COMPOSITE_FOODS_FILE))) {
            for (CompositeFoodItem food : compositeFoods.values()) {
                String keywords = String.join(",", food.getKeywords());
                
                // Convert components to string representation
                String componentsStr = food.getComponents().stream()
                    .map(serving -> serving.getFood().getIdentifier() + ":" + serving.getServings())
                    .collect(Collectors.joining(";"));
                
                writer.println(String.format("%s|%s|%s", 
                    food.getIdentifier(), 
                    keywords, 
                    componentsStr));
            }
        } catch (IOException e) {
            System.err.println("Error saving composite foods: " + e.getMessage());
        }
    }
}
