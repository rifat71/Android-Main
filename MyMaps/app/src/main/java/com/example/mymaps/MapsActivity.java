package com.example.mymaps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.android.rides.RideRequestButtonCallback;
import com.uber.sdk.android.rides.RideRequestView;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.rides.client.error.ApiError;
import com.uber.sdk.rides.client.model.RideEstimate;

import java.util.Arrays;

import static android.os.Build.VERSION_CODES.M;

public
class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener{

    GoogleMap mMap;
    Button search_button;
    TextView search_context,destination;
    Marker marker,marker2;
    Polyline polyline;
    AutocompleteFilter typeFilter;
    //RideRequestButton rideRequestButton;

    LatLng common,source,des,user;

    @Override
    protected
    void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        search_button=findViewById(R.id.search_btn);
        search_context=findViewById(R.id.search_context);
        destination=findViewById(R.id.destination);
      //  rideRequestButton=findViewById(R.id.uber);

        ///MapSupport
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        search_button.setOnClickListener(this);
        search_context.setOnClickListener(this);
        destination.setOnClickListener(this);
    }

    @Override
    public
    void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);
        } else {
            // Toast.makeText(this,"pacchena",Toast.LENGTH_LONG).show();
            mMap.setMyLocationEnabled(true);

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            if (location != null) {
                //Toast.makeText(this,"GOT it",Toast.LENGTH_LONG).show();

                user=new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
                //search_context.setText(locationManager.getLastKnownLocation(location.));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(user)
                        .zoom(18)
                        .bearing(0)
                        .tilt(0)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                marker=mMap.addMarker(new MarkerOptions().position(user).title("Your Location"));

            }
        }
    }



    @RequiresApi(api = M)
    @Override
    public
    void onClick(View v) {

        if(v==search_button){

            if(polyline!=null){polyline.remove();}
            if(source!=null && des!=null) {
                polyline = mMap.addPolyline(new PolylineOptions()
                        .add(source, des)
                        .width(15)
                        .color(Color.GREEN));

               // rideRequestButton.setVisibility(View.VISIBLE);
            }


            SessionConfiguration config = new SessionConfiguration.Builder()
                    // mandatory
                    .setClientId("<fgcU0Gy9NPV9FwTV1vmkLD3uGD0DfHm8>")
                    // required for enhanced button features
                    .setServerToken("<mmKqGZPssZ6fZq2FXle_DUxpVC0DeqF7YzOVuF6_>")
                    // required for implicit grant authentication
                    //.setRedirectUri("<>")
                    .setScopes(Arrays.asList(Scope.RIDE_WIDGETS))
                    // optional: set sandbox as operating environment
                    .setEnvironment(SessionConfiguration.Environment.SANDBOX)
                    .build();
            UberSdk.initialize(config);

            RideRequestButton requestButton = new RideRequestButton(MapsActivity.this);
            LinearLayout layout =  findViewById(R.id.hold_uber);
            layout.addView(requestButton);

             RideParameters rideParams = new RideParameters.Builder()
                    .setProductId("a1111c8c-c720-46c3-8534-2fcdd730040d")
                    .setDropoffLocation(des.latitude, des.longitude, "Drop off", "D")
                    .setPickupLocation(source.latitude, source.longitude, "Pick Up", "S")
                    .build();
            requestButton.setRideParameters(rideParams);




            ServerTokenSession session = new ServerTokenSession(config);
            requestButton.setSession(session);

            //layout.addView(requestButton.loadRideInformation(this));
            requestButton.loadRideInformation();

            RideRequestButtonCallback callback = new RideRequestButtonCallback() {

                @Override
                public void onRideInformationLoaded() {
                    // react to the displayed estimates

                }

                @Override
                public void onError(ApiError apiError) {
                    // API error details: /docs/riders/references/api#section-errors
                }

                @Override
                public void onError(Throwable throwable) {
                    // Unexpected error, very likely an IOException
                }
            };
            requestButton.setCallback(callback);

        }

        //////////////////////////////////////////////////// SOURCE TextField
        if(v==search_context){
           // Toast.makeText(this,"FROM",Toast.LENGTH_LONG).show();

             typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                    .setCountry("BD")
                    .build();
            Intent intent = null;
            try {
                intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                        .setFilter(typeFilter)
                        .build(this);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
            startActivityForResult(intent, 1);
            //search_context.setText("Your location");

        }

        if(v==destination){

           // Toast.makeText(this,"TO",Toast.LENGTH_LONG).show();

             typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                    .setCountry("BD")
                    .build();
            Intent intent = null;
            try {
                intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                        .setFilter(typeFilter)
                        .build(this);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
            startActivityForResult(intent, 2);

        }

    }

    @Override
    protected
    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Place place = PlaceAutocomplete.getPlace(this, data);
        final LatLng latLng=place.getLatLng();
        common=latLng;

        if(requestCode==1){
            search_context.setText(place.getName().toString());
            if(marker!=null){marker.remove();}
            if(polyline!=null){polyline.remove();}
            source=common;
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(source)
                    .zoom(13)
                    .bearing(0)
                    .tilt(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            marker=mMap.addMarker(new MarkerOptions().position(source).title(place.getName().toString()));
        }
        else if(requestCode==2){
            destination.setText(place.getName().toString());
            if(marker2!=null){marker2.remove();}
            if(polyline!=null){polyline.remove();}
            des=common;
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(des)
                    .zoom(13)
                    .bearing(0)
                    .tilt(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            marker2=mMap.addMarker(new MarkerOptions().position(des).title(place.getName().toString()));
        }

    }

}
