package yada.model;

import java.io.Serializable;
import java.util.Objects;
/**
 * Represents a specific serving of a food item.
 */
public class FoodServing implements Serializable {
    private final Food food;
    private final double servings;

    /**
     * Constructs a food serving.
     * @param food the food item
     * @param servings number of servings
     */
    public FoodServing(Food food, double servings) {
        this.food = Objects.requireNonNull(food, "Food cannot be null");
        this.servings = servings;
    }

    /**
     * Gets the food item.
     * @return the food
     */
    public Food getFood() {
        return food;
    }

    /**
     * Gets the number of servings.
     * @return servings count
     */
    public double getServings() {
        return servings;
    }
}