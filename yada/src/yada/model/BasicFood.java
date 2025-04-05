package yada.model;

import java.util.List;
import java.util.Objects;

/**
 * Represents a basic food item with fixed nutritional information.
 */
public class BasicFood implements Food {
    private final String identifier;
    private final List<String> keywords;
    private final double caloriesPerServing;

    /**
     * Constructs a new BasicFood item.
     * @param identifier unique name or identifier for the food
     * @param keywords list of search keywords
     * @param caloriesPerServing calories in one serving
     */
    public BasicFood(String identifier, List<String> keywords, double caloriesPerServing) {
        this.identifier = Objects.requireNonNull(identifier, "Identifier cannot be null");
        this.keywords = Objects.requireNonNull(keywords, "Keywords cannot be null");
        this.caloriesPerServing = caloriesPerServing;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public List<String> getKeywords() {
        return keywords;
    }

    @Override
    public double getCaloriesPerServing() {
        return caloriesPerServing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicFood)) return false;
        BasicFood basicFood = (BasicFood) o;
        return Double.compare(basicFood.caloriesPerServing, caloriesPerServing) == 0 &&
               identifier.equals(basicFood.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, caloriesPerServing);
    }
}
