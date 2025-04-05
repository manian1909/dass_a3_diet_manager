package yada.model;

import java.util.List;
import java.util.Objects;

/**
 * Represents a composite food made up of multiple food items.
 */
public class CompositeFoodItem implements Food {
    private final String identifier;
    private final List<String> keywords;
    private final List<FoodServing> components;

    /**
     * Constructs a new composite food item.
     * @param identifier unique name for the composite food
     * @param keywords list of search keywords
     * @param components list of food servings that make up this composite
     */
    public CompositeFoodItem(String identifier, List<String> keywords, List<FoodServing> components) {
        this.identifier = Objects.requireNonNull(identifier, "Identifier cannot be null");
        this.keywords = Objects.requireNonNull(keywords, "Keywords cannot be null");
        this.components = Objects.requireNonNull(components, "Components cannot be null");
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public List<String> getKeywords() {
        return keywords;
    }
    public List<FoodServing> getComponents() {
        return components;
    }

    @Override
    public double getCaloriesPerServing() {
        return components.stream()
            .mapToDouble(serving -> 
                serving.getFood().getCaloriesPerServing() * serving.getServings())
            .sum();
    }
}
