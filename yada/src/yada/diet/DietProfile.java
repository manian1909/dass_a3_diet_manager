package yada.diet;
import java.io.Serializable;
/**
 * Represents a user's diet profile for calorie calculations.
 */
public class DietProfile implements Serializable {
    private Gender gender;
    private double weight;     // in kg
    private double height;     // in cm
    private int age;
    private ActivityLevel activityLevel;

    // Constructor, getters, and setters
    public DietProfile(Gender gender, double weight, double height, 
                       int age, ActivityLevel activityLevel) {
        this.gender = gender;
        this.weight = weight;
        this.height = height;
        this.age = age;
        this.activityLevel = activityLevel;
    }

    // Getters and setters (omitted for brevity)
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public ActivityLevel getActivityLevel() { return activityLevel; }
    public void setActivityLevel(ActivityLevel activityLevel) { this.activityLevel = activityLevel; }
}