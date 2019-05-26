package ru.alexandra.taxi.model;

import android.util.Pair;

import java.util.Random;

/**
 * @author Anton Vlasov - whalemare
 * @since 2019
 */
public class MainInteractor {
    public int calculatePrice(Pair<Place, Place> userRoute) {
        int price = new Random().nextInt(100);
        Place from = userRoute.first;
        Place to = userRoute.second;
        if (to != null) {
            price += to.getPrimaryText().length();
        }
        if (from != null) {
            price += from.getPrimaryText().length();
        }
        price *= 2;
        return price;
    }
}
