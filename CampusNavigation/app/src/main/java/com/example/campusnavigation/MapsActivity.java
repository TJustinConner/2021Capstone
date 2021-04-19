package com.example.campusnavigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.lang.Math;

import static java.sql.DriverManager.println;


/*
Currently this code can:
find the current locaiton of the user
find the closest point between a set point and other points on campus
draw a line from your current location to the one you are going to
*/


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {
    private static final String TAG = "MapsActivity";


    int Location_Request_CODE = 10001;
    double Longitude;
    double Latitude;
    int Coord_number = 0;

    Polyline polylineFinal;
    PolylineOptions polylineOptions;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationrequest;

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult){
            if(locationResult == null){
                return;
            }
            for(Location location: locationResult.getLocations()){
                //this method updates location to keep it current
                Longitude = location.getLongitude();
                Latitude = location.getLatitude();

            }

        }
    };


    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1*2500;



    private GoogleMap mMap;
    int buttonChooser = 0;//used to show which button type you are on Satellite or

    List<Marker> AllMarkers = new ArrayList<Marker>(); //Holds all the markers in the map
    //default
    //each time you update the map this needs to be updataed as well
    //make sure it matches the matrix in the python program
    int[][] Matrix = new int[440][440];
    double[][] Coords = new double[440][2];
    List<Polyline> polylines = new ArrayList<Polyline>();

    // List<Integer> my_paths = new ArrayList<Integer>();
///////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        int i = 0;


        InputStream is = getResources().openRawResource(R.raw.paths);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        String line = "";

        try {
            List<String[]> temp = new ArrayList<String[]>();
            while ((line = reader.readLine()) != null) {
                // String[] token = line.split(",");
                temp.add((line.split(",")));
                //            Log.i("test","at least it prints: "+token[332].length());
                Log.i("test", "i: " + i);

                i++;
            }
            // Log.i("matrix output: ","correct output?"+temp.size());
            String[][] array = new String[temp.size()][0];
            temp.toArray(array);
/*
            Log.i("matrix output: ","correct output?"+temp.size());
            String[][]array = new String[temp.size()][0];
            temp.toArray(array);
            */
            for (int row = 0; row < 440; row++) {
                for (int column = 0; column < 440; column++) {
                    Matrix[row][column] = Integer.parseInt(array[row][column]);
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationrequest = LocationRequest.create();
        locationrequest.setInterval(2000);
        locationrequest.setFastestInterval(2000);
        locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Spinner spinner = findViewById(R.id.maps_Spinners);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Locations, android.R.layout.simple_dropdown_item_1line);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


    }//onCreate



    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) { //if app permissions for using location is granted
            //  getLastLocation();//this gets the last location of the phone

            checksettingsAndStartLocationUpdates();
        } else {
            askLocationPermission();
        }
    }//onStart

    @Override
    protected void onPause() {
        super.onPause();
        stoplocationupdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startlocationupdates();

    }

    private void checksettingsAndStartLocationUpdates() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder().addLocationRequest(locationrequest).build();

        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //make sure that settings on the device are met
                //start location updates
                //call the location updates method
                startlocationupdates();

            }
        });

        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //settings are no good
                //ask user for input
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(MapsActivity.this, 1001);
                    } catch (IntentSender.SendIntentException sendIntentException) {
                        sendIntentException.printStackTrace();
                    }

                }

            }
        });
    }


    private void startlocationupdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //  fusedLocationProviderClient.requestLocationUpdates(locationrequest,locationCallback, Looper.getMainLooper());

        fusedLocationProviderClient.requestLocationUpdates(locationrequest,locationCallback, Looper.myLooper());


    }
    private void stoplocationupdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.i("make alert diolog", "diologe");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Location_Request_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Location_Request_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Location_Request_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //granted
                // getLastLocation();
                checksettingsAndStartLocationUpdates();
            } else {
                //not granted
            }
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location !=null){
                    //we get location here

                }
                else{
                    //location null...
                }
            }
        });
        locationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("error_cant find","cannot get location");
            }
        });

    }

    public static class DijkstrasAlgorithm {

        private static final int NO_PARENT = -1;

        // Function that implements Dijkstra's
        // single source shortest path
        // algorithm for a graph represented
        // using adjacency matrix
        // representation
        private static List<Integer> my_paths = new ArrayList<Integer>();

        private static void dijkstra(int[][] adjacencyMatrix,
                                     int startVertex, int ender) {

            int nVertices = adjacencyMatrix[0].length;

            // shortestDistances[i] will hold the
            // shortest distance from src to i
            int[] shortestDistances = new int[nVertices];

            // added[i] will true if vertex i is
            // included / in shortest path tree
            // or shortest distance from src to
            // i is finalized
            boolean[] added = new boolean[nVertices];

            // Initialize all distances as
            // INFINITE and added[] as false
            for (int vertexIndex = 0; vertexIndex < nVertices;
                 vertexIndex++) {
                shortestDistances[vertexIndex] = Integer.MAX_VALUE;
                added[vertexIndex] = false;
            }

            // Distance of source vertex from
            // itself is always 0
            shortestDistances[startVertex] = 0;

            // Parent array to store shortest
            // path tree
            int[] parents = new int[nVertices];

            // The starting vertex does not
            // have a parent
            parents[startVertex] = NO_PARENT;

            // Find shortest path for all
            // vertices
            for (int i = 1; i < nVertices; i++) {

                // Pick the minimum distance vertex
                // from the set of vertices not yet
                // processed. nearestVertex is
                // always equal to startNode in
                // first iteration.
                int nearestVertex = -1;
                int shortestDistance = Integer.MAX_VALUE;
                for (int vertexIndex = 0;
                     vertexIndex < nVertices;
                     vertexIndex++) {
                    if (!added[vertexIndex] &&
                            shortestDistances[vertexIndex] <
                                    shortestDistance) {
                        nearestVertex = vertexIndex;
                        shortestDistance = shortestDistances[vertexIndex];
                    }
                }

                // Mark the picked vertex as
                // processed
                added[nearestVertex] = true;

                // Update dist value of the
                // adjacent vertices of the
                // picked vertex.
                for (int vertexIndex = 0;
                     vertexIndex < nVertices;
                     vertexIndex++) {
                    int edgeDistance = adjacencyMatrix[nearestVertex][vertexIndex];

                    if (edgeDistance > 0
                            && ((shortestDistance + edgeDistance) <
                            shortestDistances[vertexIndex])) {
                        parents[vertexIndex] = nearestVertex;
                        shortestDistances[vertexIndex] = shortestDistance +
                                edgeDistance;
                    }
                }
            }

            printSolution(startVertex, shortestDistances, parents, ender);
        }

        // A utility function to print
        // the constructed distances
        // array and shortest paths

        private static void clear_All() {
            my_paths.clear();
        }

        private static void printSolution(int startVertex,
                                          int[] distances,
                                          int[] parents, int ender) {

            printPath(ender, parents);

        }

        // Function to print shortest path
        // from source to currentVertex
        // using parents array
        public static void printPath(int currentVertex,
                                     int[] parents) {

            // Base case : Source node has
            // been processed

            if (currentVertex == NO_PARENT) {
                return;
            }
            printPath(parents[currentVertex], parents);
            Log.i("test: ", "works: " + currentVertex + " ");
            my_paths.add(currentVertex);
            // System.out.print(currentVertex + " ");


        }


    }

    //////////////////////////////////////////


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
///////////////////////////////////
        InputStream is = getResources().openRawResource(R.raw.coords);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is,Charset.forName("UTF-8"))
        );
        String line = "";

        try {
            List<String[]> temp = new ArrayList<String[]>();
            while ((line = reader.readLine()) != null) {
                // String[] token = line.split(",");
                temp.add((line.split(",")));
                //            Log.i("test","at least it prints: "+token[332].length());
                //  Log.i("test","i: "+i);


            }
            // Log.i("matrix output: ","correct output?"+temp.size());
            String[][]array = new String[temp.size()][0];
            temp.toArray(array);
/*
            Log.i("matrix output: ","correct output?"+temp.size());
            String[][]array = new String[temp.size()][0];
            temp.toArray(array);
            */
            for(int row=0;row<440;row++){
                for(int column=0; column<2;column++){
                    Coords[row][column] = Double.parseDouble(array[row][column]);
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }


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
                // onPause();

            }
            else{
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                buttonChooser = 0;
                // onResume();

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

    private void RemoveAll(){
        for(Marker mark: AllMarkers){
            mark.remove();
        }
        for(Polyline line: polylines){
            line.remove();
        }
        polylines.clear();
    }

    private double GetDistance(double tlat1,double tlon1){

        double Lat1 = java.lang.Math.toRadians(tlat1);
        double Lon1 = java.lang.Math.toRadians(tlon1);
        double Lat2 = java.lang.Math.toRadians(Latitude);
        double Lon2 = java.lang.Math.toRadians(Longitude);

        double radius = 6373.0;

        double dlon = Lon2 - Lon1;
        double dlat = Lat2 - Lat1;

        double a = java.lang.Math.pow(java.lang.Math.sin(dlat/2),2)
                + java.lang.Math.cos(Lat1) * java.lang.Math.cos(Lat2)
                * java.lang.Math.pow(java.lang.Math.sin(dlon/2),2);
        double c = 2 * java.lang.Math.atan2( java.lang.Math.sqrt(a),java.lang.Math.sqrt(1 - a));



        return (radius * c);
    }

    private int GetClosest(){
        double distance=900000000;
        int counter = 0;
        for(int x=0;x<440;x++){
            if(GetDistance(Coords[x][1],Coords[x][0])<distance){
                counter = x;
                distance = GetDistance(Coords[x][1],Coords[x][0]);
            }
        }
        return counter;
    }
    private void makeToast(final int stoper){
        if(Coord_number !=0 && Coord_number != 289){
            handler.postDelayed(runnable = new Runnable() {
                @Override
                public void run() {
                    Context context = getApplicationContext();
                    CharSequence text = "Hello toast!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    killLines();
                    makePaths(stoper);


                    handler.postDelayed(runnable,delay);
                }
            }, delay);

        }
        if(stoper == 289){
            handler.removeCallbacks(runnable);
        }

    }

    private void killLines(){
        for(Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }

    private void makePaths(int Coord_number) {

        onPause();
        DijkstrasAlgorithm path = new DijkstrasAlgorithm();
        path.dijkstra(Matrix, GetClosest(), Coord_number); //actually returns values for coords

        if(Coord_number !=0) {

            polylines.add(this.mMap.addPolyline(new PolylineOptions().color(Color.RED).clickable(true).add(
                    new LatLng(Coords[GetClosest()][1], Coords[GetClosest()][0]),
                    new LatLng(Latitude, Longitude))));

        }
        onResume();


        for (int start = 1; start < path.my_paths.size(); start++) {

            polylines.add(this.mMap.addPolyline(new PolylineOptions().color(Color.RED).clickable(true).add(
                    new LatLng(Coords[path.my_paths.get(start - 1)][1], Coords[path.my_paths.get(start - 1)][0]),
                    new LatLng(Coords[path.my_paths.get(start)][1], Coords[path.my_paths.get(start)][0]))));

        }

        path.clear_All();
    }

    public void display(String name){
        //This was terrible to implement
        //the problem is that you have to have each of the the
        //Coordinates has to be entered/ found manually anyway so
        //at that point why not just copy past if statements and fill in the info
        // manually... may have been a better solution but hey this is all
        //the GPS coordinates for all the main locations on campus so have fun with that.poly

        RemoveAll();//this eliminates the all the nodes each time a new node is added so that only one node is displayed at a time



        if( name.contains("Commonwealth Hall")) {
            LatLng marker = new LatLng(36.973036, -82.561149);
            Marker mark = mMap.addMarker(new MarkerOptions().position(marker).title("Common Wealth"));
            Coord_number =289;
            AllMarkers.add(mark);
        }

        if( name.contains("David J. Prior Convocation Center")) {
            LatLng marker = new LatLng(36.97615639865157, -82.56536704840991);
            Marker mark = mMap.addMarker(new MarkerOptions().position(marker).title("David J. Prior Convocation Center"));
            AllMarkers.add(mark);
            Coord_number = 193;
        }

        if( name.contains("Humphreys-Thomas Field House")) {
            LatLng marker = new LatLng(36.97551553576881, -82.56493038860793);
            Marker mark =  mMap.addMarker(new MarkerOptions().position(marker).title("Humphreys-Thomas Field House"));
            AllMarkers.add(mark);
            // Coord_number = 190;
        }
        if( name.contains("Carl Smith Stadium")) {
            LatLng marker = new LatLng(36.97497554901266, -82.56437013934703);
            Marker mark =  mMap.addMarker(new MarkerOptions().position(marker).title("Carl Smith Stadium"));
            AllMarkers.add(mark);
            Coord_number = 270;
        }
        if( name.contains("Ramseyer Press Box")) {
            LatLng marker = new LatLng(36.975232686029756, -82.56379346442196);
            Marker mark =  mMap.addMarker(new MarkerOptions().position(marker).title("Ramseyer Press Box"));
            AllMarkers.add(mark);
            Coord_number = 227;
        }
        if( name.contains("Culberston Hall")) {
            LatLng marker = new LatLng(36.97251, -82.56269);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Culberston Hall"));
            AllMarkers.add(mark);
            Coord_number = 92;
        }
        if( name.contains("College Relations/Napoleon Hill Foundation")) {
            LatLng marker = new LatLng(36.97283367105798, -82.56201908233115);
            Marker mark =  mMap.addMarker(new MarkerOptions().position(marker).title("College Relations/Napoleon Hill Foundation"));
            AllMarkers.add(mark);
            Coord_number = 104;
        }
        if( name.contains("Lila Vicars Smith House")) {
            LatLng marker = new LatLng(36.975204799790255, -82.5581445499958);
            Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Lila Vicars Smith House"));
            AllMarkers.add(mark);
            Coord_number =377;
        }
        if( name.contains("McCrary Hall")) {
            LatLng marker = new LatLng(36.97121534957083, -82.5619257479437);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("McCrary Hall"));
            AllMarkers.add(mark);
            Coord_number = 1;
        }
        if( name.contains("Gilliam Center for the Arts")) {
            LatLng marker = new LatLng(36.972080459181335, -82.56051463210983);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Gilliam Center for the Arts"));
            AllMarkers.add(mark);
            Coord_number = 130;
        }
        if( name.contains("Hunter J. Smith Dining Commons")) {
            LatLng marker = new LatLng(36.97243199745305, -82.56017324262561);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Hunter J. Smith Dining Commons"));
            AllMarkers.add(mark);
            Coord_number = 132;
        }
        if( name.contains("Thompson Hall")) {
            LatLng marker = new LatLng(36.97282049431341, -82.55976229125751);
            Marker mark =  mMap.addMarker(new MarkerOptions().position(marker).title("Thompson Hall"));
            AllMarkers.add(mark);
            Coord_number = 135;
        }
        if( name.contains("Asbury Hall")) {
            LatLng marker = new LatLng(36.97274149636506, -82.55916581699721);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Asbury Hall"));
            AllMarkers.add(mark);
            Coord_number = 139;
        }
        if( name.contains("Martha Randolph Hall")) {
            LatLng marker = new LatLng(36.97354682270125, -82.55909096561946);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Martha Randolph Hall"));
            AllMarkers.add(mark);
            Coord_number = 305;
        }
        if( name.contains("Crocket Hall (Admissions Office)")) {
            LatLng marker = new LatLng(36.97055790648928, -82.56066188458206);
            Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Crocket Hall (Admissions Office)"));
            AllMarkers.add(mark);
            Coord_number = 77;
        }
        if( name.contains("Cantrell Hall")) {
            LatLng marker = new LatLng(36.97129507307334, -82.56021663787666);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Cantrell Hall"));
            AllMarkers.add(mark);
            Coord_number = 308;
        }

        if( name.contains("Chapel of all Faiths")) {
            LatLng marker = new LatLng(36.97096077748099, -82.55998060349206);
            Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Chapel of all Faiths"));
            AllMarkers.add(mark);
            Coord_number = 171;
        }
        if( name.contains("Henson Hall")) {
            LatLng marker = new LatLng(36.97204937568239, -82.55935833102355);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Henson Hall"));
            AllMarkers.add(mark);
            Coord_number = 143;
        }
        if( name.contains("Smiddy Hall")) {
            LatLng marker = new LatLng(36.970227893533405, -82.55995378137858);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Smiddy Hall"));
            AllMarkers.add(mark);
            Coord_number = 18;
        }
        if( name.contains("C. Bascom Slemp Student Center")) {
            LatLng marker = new LatLng(36.970583621705494, -82.55972847582962);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("C. Bascom Slemp Student Center"));
            AllMarkers.add(mark);
            Coord_number = 56;
        }
        if( name.contains("Wyllie Hall")) {
            LatLng marker = new LatLng(36.97070791188466, -82.55911156776746);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Wyllie Hall"));
            AllMarkers.add(mark);
            Coord_number = 49;
        }

        if( name.contains("UVA-Wise Library")) {
            LatLng marker = new LatLng(36.971457934490424, -82.55971238256465);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("UVA-Wise Library"));
            AllMarkers.add(mark);
            Coord_number = 168;
        }
        if( name.contains("Zehmer Hall")) {
            LatLng marker = new LatLng(36.97117506968736, -82.55869314316784);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Zehmer Hall"));
            AllMarkers.add(mark);
            Coord_number = 317;
        }
        if( name.contains("Darden Hall")) {
            LatLng marker = new LatLng(36.971655082060316, -82.55906328799826);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Darden Hall"));
            AllMarkers.add(mark);
            Coord_number = 318;
        }
        if( name.contains("Leonard W. Sandridge Science Center")) {
            LatLng marker = new LatLng(36.97175365565509, -82.55802795535666);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Leonard W. Sandridge Science Center"));
            AllMarkers.add(mark);
            Coord_number = 324;
        }
        if( name.contains("Betty J. Gilliam Sculpture Garden")) {
            LatLng marker = new LatLng(36.97163793881761, -82.5585590327151);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Betty J. Gilliam Sculpture Garden"));
            AllMarkers.add(mark);
            Coord_number = 319;
        }
        if( name.contains("Physical Plant")) {
            LatLng marker = new LatLng(36.97442645746456, -82.55656261417435);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Physical Plant"));
            AllMarkers.add(mark);
            Coord_number =351;
        }
        if( name.contains("Winston Ely Health and Wellness Center")) {
            LatLng marker = new LatLng(36.97034582909863, -82.55937144085671);
            Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Winston Ely Health and Wellness Center"));
            AllMarkers.add(mark);
            Coord_number = 31;
        }
        if( name.contains("Bower-Sturgill Hall")) {
            LatLng marker = new LatLng(36.96999374950577, -82.55859710648123);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Bower-Sturgill Hall"));
            AllMarkers.add(mark);
            Coord_number = 35;
        }
        if( name.contains("Stallard Field (Baseball)")) {
            LatLng marker = new LatLng(36.96911941990102, -82.55809285121471);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Stallard Field (Baseball)"));
            AllMarkers.add(mark);
            Coord_number = 67;
        }

        if( name.contains("Fred B. Greear Gym and Swimming Pool")) {
            LatLng marker = new LatLng(36.96980945537668, -82.55743302781167);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Common Wealth"));
            AllMarkers.add(mark);
            Coord_number = 42;
        }

        if( name.contains("Humphreys Tennis Complex")) {
            LatLng marker = new LatLng(36.97135665502713, -82.55685903510364);
            Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Humphreys Tennis Complex"));
            AllMarkers.add(mark);
            Coord_number = 65;
        }

        if( name.contains("Women's Softball Field")) {
            LatLng marker = new LatLng(36.97092689771323, -82.55571754268155);
            Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Women's Softball Field"));
            AllMarkers.add(mark);
            Coord_number = 68;
        }
        if( name.contains("Intramural Sports")) {
            LatLng marker = new LatLng(36.972652570498916, -82.55382265627702);
            Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Intramural Sports"));
            AllMarkers.add(mark);
            Coord_number =361;
        }
        if( name.contains("Observatory")) {
            LatLng marker = new LatLng(36.97053447205514, -82.55175752072537);
            Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Observatory"));
            AllMarkers.add(mark);
            Coord_number =374;
        }

        if( name.contains("Resource Center")) {
            LatLng marker = new LatLng(36.97223, -82.56573);
            Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Resource Center"));
            AllMarkers.add(mark);
            Coord_number =409;
        }

        if( name.contains("Center for Teaching Excellence")) {
            LatLng marker = new LatLng(36.97091576054845, -82.56418429612896);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Center for Teaching Excellence"));
            AllMarkers.add(mark);
            Coord_number =403;
        }
        if( name.contains("Wesley Foundation")) {
            LatLng marker = new LatLng(36.96981869149186, -82.56350910361056);
            Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Wesley Foundation"));
            AllMarkers.add(mark);
            Coord_number =424;
        }

        if( name.contains("Alumni Hall")) {
            LatLng marker = new LatLng(36.96961564106247, -82.56187436766942);
            Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Alumni Hall"));
            AllMarkers.add(mark);
            Coord_number =398;
        }

        makeToast(Coord_number);
        if(Coord_number != 0 ) {
            makePaths(Coord_number);
        }
        /*
            Polyline polyline2 = mMap.addPolyline(new PolylineOptions().clickable(true).add(
                    new LatLng(Coords[start][1], Coords[start][0]),
                    new LatLng(Coords[start+1][1], Coords[start+1][0])));
            // new LatLng(36.97101392838035, -82.56078921697508)));
            */



    }//display



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