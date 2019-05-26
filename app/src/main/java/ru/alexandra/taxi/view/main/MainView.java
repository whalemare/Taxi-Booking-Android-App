package ru.alexandra.taxi.view.main;

import java.util.Calendar;

import ru.alexandra.taxi.model.Driver;
import ru.alexandra.taxi.model.Place;

/**
 * @author Anton Vlasov - whalemare
 * @since 2019
 */
public interface MainView {
    void showSelectLocationView(LocationType type);

    void showSelectedLocation(LocationType type, Place place);

    void showPrice(int price);

    void setOrderEnabled(boolean enable);

    void showOrderTime(Calendar orderTime);

    void showOrderSuccessDialog(Calendar orderTime, Driver driver);
}
