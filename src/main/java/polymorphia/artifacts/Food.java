package polymorphia.artifacts;

public class Food extends Artifact {

    public Food(String name, double healthValue) {
        super(name, healthValue);
    }

    public double getHealthValue() {
        return strength;
    }

    @Override
    public String toString() {
        String formattedHealth = String.format("%.2f", getHealthValue());
        return name + "(" + formattedHealth + ")";
    }

}
