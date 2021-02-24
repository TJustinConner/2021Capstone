package com.example.google_mapsportion;

import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MapActivity";
    private GoogleMap mMap;
    int buttonChooser = 0;//used to show which button type you are on Satellite or
    //default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }
    LatLng Wise = new LatLng(36.970702412486304, -82.56071600207069); //changes where
    //the map appears
    LatLngBounds UVaWise = new LatLngBounds(
            new LatLng(36.969886859438894, -82.57048866982446),//South west bounds,
            new LatLng(36.97759240185251, -82.54981610344926)//north east bounds

    );


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //lat and long of uva wise va: 36.970702412486304, -82.56071600207069


       // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Uva wise Va"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Wise));
        mMap.setMinZoomPreference(15.5f);//sets the max zoom so you cant zoom out to infinity

        googleMap.setLatLngBoundsForCameraTarget(UVaWise);
    }
    public void Satellite(View view){
        if(view.getId() == R.id.satellite) {
            if (buttonChooser == 0) {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                buttonChooser = 1;
            }
            else{
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                buttonChooser = 0;
            }
        }
    }

    public void ReHome(View view){
        if(view.getId()==R.id.rehome){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(Wise));
        }

    }

    public void Zoom(View view){
        if(view.getId()==R.id.zoomout){
            mMap.animateCamera(CameraUpdateFactory.zoomOut());
        }
        if(view.getId()==R.id.zoomin){
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
        }

    }

}