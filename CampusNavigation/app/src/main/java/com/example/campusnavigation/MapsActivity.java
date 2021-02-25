package com.example.campusnavigation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,AdapterView.OnItemSelectedListener {
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

        Spinner spinner = findViewById(R.id.maps_Spinners);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Locations, android.R.layout.simple_dropdown_item_1line);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

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


        //LatLng commonWealth = new LatLng(36.973036, -82.561149);
       // googleMap.addMarker(new MarkerOptions().position(commonWealth).title("Common Wealth"));
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

    public void display(String name){
        /*
        <item>David J. Prior Convocation Center</item>
        <item>Humphreys-Thomas Field House</item>
        <item>Carl Smith Stadium</item>
        <item>Ramseyer Press Box</item>
        <item>Baptitst Collegiate Ministries</item>
        <item>Culberston Hall</item>
        <item>College Relations/Napoleon Hill Foundation</item>
        <item>Commonwealth Hall</item>
        <item>Lila Vicars Smith House</item>
        <item>McCrary Hall</item>
        <item>Gilliam Center for the Arts</item>
        <item>Hunter J. Smith Dining Commons</item>
        <item>Thompson Hall</item>
        <item>Asbury Hall</item>
        <item>Martha Randolph Hall</item>
        <item>Crocket Hall (Admissions Office)</item>
        <item>Cantrell Hall</item>
        <item>Chapel of all Faiths</item>
        <item>Henson Hall</item>
        <item>Smiddy Hall</item>
        <item>C. Bascom Slemp Student Center</item>
        <item>Wyllie Hall</item>
        <item>UVA-Wise Library</item>
        <item>Zehmer Hall</item>
        <item>Darden Hall</item>
        <item>Leonard W. Sandridge Science Center</item>
        <item>Betty J. Gilliam Sculpture Garden</item>
        <item>Physical Plant</item>
        <item>Winston Ely Health and Wellness Center</item>
        <item>Bower-Sturgill Hall</item>
        <item>Stallard Field (Baseball)</item>
        <item>Fred B. Greear Gym and Swimming Pool</item>
        <item>Humphreys Tennis Complex</item>
        <item>Women\'s Softball Field</item>
                <item>Intramural Sports</item>
        <item>Observatory</item>
        <item>Townhouses</item>
        <item>Resource Center</item>
        <item>Center for Teaching Excellence</item>
        <item>Wesley Foundation</item>
        <item>Alumni Hall</item>
        */
                if( name.contains("Commonwealth Hall")) {
                    LatLng marker = new LatLng(36.973036, -82.561149);
                    mMap.addMarker(new MarkerOptions().position(marker).title("Common Wealth"));
                }
        }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        //Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
        display(text);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}