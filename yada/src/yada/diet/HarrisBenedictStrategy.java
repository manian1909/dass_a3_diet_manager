package yada.diet;


/**
 * Harris-Benedict Equation for Basal Metabolic Rate (BMR) calculation.
 */
public class HarrisBenedictStrategy implements CalorieTargetStrategy {
    @Override
    public double calculateDailyTarget(DietProfile profile) {
        double bmr;
        
        // Harris-Benedict Equation
        if (profile.getGender() == Gender.MALE) {
            bmr = 88.362 + 
                  (13.397 * profile.getWeight()) + 
                  (4.799 * profile.getHeight()) - 
                  (5.677 * profile.getAge());
        } else {
            bmr = 447.593 + 
                  (9.247 * profile.getWeight()) + 
                  (3.098 * profile.getHeight()) - 
                  (4.330 * profile.getAge());
        }
        
        // Apply activity level multiplier
        return bmr * getActivityMultiplier(profile.getActivityLevel());
    }

    private double getActivityMultiplier(ActivityLevel level) {
        return switch (level) {
            case SEDENTARY -> 1.2;
            case LIGHTLY_ACTIVE -> 1.375;
            case MODERATELY_ACTIVE -> 1.55;
            case VERY_ACTIVE -> 1.725;
            case EXTRA_ACTIVE -> 1.9;
        };
    }

    @Override
    public String getStrategyName() {
        return "Harris-Benedict Equation";
    }
}

