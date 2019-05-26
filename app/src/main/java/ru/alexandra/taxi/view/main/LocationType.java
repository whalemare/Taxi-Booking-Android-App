package ru.alexandra.taxi.view.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;

import ru.alexandra.taxi.tukla.Place;

import static android.app.Activity.RESULT_OK;

/**
 * @author Anton Vlasov - whalemare
 * @since 2019
 */
public enum LocationType {
    FROM {
        @Override
        void selectLocation(Activity activity) throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            activity.startActivityForResult(builder.build(activity), FROM.ordinal());
        }
    },
    TO {
        @Override
        void selectLocation(Activity activity) throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            activity.startActivityForResult(builder.build(activity), TO.ordinal());
        }
    };

    abstract void selectLocation(Activity activity) throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException;

    Place extractLocation(Context context, int requestCode, int resultCode, Intent data) {
        if (requestCode == FROM.ordinal() || requestCode == TO.ordinal()) {
            if (resultCode == RESULT_OK) {
                com.google.android.gms.location.places.Place place = PlacePicker.getPlace(context, data);
                return new Place(
                    place.getName().toString(),
                    place.getAddress().toString(),
                    String.valueOf(place.getRating())
                );
            }
        }
        return null;
    }
}
