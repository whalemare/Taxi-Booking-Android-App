package ru.alexandra.taxi.view.main;

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
}
