package ru.alexandra.taxi.controller;

import android.util.Pair;

import java.util.Calendar;

import ru.alexandra.taxi.model.Driver;
import ru.alexandra.taxi.model.MainInteractor;
import ru.alexandra.taxi.model.Place;
import ru.alexandra.taxi.view.main.LocationType;
import ru.alexandra.taxi.view.main.MainView;

/**
 * @author Anton Vlasov - whalemare
 * @since 2019
 */
public class MainController extends BaseController<MainView> {

    MainInteractor interactor = new MainInteractor();
    Pair<Place, Place> userRoute = new Pair<>(null, null);
    Calendar orderTime = null;

    /**
     * Пользователь хочет выбрать точку отправления или назначения
     * @param type тип, либо точка назначения либо отправления
     */
    public void onClickSelectLocation(LocationType type) {
        isAttach((view) -> {
            view.showSelectLocationView(type);
        });
    }

    /**
     * Пользователь хочет закать такси
     */
    public void onClickOrder() {
        Driver driver = interactor.makeOrder(orderTime);
        isAttach(view -> {
            view.showOrderSuccessDialog(orderTime, driver);
        });
    }

    public void onLocationSelected(LocationType type, Place place) {
        switch (type) {
            case FROM:
                userRoute = new Pair<>(place, userRoute.second);
                break;
            case TO:
                userRoute = new Pair<>(userRoute.first, place);
                break;
        }
        int price = interactor.calculatePrice(userRoute);
        isAttach(view -> view.showSelectedLocation(type, place));
        isAttach(view -> {
            view.showPrice(price);
            validateButton();
        });
    }

    private void validateButton() {
        isAttach(view -> {
            view.setOrderEnabled(userRoute.first != null & userRoute.second != null && orderTime != null && orderTime.getTimeInMillis() > System.currentTimeMillis());
        });
    }

    public void onSelectTime(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth, hourOfDay, minute);
        orderTime = calendar;
        isAttach(view -> view.showOrderTime(orderTime));
        validateButton();
    }
}
