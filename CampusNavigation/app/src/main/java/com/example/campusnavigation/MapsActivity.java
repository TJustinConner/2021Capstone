package com.example.campusnavigation;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {
    private static final String TAG = "MapsActivity";

    int locationRequestCode = 10001;    //used as a request code by the program to ensure the user consents to use of location
    double Longitude=-82.5619257479437;    //global variables that hold the coords of the current location of the user
    double Latitude=36.97121534957083;    //global variables that hold the coords of the current location of the user
    int Coord_number = 0;     //used to choose the user's marker

    Handler handler = new Handler(); //handler is used to communicate background used in time updating
    Runnable runnable; //used in creating a thread to run the time updating for path updating/displaying
    int delay = 1*1000; //this is the delay between path generation on the map

    private GoogleMap mMap; //makes a mMap object to be to call GoogleMaps services
    int buttonChooser = 0;//used to show which button type you are on Satellite or

    int[][] Matrix = new int[440][440]; //This holds all of the Matrix between any two nodes
    double[][] Coords = new double[440][2]; //translates each marker to each coord
    List<Polyline> polylines = new ArrayList<Polyline>(); //holds the polylines used when drawing paths on the map
    private static List<Integer> my_paths = new ArrayList<Integer>(); //this will the current path that I am taking it will update every 2 seconds

    //Handicap data
    int[][] handiMatrix = new int[440][440];//will hold the physical places for the handicap layer
    double[][] handiCoords = new double[440][2];//used to translate the outputs from the hani matrix in dijkstra to actual coordinates

    Marker update =null;
    int type = 0;//handi cap or regular

    //used to draw the GEO fence for the wrapper
    LatLng Wise = new LatLng(Latitude, Longitude); //changes where
    // LatLng Wise = new LatLng(36.969886859438894, -82.57048866982446);
    //the map appears
    LatLngBounds UVaWise = new LatLngBounds(
            new LatLng(36.969886859438894, -82.57048866982446),//South west bounds,
            new LatLng(36.97759240185251, -82.54981610344926)//north east bounds
    );


    public Bitmap resizeMapIcons(String iconName,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    //Google play services that allow the user to find the current and accurate location of the user
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationrequest;


    //loop that runs every 2 seconds and updates the position to Longitude and Latitude
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult){
            if(locationResult == null){
                return; // if no location available just return nothing
            }
            for(Location location: locationResult.getLocations()){
                //actually puts the Location object private data into global variables longitude and latitude

                update.remove();
                LatLng place = new LatLng(Latitude, Longitude);
                MarkerOptions current = new MarkerOptions().position(place).title("me").icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("my_marker", 50, 100)));
                update = mMap.addMarker(current);


                Longitude = location.getLongitude();
                Latitude = location.getLatitude();

            }
        }
    };



    //Things in the Oncreate method should be performed once on start up and then never again
    //this information will create coords,Directions ect.
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //opens the paths.csv file to get all the data into a matrix to be used later FOR THE MAIN LAYER
        InputStream is = getResources().openRawResource(R.raw.upaths);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        String line = "";

        //puts each line into the string array (called temp)
        try {

            List<String[]> temp = new ArrayList<String[]>();
            while ((line = reader.readLine()) != null) {

                temp.add((line.split(",")));

            }//while loop


            //makes a 2D array that when placed into the .toarray() method will put the data into the 2D array
            //basically transforms string into 2d adday
            String[][] array = new String[temp.size()][0];
            temp.toArray(array);

            //goes through every row and every column of the input matrix and puts into the
            //global array called Matrix to be used by Dijkstra algorithm
            //to find the shortest path
            //also converts string to integers (this is why all the numbers int the CSV file
            //are so bit, Dijkstra required the use of integers.)
            for (int row = 0; row < 440; row++) {

                for (int column = 0; column < 440; column++) {

                    Matrix[row][column] = Integer.parseInt(array[row][column]);

                }

            }//end of loop

        } catch (Exception e) {
            e.printStackTrace();
        }//if no file is found

        //opens the coords.csv file to get all the data into a matrix to be used later
        InputStream Coord_data = getResources().openRawResource(R.raw.ucoords);
        BufferedReader Coord_reader = new BufferedReader(new InputStreamReader(Coord_data,Charset.forName("UTF-8")));

        //try to open the file and attempt to put data into the 2D matrix
        try {
            //array to read in each line of the csv file
            List<String[]> temp = new ArrayList<String[]>();
            while ((line = Coord_reader.readLine()) != null) {

                temp.add((line.split(",")));

            }//end of while loop


            //string array to hold the values and transfer them into a 2D array
            String[][] Secondarray = new String[temp.size()][0];
            temp.toArray(Secondarray);

            //basicly takes the array and puts them into a 440 by 2 array to represent every node on campus
            for(int row=0;row<440;row++){
                for(int column=0; column<2;column++){

                    Coords[row][column] = Double.parseDouble(Secondarray[row][column]);

                }
            }//for loop

            //catch condition to see if it there is no file
        } catch (Exception e) {
            e.printStackTrace();
        }



        //THis is used to extract the handicap data
        InputStream h_is = getResources().openRawResource(R.raw.hpaths);
        BufferedReader h_reader = new BufferedReader(new InputStreamReader(h_is, Charset.forName("UTF-8")));
        //  String line = "";
        //puts each line into the string array (called temp)
        try {

            List<String[]> temp = new ArrayList<String[]>();
            while ((line = h_reader.readLine()) != null) {

                temp.add((line.split(",")));

            }//while loop


            //makes a 2D array that when placed into the .toarray() method will put the data into the 2D array
            //basically transforms string into 2d adday
            String[][] array = new String[temp.size()][0];
            temp.toArray(array);

            //goes through every row and every column of the input matrix and puts into the
            //global array called Matrix to be used by Dijkstra algorithm
            //to find the shortest path
            //also converts string to integers (this is why all the numbers int the CSV file
            //are so bit, Dijkstra required the use of integers.)
            for (int row = 0; row < 440; row++) {

                for (int column = 0; column < 440; column++) {

                    handiMatrix[row][column] = Integer.parseInt(array[row][column]);

                }

            }//end of loop

        } catch (Exception e) {
            e.printStackTrace();
        }//if no file is found

        //opens the coords.csv file to get all the data into a matrix to be used later
        InputStream Handi_Coord_data = getResources().openRawResource(R.raw.hcoords);
        BufferedReader Handi_Coord_reader = new BufferedReader(new InputStreamReader(Handi_Coord_data,Charset.forName("UTF-8")));

        //try to open the file and attempt to put data into the 2D matrix
        try {
            //array to read in each line of the csv file
            List<String[]> temp = new ArrayList<String[]>();
            while ((line = Handi_Coord_reader.readLine()) != null) {

                temp.add((line.split(",")));

            }//end of while loop


            //string array to hold the values and transfer them into a 2D array
            String[][] h_Secondarray = new String[temp.size()][0];
            temp.toArray(h_Secondarray);

            //basicly takes the array and puts them into a 440 by 2 array to represent every node on campus
            for(int row=0;row<440;row++){
                for(int column=0; column<2;column++){

                    handiCoords[row][column] = Double.parseDouble(h_Secondarray[row][column]);

                }
            }//for loop

            //catch condition to see if it there is no file
        } catch (Exception e) {
            e.printStackTrace();
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);//creates the Location service at start up
        locationrequest = LocationRequest.create(); //creates a location request
        locationrequest.setInterval(2000); //this is the update location request time (2 seconds)
        locationrequest.setFastestInterval(2000); //sets the fastest interval of request updates
        locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); //sets the accuracy requested by the application
        super.onCreate(savedInstanceState);//calls the base case and can pass it to oncreate
        setContentView(R.layout.activity_maps);//Tag refrence

        //used to display the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);//loads a specific section of the map to use
        mapFragment.getMapAsync(this);

        Spinner spinner_main = findViewById(R.id.main_activity_spinner);//unfortunatly to avoid an issue of not being able to click on a spinner twice this must be called.

        ArrayAdapter<CharSequence> adapter_main = ArrayAdapter.createFromResource(this, R.array.Activites, android.R.layout.simple_dropdown_item_1line);
        adapter_main.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner_main.setAdapter(adapter_main);
        //  spinner_main.setSelection(0,false);//will not run SetonItemSelectListener on start up
        spinner_main.setSelection(0);
        spinner_main.setOnItemSelectedListener(this);

    }//onCreate



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId()==R.id.maps_Spinners) {
            String text = parent.getItemAtPosition(position).toString();

        }
        if(parent.getId()==R.id.main_activity_spinner){
            String mytext = parent.getItemAtPosition(position).toString();

            if(mytext.contains("Workouts")) {
                //makes a new spinner object that
                //allows me to call the id more than once (a true blessing and the solution to the most annoying time vampire)
                Spinner spinnerMyList = findViewById(R.id.main_activity_spinner);
                spinnerMyList.setSelection(0);

                //runs the workout activity
                Intent workouts = new Intent(view.getContext(), WorkoutCreation.class);
                startActivity(workouts);
            }

            if(mytext.contains("Log in")) {
                //makes a new spinner object that
                //allows me to call the id more than once (a true blessing and the solution to the most annoying time vampire)
                Spinner spinnerMyList = findViewById(R.id.main_activity_spinner);
                spinnerMyList.setSelection(0);

                //runs the workout activity
                Intent ToLoginActivity = new Intent(view.getContext(), LoginActivity.class);
                startActivity(ToLoginActivity);
            }

            if(mytext.contains("Events")){
                //makes a new spinner object that
                //allows me to call the id more than once (a true blessing and the solution to the most annoying time vampire)
                Spinner spinnerMy = findViewById(R.id.main_activity_spinner);
                spinnerMy.setSelection(0);

                Intent toLogin= new Intent(view.getContext(), LoginActivity.class);
                startActivity(toLogin);
            }

            if(mytext.contains("Navigate")){

                //makes a new spinner object that
                //allows me to call the id more than once (a true blessing and the solution to the most annoying time vampire)
                Spinner spinnerMy = findViewById(R.id.main_activity_spinner);
                spinnerMy.setSelection(0);

                int launch = 1;
                Intent eventSearch = new Intent(this, LocationFinder.class);
                startActivityForResult(eventSearch,launch);

            }

            if(mytext.contains("AR")) {
                //makes a new spinner object that
                //allows me to call the id more than once (a true blessing and the solution to the most annoying time vampire)
                Spinner spinnerMyList = findViewById(R.id.main_activity_spinner);
                spinnerMyList.setSelection(0);

                //runs the AR activity
                Intent AR = new Intent(view.getContext(), ArViewPlacement.class);
                startActivity(AR);
            }

        }
    }

    //this is used to get the location the person wants to travel to
    //it
    @Override
    protected  void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Context context = getApplicationContext();
        String[] array = new String [3];

        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                array[0] = data.getStringExtra("data");
                array[1] = data.getStringExtra("coordinate");
                Coord_number = Integer.valueOf(array[1]);
                repeater(1);

                //this allows for the button to appear only after the
                // location has been chosen.
                //can stop the display of the route
                Button stop_navigation = (Button)findViewById(R.id.stop_navigation); //access the STOP_navigaiton button
                // final Button handicap = (Button)findViewById(R.id.handicap);

                final ImageButton handi = (ImageButton)findViewById(R.id.handi);
                final ImageButton walkie = (ImageButton)findViewById(R.id.walkie);


                if(Coord_number != 0){//works with coord value the coord value is up dated when a value is passed
                    walkie.setVisibility(View.VISIBLE);
                    stop_navigation.setVisibility(View.VISIBLE);//turns the button on


                    handi.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            walkie.setVisibility(View.VISIBLE);
                            v.setVisibility(View.INVISIBLE);

                            repeater(42069);
                            type = 0;
                            repeater(0);

                        }
                    });
                    walkie.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            v.setVisibility(View.INVISIBLE);
                            handi.setVisibility(View.VISIBLE);
                            repeater(42069);
                            type=1;
                            repeater(0);
                        }
                    });


                    stop_navigation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            repeater(42069);//stops the path generation repeater
                            Coord_number = 0; //sets the coord back to zero
                            v.setVisibility(View.INVISIBLE); //turns the button back to invisible
                            handi.setVisibility(View.INVISIBLE);
                            walkie.setVisibility(View.INVISIBLE);
                        }
                    });
                }//when route is activating
            }
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {

    }


    //This is used to ensure that we can actually have permission to access the data
    //asks for the permission to get the fine location of the user
    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) { //if app permissions for using location is granted

            checksettingsAndStartLocationUpdates();//name is self-explanatory
        } else {
            askLocationPermission();//sends a message to ask user for permission
        }
    }//onStart

    //used to pause the location updating service
    //basicly just calls the stop location method
    //this can be called in most functions as this a stand along method
    @Override
    protected void onPause() {
        super.onPause();
        stoplocationupdates();
    }
    //After stopping you may want to resume use this method for doing this
    @Override
    protected void onResume() {
        super.onResume();
        startlocationupdates();
    }
    @Override
    protected void onStop(){
        super.onStop();
        stoplocationupdates();

    }

    //This is literally just making sure the user wants to let the phone have their location
    //if they do not it will ask them for the location
    //This also starts the update location function
    //https://www.youtube.com/watch?v=Ak1O9Gip-pg
    private void checksettingsAndStartLocationUpdates() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder().addLocationRequest(locationrequest).build();//asks for location

        SettingsClient client = LocationServices.getSettingsClient(this);//gets the current settings for the phone

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);//
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //make sure that settings on the device are met
                //start location updates
                //call the location updates method
                startlocationupdates();//actually updates

            }
        });

        //on the off chance that we were rejected this method is called to attempt the user
        //resolve their problem
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
    }//check settings and update

    //the main function that actually asks and sets the current location
    private void startlocationupdates() {
        //this was handled before but if it is not here the program will flip crap so
        //I auto generated it so it would sit down and
        //be calm
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
        //Does the good work of getting the locations from the client
        //sends the data to locationrequest
        //calls the looper which repeats the action over again
        fusedLocationProviderClient.requestLocationUpdates(locationrequest,locationCallback, Looper.myLooper());

    }//startlocationUpdates


    //used by the ONSTOP method to actually stop the
    //recording
    private void stoplocationupdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    //What actually should print out if the application cannot have the permission
    //to use the location services
    //to be completely honest there is just here for niceness
    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, locationRequestCode);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, locationRequestCode);
            }
        }
    }//end of asklocationpermission


    // Function that implements Dijkstra's
    // single source shortest path
    // algorithm for a graph represented
    // using adjacency matrix
    // representation
    //https://www.geeksforgeeks.org/printing-paths-dijkstras-shortest-path-algorithm/
    private static void dijkstra(int[][] adjacencyMatrix,int startVertex, int ender) {
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
        for (int vertexIndex = 0; vertexIndex < nVertices; vertexIndex++) {
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
        parents[startVertex] = -1;

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
            for (int vertexIndex = 0; vertexIndex < nVertices; vertexIndex++) {
                if (!added[vertexIndex] && shortestDistances[vertexIndex] < shortestDistance) {
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
            for (int vertexIndex = 0; vertexIndex < nVertices; vertexIndex++) {
                int edgeDistance = adjacencyMatrix[nearestVertex][vertexIndex];

                if (edgeDistance > 0 && ((shortestDistance + edgeDistance) < shortestDistances[vertexIndex])) {
                    parents[vertexIndex] = nearestVertex;
                    shortestDistances[vertexIndex] = shortestDistance + edgeDistance;
                }
            }
        }

        printSolution(startVertex, shortestDistances, parents, ender);
    }

    //used to destroy all the paths made by the algorithm
    private static void clear_All() {

        my_paths.clear();
    }

    //used with dijkstra to print the special array
    private static void printSolution(int startVertex, int[] distances, int[] parents, int ender) {
        printPath(ender, parents);
    }

    // Function to print shortest path
    // from source to currentVertex
    // using parents array
    //does this recursivly.
    //the path is set up in a sudo 2D matrix where every path in the
    //array is determined by starting at that paths coordient.
    public static void printPath(int currentVertex,
                                 int[] parents) {

        // Base case : Source node has
        // been processed

        if (currentVertex == -1) {
            return;
        }
        printPath(parents[currentVertex], parents);
        my_paths.add(currentVertex);

    }



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

        mMap = googleMap; //sets the map we are going to be using to the object googleMap
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Wise));//set wise in the global
        mMap.setMinZoomPreference(15.5f);//sets the max zoom so you cant zoom out to infinity
        googleMap.setLatLngBoundsForCameraTarget(UVaWise);//sets the bounds for the wrapper

        LatLng place = new LatLng(Latitude, Longitude);
        MarkerOptions current = new MarkerOptions().position(place).title("hello").icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("my_marker", 50, 100)));
        update = mMap.addMarker(current);




        final String[] location_number = getResources().getStringArray(R.array.Location_number);//this holds the values in the string array location_number
        double mini_lat;//places the bulletin board
        double mini_long;//helps to place bulletin board


        //Goes through the entire Array-string extracts the
        //coord number and relates it to latitude and longitude
        //at the same time it is doing that it is also placing the string associated
        //with each marker in the marker as well
        for(int skipper = 1; skipper<77;skipper = skipper +2) {

            //gets the lat and from the other value in the string array
            mini_lat = Coords[Integer.valueOf(location_number[skipper])][1];
            mini_long = Coords[Integer.valueOf(location_number[skipper])][0];

            //makes a new latlong
            LatLng tester = new LatLng(mini_lat, mini_long);
            MarkerOptions whereyouat = new MarkerOptions().position(tester).icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("event", 30, 60)));;
            Marker test = null;

            //places the item on the map and assigns its string
            test = mMap.addMarker(whereyouat);
            test.setTag(location_number[skipper-1]);
        }

        //THIS IS WHERE YOU WOULD PUT CODE THAT HAPPENS WHEN YOU CLICK ON A BULLETIN BOARD
        //String.valueof(marker.tag()); will hold the string you want
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                CharSequence text = String.valueOf(marker.getTag());

                Intent eventSearch = new Intent(getBaseContext(),BulletinBoardGUI.class);
                eventSearch.putExtra("location",text);
                startActivity(eventSearch);

                return true;
            }
        });

    }//end of map ready

    //used to toggle the button being turned on and off
    //for satellite
    //if button chooser is 0 it turns on satellite else turns it off
    public void Satellite(View view){


        ImageButton sat = (ImageButton)findViewById(R.id.satell); //access the STOP_navigaiton button
        ImageButton maper = (ImageButton)findViewById(R.id.maper); //access the STOP_navigaiton button
        if (buttonChooser == 0) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            sat.setVisibility(View.INVISIBLE);
            maper.setVisibility(View.VISIBLE);
            buttonChooser = 1;
            // onPause();

        }
        else{
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            sat.setVisibility(View.VISIBLE);
            maper.setVisibility(View.INVISIBLE);
            buttonChooser = 0;
            // onResume();

        }

    }






    //press the button to rehome the map on your current location
    public void ReHome(View view){
        if(view.getId()==R.id.rehome){
            LatLng me = new LatLng(Latitude,Longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(me));
        }

    }


    //zooms the map in or out
    public void Zoom(View view){
        if(view.getId()==R.id.zoomout){
            mMap.animateCamera(CameraUpdateFactory.zoomOut());
        }
        if(view.getId()==R.id.zoomin){
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
        }
    }



    //small equation that gets the distance between any two points
    //used to find the closest node to the user
    //updated from python code
    //https://stackoverflow.com/questions/5407969/distance-formula-between-two-points-in-a-list
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

    //looks at every point and finds the closest
    //one to the user
    private int GetClosest(){
        double distance=900000000;//large impossible distance to initialize an "infinity" distance
        int counter = 0;
        for(int x=0;x<440;x++){
            if(GetDistance(Coords[x][1],Coords[x][0])<distance){
                counter = x;
                distance = GetDistance(Coords[x][1],Coords[x][0]);
            }
        }
        return counter;
    }

    private int hGetClosest(){
        double distance=900000000;
        int counter = 0;
        for(int x=0;x<440;x++){
            if(GetDistance(handiCoords[x][1],handiCoords[x][0])<distance){
                counter = x;
                distance = GetDistance(handiCoords[x][1],handiCoords[x][0]);
            }
        }
        return counter;
    }
    //this function calls the function over and over until we want it to stop
    // just repeats over and over
    private void repeater(final int stopper){
        if(stopper != 42069) {
            handler.postDelayed(runnable = new Runnable() {
                @Override
                public void run() {
                    killLines();
                    makePaths();
                    handler.postDelayed(runnable, delay);
                }
            }, delay);

        }

        if(stopper == 42069){
            killLines();
            handler.removeCallbacks(runnable);
        }

    }
    //this gets rid of all of the lines on the map after ever iteration
    //if we were to change places though it would need another function to change that
    private void killLines(){
        for(Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }

    //This actually draws the paths onto the map.
    private void makePaths() {

        if(type == 0) {
            dijkstra(Matrix, GetClosest(), Coord_number); // returns values for coords gets the values for the closet points
            if (Coord_number != 0) {

                polylines.add(this.mMap.addPolyline(new PolylineOptions().color(Color.RED).clickable(true).add(
                        new LatLng(Coords[GetClosest()][1], Coords[GetClosest()][0]),
                        new LatLng(Latitude, Longitude))));

            }


            for (int start = 1; start < my_paths.size(); start++) {

                polylines.add(this.mMap.addPolyline(new PolylineOptions().color(Color.RED).clickable(true).add(
                        new LatLng(Coords[my_paths.get(start - 1)][1], Coords[my_paths.get(start - 1)][0]),
                        new LatLng(Coords[my_paths.get(start)][1], Coords[my_paths.get(start)][0]))));

            }

            clear_All();
        }

        if(type == 1) {
            dijkstra(handiMatrix, hGetClosest(), Coord_number); // returns values for coords gets the values for the closet points
            if (Coord_number != 0) {

                polylines.add(this.mMap.addPolyline(new PolylineOptions().color(Color.RED).clickable(true).add(
                        new LatLng(handiCoords[hGetClosest()][1], handiCoords[hGetClosest()][0]),
                        new LatLng(Latitude, Longitude))));

            }


            for (int start = 1; start < my_paths.size(); start++) {

                polylines.add(this.mMap.addPolyline(new PolylineOptions().color(Color.RED).clickable(true).add(
                        new LatLng(handiCoords[my_paths.get(start - 1)][1], handiCoords[my_paths.get(start - 1)][0]),
                        new LatLng(handiCoords[my_paths.get(start)][1], handiCoords[my_paths.get(start)][0]))));
            }

            clear_All();
        }

    }







}