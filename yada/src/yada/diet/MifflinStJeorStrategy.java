package yada.diet;


/**
 * Mifflin-St Jeor Equation for Basal Metabolic Rate (BMR) calculation.
 */
public class MifflinStJeorStrategy implements CalorieTargetStrategy {
    @Override
    public double calculateDailyTarget(DietProfile profile) {
        double bmr;
        
        // Mifflin-St Jeor Equation
        if (profile.getGender() == Gender.MALE) {
            bmr = (10 * profile.getWeight()) + 
                  (6.25 * profile.getHeight()) - 
                  (5 * profile.getAge()) + 5;
        } else {
            bmr = (10 * profile.getWeight()) + 
                  (6.25 * profile.getHeight()) - 
                  (5 * profile.getAge()) - 161;
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
        return "Mifflin-St Jeor Equation";
    }
}

