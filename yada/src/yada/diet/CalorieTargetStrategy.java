package yada.diet;

import java.io.Serializable;

/**
 * Strategy interface for calculating daily calorie targets.
 * Allows for easy addition of new calculation methods.
 */
public interface CalorieTargetStrategy extends Serializable {
    /**
     * Calculates the daily calorie target based on user profile.
     * @param profile user's diet profile
     * @return recommended daily calorie intake
     */
    double calculateDailyTarget(DietProfile profile);

    /**
     * Gets the name of this calculation strategy.
     * @return strategy name
     */
    String getStrategyName();
}
