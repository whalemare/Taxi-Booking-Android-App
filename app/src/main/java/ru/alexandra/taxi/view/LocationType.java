package ru.alexandra.taxi.view;

import android.content.Context;
import android.content.Intent;

import ru.alexandra.taxi.tukla.FindDriver;

/**
 * @author Anton Vlasov - whalemare
 * @since 2019
 */
public enum LocationType {
    FROM {
        @Override
        Intent getIntent(Context context) {
            Intent intent = new Intent(context, FindDriver.class);
            intent.putExtra("type", LocationType.FROM);
            return intent;
        }
    },
    TO {
        @Override
        Intent getIntent(Context context) {
            Intent intent = new Intent(context, FindDriver.class);
            intent.putExtra("type", LocationType.TO);
            return intent;
        }
    };

    abstract Intent getIntent(Context context);
}
