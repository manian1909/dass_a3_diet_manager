package yada.model;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a base food item with core nutritional information.
 * This interface allows for extensibility of food tracking.
 */
public interface Food extends Serializable {
    /**
     * Gets the unique identifier for this food item.
     * @return the food's identifier
     */
    String getIdentifier();

    /**
     * Gets the list of keywords associated with this food.
     * @return list of search keywords
     */
    List<String> getKeywords();

    /**
     * Calculates the calories per serving for this food.
     * @return calories per serving
     */
    double getCaloriesPerServing();
}