package ru.alexandra.taxi.model;

/**
 * @author Anton Vlasov - whalemare
 * @since 2019
 */
public class Driver {
    String name;
    String carName;

    public Driver(String name, String carName) {
        this.name = name;
        this.carName = carName;
    }

    public String getName() {
        return name;
    }

    public String getCarName() {
        return carName;
    }
}
