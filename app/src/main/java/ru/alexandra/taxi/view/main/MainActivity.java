package ru.alexandra.taxi.view.main;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.IntentSender;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tukla.www.tukla.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ru.alexandra.taxi.controller.MainController;
import ru.alexandra.taxi.view.location.LocationActivity;

import static com.google.android.gms.location.LocationServices.FusedLocationApi;
import static com.tukla.www.tukla.R.id.map;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener,
    OnMapReadyCallback,
    LoaderManager.LoaderCallbacks<Object>,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener,
    MainView {

    RelativeLayout products_select_option;
    ImageButton select_btn, product1, product2;
    Button buttonLocationFrom;
    Button buttonOrder;
    Button buttonLocationTo;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_CHECK_SETTINGS = 1000;
    private MapFragment mapFragment;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private Location mLastLocation;
    private LocationRequest request;
    View mapView;
    private boolean mRequestingLocationUpdates;
    CameraUpdate cLocation;
    double latitude, longitude;
    Marker now;

    Geocoder geocoder;
    List<android.location.Address> addresses;

    MainController controller = new MainController();
    private LocationType type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupLocationManager();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        controller.attach(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupMap();
        requestLocationPermissions();
        setupNavigationDrawer(toolbar);

        select_btn = (ImageButton) findViewById(R.id.img_selected);
        product1 = (ImageButton) findViewById(R.id.product_type_1_button);
        product2 = (ImageButton) findViewById(R.id.product_type_2_button);
        products_select_option = (RelativeLayout) findViewById(R.id.products_select_option);

        buttonLocationFrom = (Button) findViewById(R.id.buttonLocationFrom);
        buttonLocationTo = (Button) findViewById(R.id.buttonLocationTo);
        buttonOrder = (Button) findViewById(R.id.buttonOrder);

        buttonOrder.setOnClickListener(v -> {
            controller.onClickOrder();
        });
        buttonLocationTo.setOnClickListener(view -> {
            controller.onClickSelectLocation(LocationType.TO);
        });
        buttonLocationFrom.setOnClickListener(view -> {
            controller.onClickSelectLocation(LocationType.FROM);
        });
    }

    /**
     * Выполним настройку управления боковым меню, привязав анимацию открытия к тулбару
     */
    private void setupNavigationDrawer(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Выполним необходимые настройки карты в приложении
     */
    private void setupMap() {
        mapFragment = (MapFragment) getFragmentManager()
            .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
        if (googleApiClient.isConnected()) {
            setInitialLocation();
        }
    }

    @Override
    public void onBackPressed() {
        // Сделаем закрытие бокового меню если оно открыто, когда пользователь нажал кнопку "назад", вместо того чтобы закрыть приложение
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Проверяем включен ли gps
        if (!enabled) {
            buildAlertMessageNoGps();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.map_style));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then over   riding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        //This line will show your current location on Map with GPS dot
        mMap.setMyLocationEnabled(true);
        locationButton();
    }


    private void setupLocationManager() {
        //buildGoogleApiClient();
        if (googleApiClient == null) {

            googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
            //mGoogleApiClient = new GoogleApiClient.Builder(this);
        }
        googleApiClient.connect();
        createLocationRequest();
    }


    protected void createLocationRequest() {
        request = new LocationRequest();
        request.setSmallestDisplacement(10);
        request.setFastestInterval(50000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(3);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
            .addLocationRequest(request);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient,
                builder.build());


        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {

                    case LocationSettingsStatusCodes.SUCCESS:
                        setInitialLocation();
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                MainActivity.this,
                                REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult()", Integer.toString(resultCode));
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK: {

                        setInitialLocation();

                        Toast.makeText(MainActivity.this, "Location enabled", Toast.LENGTH_LONG).show();
                        mRequestingLocationUpdates = true;
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(MainActivity.this, "Location not enabled", Toast.LENGTH_LONG).show();
                        mRequestingLocationUpdates = false;
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }

        if (type != null) {
            ru.alexandra.taxi.tukla.Place place = type.extractLocation(this, requestCode, resultCode, data);
            controller.onLocationSelected(type, place);
            type = null;
        }
    }


    /**
     * Один раз установим начальную позицию
     */
    private void setInitialLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        FusedLocationApi.requestLocationUpdates(googleApiClient, request, location -> {
            mLastLocation = location;
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            MainActivity.this.latitude = lat;
            MainActivity.this.longitude = lng;

            try {
                if (now != null) {
                    now.remove();
                }
                LatLng positionUpdate = new LatLng(MainActivity.this.latitude, MainActivity.this.longitude);
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(positionUpdate, 15);
                now = mMap.addMarker(new MarkerOptions().position(positionUpdate)
                    .title("Your Location"));

                mMap.animateCamera(update);
                //buttonLocationFrom.setText( ""+latitude );


            } catch (Exception ex) {
                ex.printStackTrace();
                Log.e("MapException", ex.getMessage());
            }

            //Geocode current location details
            try {
                geocoder = new Geocoder(MainActivity.this, Locale.ENGLISH);
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                StringBuilder str = new StringBuilder();
                if (Geocoder.isPresent()) {
                    android.location.Address returnAddress = addresses.get(0);

                    String localityString = returnAddress.getAddressLine(0);
                    str.append(localityString);

                    buttonLocationFrom.setText(str);
                    Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                    FusedLocationApi.removeLocationUpdates(googleApiClient, this);
                } else {
                    Toast.makeText(getApplicationContext(), "geocoder not present", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Log.e("tag", e.getMessage());
                Toast.makeText(getApplicationContext(), "Ошибка" + e, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Для андроида выше версии 5 необходимо явно запрашивать разрешение для использования геолокации
     * Сделаем это
     */
    private void requestLocationPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1002);
            } else {
                setupLocationManager();
            }
        } else {
            setupLocationManager();
        }
    }


    /**
     * Обработаем ответ от пользователя, на запрос пермишенов
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1002: {
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        setupLocationManager();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }

    }


    public void getLatLang(String placeId) {
        Places.GeoDataApi.getPlaceById(googleApiClient, placeId)
            .setResultCallback(new ResultCallback<PlaceBuffer>() {
                @Override
                public void onResult(PlaceBuffer places) {
                    if (places.getStatus().isSuccess() && places.getCount() > 0) {
                        final Place place = places.get(0);

                        LatLng latLng = place.getLatLng();
                        try {
                            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                            mMap.animateCamera(update);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Log.e("MapException", ex.getMessage());
                        }

                        Log.i("place", "Place found: " + place.getName());
                    } else {
                        Log.e("place", "Place not found");
                    }
                    places.release();
                }
            });
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    @Override
    public Loader<Object> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object o) {

    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }


    private void locationButton() {
        MapFragment mapFragment = (MapFragment) getFragmentManager()
            .findFragmentById(map);

        View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).
            getParent()).findViewById(Integer.parseInt("2"));
        if (locationButton != null && locationButton.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            // location button is inside of RelativeLayout
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();

            // Align it to - parent BOTTOM|LEFT
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);

            // Update margins, set to 10dp
            final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150,
                getResources().getDisplayMetrics());
            params.setMargins(margin, margin, margin, margin);

            locationButton.setLayoutParams(params);
        }
    }


    //Button Location Search
    public void myLocation(View view) {
        Intent intent = new Intent(MainActivity.this, LocationActivity.class);
        startActivity(intent);
    }


    public void destination(View view) {
        Intent intent = new Intent(MainActivity.this, LocationActivity.class);
        startActivity(intent);
    }


    public void img_selected(View view) {
        products_select_option.setVisibility(View.VISIBLE);
    }

    //Select product option button click
    public void product_type_1_button(View view) {
        products_select_option.setVisibility(View.GONE);

    }

    //Select product option button click
    public void product_type_2_button(View view) {
        products_select_option.setVisibility(View.GONE);

    }

    //Enable Location Button
    private void checkLocaionStatus() {

    }

    protected void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Пожалуйста включите геолокацию")
            .setTitle("GPS выключен")
            .setCancelable(false)
            .setPositiveButton("Да", (dialog, id) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
            .setNegativeButton("Нет", (dialog, id) -> dialog.cancel());
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void showSelectedLocation(LocationType type, ru.alexandra.taxi.tukla.Place place) {
        switch (type) {
            case FROM:
                buttonLocationFrom.setText(place.toString());
                break;
            case TO:
                buttonLocationTo.setText(place.toString());
                break;
        }
    }

    @Override
    public void showSelectLocationView(LocationType type) {
        this.type = type;
        try {
            type.selectLocation(this);
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        }
    }
}
