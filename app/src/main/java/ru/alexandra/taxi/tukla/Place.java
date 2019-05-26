package ru.alexandra.taxi.tukla;

/**
 * Created by Lenovo on 10/23/2017.
 */

public class Place {

    private String primaryText, addressDescription, distance;

    public Place(String primaryText, String addressDescription, String distance) {
        this.primaryText = primaryText;
        this.addressDescription = addressDescription;
        this.distance = distance;
    }


    public String getPrimaryText() {
        return primaryText;
    }

    public String getAddressDescription() {
        return addressDescription;
    }

    public String getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return getPrimaryText() + ", " + getAddressDescription();
    }
}
