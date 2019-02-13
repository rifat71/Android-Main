package com.example.mymaps;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.android.rides.RideRequestButtonCallback;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.rides.client.error.ApiError;
import com.uber.sdk.rides.client.model.RideEstimate;

import java.util.Arrays;

import static android.content.ContentValues.TAG;
import static com.example.mymaps.MapsActivity.des;
import static com.example.mymaps.MapsActivity.dropoff_location;
import static com.example.mymaps.MapsActivity.pickup_location;
import static com.example.mymaps.MapsActivity.source;

public
class PopUp extends Activity {

    int count=0;

    @Override
    protected
    void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);

        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width=dm.widthPixels;
        int height=dm.heightPixels;

        getWindow().setLayout((int)(width*.9),(int)(height*.4));


        // rideRequestButton.setVisibility(View.VISIBLE);
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



        final RideRequestButton requestButton = new RideRequestButton(PopUp.this);
        LinearLayout layout = findViewById(R.id.hold_uber);
        //RelativeLayout popup_window = findViewById(R.id.mainPopUp);
        //popup_window.getScrollX();
        if (count == 0) {
            layout.addView(requestButton);
        }

        /////////////////   CODE of Request send   ///////////////////



        ////////////////////////////////////

        final RideParameters rideParams = new RideParameters.Builder()
                .setProductId("a1111c8c-c720-46c3-8534-2fcdd730040d")
                .setDropoffLocation(des.latitude, des.longitude, dropoff_location, "")
                .setPickupLocation(source.latitude, source.longitude, "My Location", pickup_location)
                .build();
        requestButton.setRideParameters(rideParams);

        ServerTokenSession session = new ServerTokenSession(config);
        requestButton.setSession(session);

        requestButton.loadRideInformation();
    }
}
