package com.example.campusnavigation;

<<<<<<< HEAD
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.provider.Settings;
=======
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
>>>>>>> main
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

<<<<<<< HEAD
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
=======
>>>>>>> main
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
<<<<<<< HEAD
import com.google.android.gms.tasks.OnCompleteListener;
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

import static java.sql.DriverManager.println;


/*
This can now find the shortest path between two points
and save the directions into a list that is accessable
then there is an array of values that represets the number of each of these values
so doing something like coorda[values][0] would get the lat for the first point
*/


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {
    FusedLocationProviderClient fusedLocationProviderClient;


=======

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,AdapterView.OnItemSelectedListener {
>>>>>>> main
    private static final String TAG = "MapActivity";
    private GoogleMap mMap;
    int buttonChooser = 0;//used to show which button type you are on Satellite or
    List<Marker> AllMarkers = new ArrayList<Marker>(); //Holds all the markers in the map
    //default
<<<<<<< HEAD
    //each time you update the map this needs to be updataed as well
    //make sure it matches the matrix in the python program
    int[][] Matrix = new int[440][440];
    double[][] Coords = new double[440][2];
    List<Polyline> polylines = new ArrayList<Polyline>();
    // List<Integer> my_paths = new ArrayList<Integer>();
///////////////////////////////////////////

    public static class DijkstrasAlgorithm {
=======
///////////////////////////////////////////
    public static class  DijkstrasAlgorithm {
>>>>>>> main

        private static final int NO_PARENT = -1;

        // Function that implements Dijkstra's
        // single source shortest path
        // algorithm for a graph represented
        // using adjacency matrix
        // representation
<<<<<<< HEAD
        private static List<Integer> my_paths = new ArrayList<Integer>();

        private static void dijkstra(int[][] adjacencyMatrix,
                                     int startVertex, int ender) {

=======
        private static void dijkstra(int[][] adjacencyMatrix,
                                     int startVertex,int ender)
        {
>>>>>>> main
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
<<<<<<< HEAD
                 vertexIndex++) {
=======
                 vertexIndex++)
            {
>>>>>>> main
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
<<<<<<< HEAD
            for (int i = 1; i < nVertices; i++) {
=======
            for (int i = 1; i < nVertices; i++)
            {
>>>>>>> main

                // Pick the minimum distance vertex
                // from the set of vertices not yet
                // processed. nearestVertex is
                // always equal to startNode in
                // first iteration.
                int nearestVertex = -1;
                int shortestDistance = Integer.MAX_VALUE;
                for (int vertexIndex = 0;
                     vertexIndex < nVertices;
<<<<<<< HEAD
                     vertexIndex++) {
                    if (!added[vertexIndex] &&
                            shortestDistances[vertexIndex] <
                                    shortestDistance) {
=======
                     vertexIndex++)
                {
                    if (!added[vertexIndex] &&
                            shortestDistances[vertexIndex] <
                                    shortestDistance)
                    {
>>>>>>> main
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
<<<<<<< HEAD
                     vertexIndex++) {
=======
                     vertexIndex++)
                {
>>>>>>> main
                    int edgeDistance = adjacencyMatrix[nearestVertex][vertexIndex];

                    if (edgeDistance > 0
                            && ((shortestDistance + edgeDistance) <
<<<<<<< HEAD
                            shortestDistances[vertexIndex])) {
=======
                            shortestDistances[vertexIndex]))
                    {
>>>>>>> main
                        parents[vertexIndex] = nearestVertex;
                        shortestDistances[vertexIndex] = shortestDistance +
                                edgeDistance;
                    }
                }
            }

<<<<<<< HEAD
            printSolution(startVertex, shortestDistances, parents, ender);
=======
            printSolution(startVertex, shortestDistances, parents,ender);
>>>>>>> main
        }

        // A utility function to print
        // the constructed distances
        // array and shortest paths
<<<<<<< HEAD

        private static void clear_All() {
            my_paths.clear();
        }

        private static void printSolution(int startVertex,
                                          int[] distances,
                                          int[] parents, int ender) {
=======
        private static void printSolution(int startVertex,
                                          int[] distances,
                                          int[] parents,int ender)
        {
            int nVertices = distances.length;
>>>>>>> main

            printPath(ender, parents);

        }

        // Function to print shortest path
        // from source to currentVertex
        // using parents array
<<<<<<< HEAD
        public static void printPath(int currentVertex,
                                     int[] parents) {
=======
        private static void printPath(int currentVertex,
                                      int[] parents)
        {
>>>>>>> main

            // Base case : Source node has
            // been processed

<<<<<<< HEAD
            if (currentVertex == NO_PARENT) {
                return;
            }
            printPath(parents[currentVertex], parents);
            Log.i("test: ", "works: " + currentVertex + " ");
            my_paths.add(currentVertex);
            // System.out.print(currentVertex + " ");


        }


/*
=======

            if (currentVertex == NO_PARENT)
            {
                return;
            }
            printPath(parents[currentVertex], parents);
            System.out.print(currentVertex + " ");
        }

>>>>>>> main
        // Driver Code
        public static void main(String[] args)
        {
            //zero represents a non path
            int[][] adjacencyMatrix = { { 0, 4, 0, 0, 0, 0, 0, 8, 0 },
                    { 4, 0, 8, 0, 0, 0, 0, 11, 0 },
                    { 0, 8, 0, 7, 0, 4, 0, 0, 2 },
                    { 0, 0, 7, 0, 9, 14, 0, 0, 0 },
                    { 0, 0, 0, 9, 0, 10, 0, 0, 0 },
                    { 0, 0, 4, 0, 10, 0, 2, 0, 0 },
                    { 0, 0, 0, 14, 0, 2, 0, 1, 6 },
                    { 8, 11, 0, 0, 0, 0, 1, 0, 7 },
                    { 0, 0, 2, 0, 0, 0, 6, 7, 0 } };
<<<<<<< HEAD
            //dijkstra(adjacencyMatrix, 2,7);
       // Log.i("test","out[ut");
        }

        */
=======
            dijkstra(adjacencyMatrix, 2,7);
        }
>>>>>>> main
    }

    //////////////////////////////////////////


<<<<<<< HEAD
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //this is when the permision is granted
            getLocation();
        } else {
            //user no trust us so they give us the slap
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

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
            /*
            int checker = 0;
            int problem_row = 0;
            int problem_column = 0;
            for(int row=0;row<333;row++){
                for(int column=0; column<333;column++){
                    if(Matrix[row][column]!=Matrix[column][row]){
                        checker = 1;
                        problem_column = column;
                        problem_row = row;
                    }
                  //  Matrix[row][column] = Integer.parseInt(array[row][column]);
                }

                Log.i("matrix output: ","Zero if good: "+checker);

            }
            */

            //  Log.i("matrix output: ","correct output?"+Matrix[331][332]);


        } catch (Exception e) {
            e.printStackTrace();
        }
///////////////////
        //This works so good!!! Now all I need is a dictionary that takes in matrix values and
        //returns coords.
        // my_paths.clear();
        //    DijkstrasAlgorithm.dijkstra(Matrix,0,1);
///////////////////


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Spinner spinner = findViewById(R.id.maps_Spinners);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Locations, android.R.layout.simple_dropdown_item_1line);
=======



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
>>>>>>> main
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

<<<<<<< HEAD

    }

    private void getLocation() {
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
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if(location != null){

                    try {
                        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(),location.getLongitude(),1
                        );
                        LatLng marker = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                        Marker mark = mMap.addMarker(new MarkerOptions().position(marker).title("Common Wealth"));
                        AllMarkers.add(mark);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

=======
    }
>>>>>>> main
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
<<<<<<< HEAD
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

            /*
            int checker = 0;
            int problem_row = 0;
            int problem_column = 0;
            for(int row=0;row<333;row++){
                for(int column=0; column<333;column++){
                    if(Matrix[row][column]!=Matrix[column][row]){
                        checker = 1;
                        problem_column = column;
                        problem_row = row;
                    }
                  //  Matrix[row][column] = Integer.parseInt(array[row][column]);
                }

                Log.i("matrix output: ","Zero if good: "+checker);

            }
            */

            //  Log.i("matrix output: ","correct output?"+Matrix[331][332]);


        } catch (Exception e) {
            e.printStackTrace();
        }


      //  Log.i("coords","//////////////////////////////////////////////coords for one: ");
       // Log.i("coords","//////////////////////////////////////////////coords for one: "+Coords[0][1]);

        //////////////////////////
//This is used for generating lines on the map
//it will get node data from the nodes created in the dijkstra's algorithm
//and will make a black line between all of the points
       /*

        Polyline polyline1 = mMap.addPolyline(new PolylineOptions().clickable(true).add(
                new LatLng(36.97053391196394, -82.55990677023982),
                new LatLng(36.970692489150636, -82.56020717763906)));
       */
        //new LatLng(36.97101392838035, -82.56078921697508))
/////////////////////////
//DijkstrasAlgorithm path = new DijkstrasAlgorithm();
//path.dijkstra(Matrix,0,68); //actually returns values for coords

//Log.i("hello","message/////////////////////"+path.my_paths.size());
////////////////////////

       // for(int start =1;start<path.my_paths.size()/2;start++) {

          //  Polyline polyline2 = mMap.addPolyline(new PolylineOptions().clickable(true).add(
                   // new LatLng(Coords[path.my_paths.get(start-1)][1], Coords[path.my_paths.get(start-1)][0]),
                    //new LatLng(Coords[path.my_paths.get(start)][1], Coords[path.my_paths.get(start)][0])));
/*
            Polyline polyline2 = mMap.addPolyline(new PolylineOptions().clickable(true).add(
                    new LatLng(Coords[start][1], Coords[start][0]),
                    new LatLng(Coords[start+1][1], Coords[start+1][0])));
            // new LatLng(36.97101392838035, -82.56078921697508)));
            */

       // }

=======

        // Add a marker in Sydney and move the camera
        //lat and long of uva wise va: 36.970702412486304, -82.56071600207069

//////////////////////////
//This is used for generating lines on the map
//it will get node data from the nodes created in the dijkstra's algorithm
//and will make a black line between all of the points
          Polyline polyline1 = mMap.addPolyline(new PolylineOptions().clickable(true).add(
               new LatLng(36.97053391196394, -82.55990677023982),
        new LatLng(36.970692489150636, -82.56020717763906),
        new LatLng(36.97101392838035, -82.56078921697508)));
/////////////////////////
>>>>>>> main
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

    private void RemoveAll(){
        for(Marker mark: AllMarkers){
            mark.remove();
        }
<<<<<<< HEAD
        for(Polyline line: polylines){
            line.remove();
        }
        polylines.clear();
=======
        AllMarkers.clear();
>>>>>>> main
    }

    public void display(String name){
  //This was terrible to implement
        //the problem is that you have to have each of the the
        //Coordinates has to be entered/ found manually anyway so
        //at that point why not just copy past if statements and fill in the info
        // manually... may have been a better solution but hey this is all
<<<<<<< HEAD
        //the GPS coordinates for all the main locations on campus so have fun with that.poly

    RemoveAll();//this eliminates the all the nodes each time a new node is added so that only one node is displayed at a time
        int Coord_number = 0;

=======
        //the GPS coordinates for all the main locations on campus so have fun with that.


    RemoveAll();//this eliminates the all the nodes each time a new node is added so that only one node is displayed at a time
>>>>>>> main

        if( name.contains("Commonwealth Hall")) {
            LatLng marker = new LatLng(36.973036, -82.561149);
               Marker mark = mMap.addMarker(new MarkerOptions().position(marker).title("Common Wealth"));
<<<<<<< HEAD
               Coord_number =289;
=======
>>>>>>> main
               AllMarkers.add(mark);
        }

        if( name.contains("David J. Prior Convocation Center")) {
            LatLng marker = new LatLng(36.97615639865157, -82.56536704840991);
            Marker mark = mMap.addMarker(new MarkerOptions().position(marker).title("David J. Prior Convocation Center"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number = 193;
=======
>>>>>>> main
        }

        if( name.contains("Humphreys-Thomas Field House")) {
            LatLng marker = new LatLng(36.97551553576881, -82.56493038860793);
            Marker mark =  mMap.addMarker(new MarkerOptions().position(marker).title("Humphreys-Thomas Field House"));
            AllMarkers.add(mark);
<<<<<<< HEAD
           // Coord_number = 190;
=======
>>>>>>> main
        }
        if( name.contains("Carl Smith Stadium")) {
            LatLng marker = new LatLng(36.97497554901266, -82.56437013934703);
            Marker mark =  mMap.addMarker(new MarkerOptions().position(marker).title("Carl Smith Stadium"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number = 270;
=======
>>>>>>> main
        }
        if( name.contains("Ramseyer Press Box")) {
            LatLng marker = new LatLng(36.975232686029756, -82.56379346442196);
            Marker mark =  mMap.addMarker(new MarkerOptions().position(marker).title("Ramseyer Press Box"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number = 227;
        }
        if( name.contains("Culberston Hall")) {
            LatLng marker = new LatLng(36.97251, -82.56269);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Culberston Hall"));
            AllMarkers.add(mark);
            Coord_number = 92;
=======
        }
        if( name.contains("Baptitst Collegiate Ministries")) {
            LatLng marker = new LatLng(36.97165973072659, -82.56389986680914);
            Marker mark =  mMap.addMarker(new MarkerOptions().position(marker).title("Baptitst Collegiate Ministries"));
            AllMarkers.add(mark);
        }
        if( name.contains("Culberston Hall")) {
            LatLng marker = new LatLng(36.97058669395023, -82.5606945444325);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Culberston Hall"));
            AllMarkers.add(mark);
>>>>>>> main
        }
        if( name.contains("College Relations/Napoleon Hill Foundation")) {
            LatLng marker = new LatLng(36.97283367105798, -82.56201908233115);
            Marker mark =  mMap.addMarker(new MarkerOptions().position(marker).title("College Relations/Napoleon Hill Foundation"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number = 104;
=======
>>>>>>> main
        }
        if( name.contains("Lila Vicars Smith House")) {
            LatLng marker = new LatLng(36.975204799790255, -82.5581445499958);
            Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Lila Vicars Smith House"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number =377;
=======
>>>>>>> main
        }
        if( name.contains("McCrary Hall")) {
            LatLng marker = new LatLng(36.97121534957083, -82.5619257479437);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("McCrary Hall"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number = 1;
=======
>>>>>>> main
        }
        if( name.contains("Gilliam Center for the Arts")) {
            LatLng marker = new LatLng(36.972080459181335, -82.56051463210983);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Gilliam Center for the Arts"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number = 130;
=======
>>>>>>> main
        }
        if( name.contains("Hunter J. Smith Dining Commons")) {
            LatLng marker = new LatLng(36.97243199745305, -82.56017324262561);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Hunter J. Smith Dining Commons"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number = 132;
=======
>>>>>>> main
        }
        if( name.contains("Thompson Hall")) {
            LatLng marker = new LatLng(36.97282049431341, -82.55976229125751);
            Marker mark =  mMap.addMarker(new MarkerOptions().position(marker).title("Thompson Hall"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number = 135;
=======
>>>>>>> main
        }
        if( name.contains("Asbury Hall")) {
            LatLng marker = new LatLng(36.97274149636506, -82.55916581699721);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Asbury Hall"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number = 139;
=======
>>>>>>> main
        }
        if( name.contains("Martha Randolph Hall")) {
            LatLng marker = new LatLng(36.97354682270125, -82.55909096561946);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Martha Randolph Hall"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number = 305;
=======
>>>>>>> main
        }
        if( name.contains("Crocket Hall (Admissions Office)")) {
            LatLng marker = new LatLng(36.97055790648928, -82.56066188458206);
            Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Crocket Hall (Admissions Office)"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number = 77;
=======
>>>>>>> main
        }
        if( name.contains("Cantrell Hall")) {
            LatLng marker = new LatLng(36.97129507307334, -82.56021663787666);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Cantrell Hall"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number = 308;
=======
>>>>>>> main
        }

        if( name.contains("Chapel of all Faiths")) {
            LatLng marker = new LatLng(36.97096077748099, -82.55998060349206);
            Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Chapel of all Faiths"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number = 171;
=======
>>>>>>> main
        }
        if( name.contains("Henson Hall")) {
            LatLng marker = new LatLng(36.97204937568239, -82.55935833102355);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Henson Hall"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number = 143;
=======
>>>>>>> main
        }
        if( name.contains("Smiddy Hall")) {
            LatLng marker = new LatLng(36.970227893533405, -82.55995378137858);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Smiddy Hall"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number = 18;
        }
        if( name.contains("C. Bascom Slemp Student Center")) {
            LatLng marker = new LatLng(36.970583621705494, -82.55972847582962);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("C. Bascom Slemp Student Center"));
            AllMarkers.add(mark);
            Coord_number = 56;
=======
        }
        if( name.contains("C. Bascom Slemp Student Center")) {
            LatLng marker = new LatLng(336.970583621705494, -82.55972847582962);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("C. Bascom Slemp Student Center"));
            AllMarkers.add(mark);
>>>>>>> main
        }
        if( name.contains("Wyllie Hall")) {
            LatLng marker = new LatLng(36.97070791188466, -82.55911156776746);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Wyllie Hall"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number = 49;
=======
>>>>>>> main
        }

        if( name.contains("UVA-Wise Library")) {
            LatLng marker = new LatLng(36.971457934490424, -82.55971238256465);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("UVA-Wise Library"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number = 168;
=======
>>>>>>> main
        }
        if( name.contains("Zehmer Hall")) {
            LatLng marker = new LatLng(36.97117506968736, -82.55869314316784);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Zehmer Hall"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number = 317;
=======
>>>>>>> main
        }
        if( name.contains("Darden Hall")) {
            LatLng marker = new LatLng(36.971655082060316, -82.55906328799826);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Darden Hall"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number = 318;
=======
>>>>>>> main
        }
        if( name.contains("Leonard W. Sandridge Science Center")) {
            LatLng marker = new LatLng(36.97175365565509, -82.55802795535666);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Leonard W. Sandridge Science Center"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number = 324;
=======
>>>>>>> main
        }
        if( name.contains("Betty J. Gilliam Sculpture Garden")) {
            LatLng marker = new LatLng(36.97163793881761, -82.5585590327151);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Betty J. Gilliam Sculpture Garden"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number = 319;
=======
>>>>>>> main
        }
        if( name.contains("Physical Plant")) {
            LatLng marker = new LatLng(36.97442645746456, -82.55656261417435);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Physical Plant"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number =351;
=======
>>>>>>> main
        }
        if( name.contains("Winston Ely Health and Wellness Center")) {
            LatLng marker = new LatLng(36.97034582909863, -82.55937144085671);
            Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Winston Ely Health and Wellness Center"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number = 31;
=======
>>>>>>> main
        }
        if( name.contains("Bower-Sturgill Hall")) {
            LatLng marker = new LatLng(36.96999374950577, -82.55859710648123);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Bower-Sturgill Hall"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number = 35;
=======
>>>>>>> main
        }
        if( name.contains("Stallard Field (Baseball)")) {
            LatLng marker = new LatLng(36.96911941990102, -82.55809285121471);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Stallard Field (Baseball)"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number = 67;
=======
>>>>>>> main
        }

        if( name.contains("Fred B. Greear Gym and Swimming Pool")) {
            LatLng marker = new LatLng(36.96980945537668, -82.55743302781167);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Common Wealth"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number = 42;
=======
>>>>>>> main
        }

        if( name.contains("Humphreys Tennis Complex")) {
            LatLng marker = new LatLng(36.97135665502713, -82.55685903510364);
            Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Humphreys Tennis Complex"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number = 65;
=======
>>>>>>> main
        }

        if( name.contains("Women's Softball Field")) {
            LatLng marker = new LatLng(36.97092689771323, -82.55571754268155);
            Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Women's Softball Field"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number = 68;
=======
>>>>>>> main
        }
        if( name.contains("Intramural Sports")) {
            LatLng marker = new LatLng(36.972652570498916, -82.55382265627702);
            Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Intramural Sports"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number =361;
=======
>>>>>>> main
        }
        if( name.contains("Observatory")) {
            LatLng marker = new LatLng(36.97053447205514, -82.55175752072537);
            Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Observatory"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number =374;
        }

        if( name.contains("Resource Center")) {
            LatLng marker = new LatLng(36.97223, -82.56573);
            Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Resource Center"));
            AllMarkers.add(mark);
            Coord_number =409;
=======
        }
        if( name.contains("Townhouses")) {
            LatLng marker = new LatLng(36.97541945173639, -82.56902534763123);
            Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Townhouses"));
            AllMarkers.add(mark);
        }

        if( name.contains("Resource Center")) {
            LatLng marker = new LatLng(36.97096110122889, -82.56435399106836);
            Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Resource Center"));
            AllMarkers.add(mark);
>>>>>>> main
        }

        if( name.contains("Center for Teaching Excellence")) {
            LatLng marker = new LatLng(36.97091576054845, -82.56418429612896);
            Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Center for Teaching Excellence"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number =403;
=======
>>>>>>> main
        }
        if( name.contains("Wesley Foundation")) {
            LatLng marker = new LatLng(36.96981869149186, -82.56350910361056);
            Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Wesley Foundation"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number =424;
=======
>>>>>>> main
        }

        if( name.contains("Alumni Hall")) {
            LatLng marker = new LatLng(36.96961564106247, -82.56187436766942);
            Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Alumni Hall"));
            AllMarkers.add(mark);
<<<<<<< HEAD
            Coord_number =398;
        }

        DijkstrasAlgorithm path = new DijkstrasAlgorithm();
        path.dijkstra(Matrix,0,Coord_number); //actually returns values for coords

      //  Log.i("path_dest","message/////////////////////"+path.my_paths);
////////////////////////

        for(int start =1;start<path.my_paths.size();start++) {

          polylines.add(this.mMap.addPolyline(new PolylineOptions().color(Color.RED).clickable(true).add(
                    new LatLng(Coords[path.my_paths.get(start-1)][1], Coords[path.my_paths.get(start-1)][0]),
                    new LatLng(Coords[path.my_paths.get(start)][1], Coords[path.my_paths.get(start)][0]))));


/*
            Polyline polyline2 = mMap.addPolyline(new PolylineOptions().clickable(true).add(
                    new LatLng(Coords[start][1], Coords[start][0]),
                    new LatLng(Coords[start+1][1], Coords[start+1][0])));
            // new LatLng(36.97101392838035, -82.56078921697508)));
            */

        }

    path.clear_All();
=======
        }


>>>>>>> main
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