package ru.alexandra.taxi.model;

import android.util.Pair;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * @author Anton Vlasov - whalemare
 * @since 2019
 */
public class MainInteractor {

    List<Driver> drivers = Arrays.asList(
        new Driver("Сергей Баженов", "А123ВВ"),
        new Driver("Елена Онегина", "А001РО"),
        new Driver("Максим Пушкин", "А847ПО"),
        new Driver("Евгения Лукина", "Г113РГ")
    );

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

    public Driver makeOrder(Calendar orderTime) {
        try {
            return drivers.get(new Random().nextInt(drivers.size()));
        } catch (Exception e) {
            return drivers.get(0);
        }
    }
}
