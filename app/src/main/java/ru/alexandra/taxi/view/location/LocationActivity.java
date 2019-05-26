package ru.alexandra.taxi.view.location;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.tukla.www.tukla.R;

import java.util.ArrayList;
import java.util.List;

import ru.alexandra.taxi.controller.LocationController;
import ru.alexandra.taxi.model.Place;


public class LocationActivity extends AppCompatActivity
    implements PlaceSelectionListener,
    LocationView {

    private List<Place> myPlacesList = new ArrayList<>();
    private RecyclerView placesRecyclerView;
    private PlacesAdapter mPlacesAdapter;

    LocationController controller = new LocationController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_auto);
        controller.attach(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_autoLocation);
        setSupportActionBar(toolbar);

        mPlacesAdapter = new PlacesAdapter(myPlacesList);

        placesRecyclerView = (RecyclerView) findViewById(R.id.placesRecyclerView);

        placesRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mlayoutManager = new LinearLayoutManager(getApplicationContext());
        placesRecyclerView.setLayoutManager(mlayoutManager);
        placesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        placesRecyclerView.setAdapter(mPlacesAdapter);
    }

    @Override
    public void onPlaceSelected(com.google.android.gms.location.places.Place place) {

    }

    @Override
    public void onError(Status status) {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

}