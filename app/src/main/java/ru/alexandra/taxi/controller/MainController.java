package ru.alexandra.taxi.controller;

import ru.alexandra.taxi.view.LocationType;
import ru.alexandra.taxi.view.MainView;

/**
 * @author Anton Vlasov - whalemare
 * @since 2019
 */
public class MainController {

    private MainView view;

    public void attach(MainView view) {
        this.view = view;
    }

    /**
     * Пользователь хочет выбрать точку отправления или назначения
     * @param type тип, либо точка назначения либо отправления
     */
    public void onClickSelectLocation(LocationType type) {
        isAttach(() -> {
            view.showSelectLocationView(type);
        });
    }

    /**
     * Пользователь хочет закать такси
     */
    public void onClickOrder() {

    }

    public void detach() {
        this.view = null;
    }

    /**
     * Проверяем существует наша View или уже умерла
     * и если существует то выполняем блок кода определенный в functino
     */
    protected void isAttach(Runnable function){
        if (view != null) {
            function.run();
        }
    }
}
