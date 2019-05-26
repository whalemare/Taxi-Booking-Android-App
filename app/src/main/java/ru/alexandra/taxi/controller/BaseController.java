package ru.alexandra.taxi.controller;

/**
 * @author Anton Vlasov - whalemare
 * @since 2019
 */
public class BaseController<View> {

    private View view;

    public void attach(View view) {
        this.view = view;
    }

    public void detach() {
        this.view = null;
    }

    /**
     * Проверяем существует наша View или уже умерла
     * и если существует то выполняем блок кода определенный в functino
     */
    protected void isAttach(Invokable<View> function){
        if (view != null) {
            function.invoke(view);
        }
    }

    interface Invokable<Item> {
        void invoke(Item view);
    }
}
