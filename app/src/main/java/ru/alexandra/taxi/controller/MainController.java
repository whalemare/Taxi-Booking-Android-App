package ru.alexandra.taxi.controller;

import ru.alexandra.taxi.tukla.Place;
import ru.alexandra.taxi.view.main.LocationType;
import ru.alexandra.taxi.view.main.MainView;

/**
 * @author Anton Vlasov - whalemare
 * @since 2019
 */
public class MainController extends BaseController<MainView> {

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

    }

    public void onLocationSelected(LocationType type, Place place) {
        isAttach(view -> view.showSelectedLocation(type, place));
    }
}
