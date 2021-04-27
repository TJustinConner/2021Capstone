package com.example.campusnavigation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Location_finder extends AppCompatActivity implements AdapterView.OnItemSelectedListener{


    int Coord_number = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_finder3);



        Spinner spinner = findViewById(R.id.maps_Spinners);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Locations, android.R.layout.simple_dropdown_item_1line);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);
        //  spinner.setSelection(0,false);//will not run SetonItemSelectListener on start up
        spinner.setOnItemSelectedListener(this);
    }


    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId()==R.id.maps_Spinners) {

            String text = parent.getItemAtPosition(position).toString();
            //  Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
            display(text);
            if(Coord_number != 0) {
            String intToString = String.valueOf(Coord_number);
            Intent sender = new Intent();
            sender.putExtra("coordinate",intToString);
            setResult(Activity.RESULT_OK,sender);
            finish();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void display(String name){
        //This was terrible to implement
        //the problem is that you have to have each of the the
        //Coordinates has to be entered/ found manually anyway so
        //at that point why not just copy past if statements and fill in the info
        // manually... may have been a better solution but hey this is all
        //the GPS coordinates for all the main locations on campus so have fun with that.poly

        if( name.contains("Commonwealth Hall")) {
            LatLng marker = new LatLng(36.973036, -82.561149);
          //  Marker mark = mMap.addMarker(new MarkerOptions().position(marker).title("Common Wealth"));
            Coord_number =289;

        }

        if( name.contains("David J. Prior Convocation Center")) {
            LatLng marker = new LatLng(36.97615639865157, -82.56536704840991);
           // Marker mark = mMap.addMarker(new MarkerOptions().position(marker).title("David J. Prior Convocation Center"));
           // AllMarkers.add(mark);
            Coord_number = 193;
        }

        if( name.contains("Humphreys-Thomas Field House")) {
            LatLng marker = new LatLng(36.97551553576881, -82.56493038860793);
            //Marker mark =  mMap.addMarker(new MarkerOptions().position(marker).title("Humphreys-Thomas Field House"));
            //AllMarkers.add(mark);
            // Coord_number = 190;
        }
        if( name.contains("Carl Smith Stadium")) {
            LatLng marker = new LatLng(36.97497554901266, -82.56437013934703);
            //Marker mark =  mMap.addMarker(new MarkerOptions().position(marker).title("Carl Smith Stadium"));
           // AllMarkers.add(mark);
            Coord_number = 270;
        }
        if( name.contains("Ramseyer Press Box")) {
            LatLng marker = new LatLng(36.975232686029756, -82.56379346442196);
            //Marker mark =  mMap.addMarker(new MarkerOptions().position(marker).title("Ramseyer Press Box"));
            //AllMarkers.add(mark);
            Coord_number = 227;
        }
        if( name.contains("Culberston Hall")) {
            LatLng marker = new LatLng(36.97251, -82.56269);
            //Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Culberston Hall"));
            //AllMarkers.add(mark);
            Coord_number = 92;
        }
        if( name.contains("College Relations/Napoleon Hill Foundation")) {
            LatLng marker = new LatLng(36.97283367105798, -82.56201908233115);
            //Marker mark =  mMap.addMarker(new MarkerOptions().position(marker).title("College Relations/Napoleon Hill Foundation"));
            ///AllMarkers.add(mark);
            Coord_number = 104;
        }
        if( name.contains("Lila Vicars Smith House")) {
            LatLng marker = new LatLng(36.975204799790255, -82.5581445499958);
            //Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Lila Vicars Smith House"));
            //AllMarkers.add(mark);
            Coord_number =377;
        }
        if( name.contains("McCrary Hall")) {
            LatLng marker = new LatLng(36.97121534957083, -82.5619257479437);
           // Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("McCrary Hall"));
            //AllMarkers.add(mark);
            Coord_number = 1;
        }
        if( name.contains("Gilliam Center for the Arts")) {
            LatLng marker = new LatLng(36.972080459181335, -82.56051463210983);
          //  Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Gilliam Center for the Arts"));
          //  AllMarkers.add(mark);
            Coord_number = 130;
        }
        if( name.contains("Hunter J. Smith Dining Commons")) {
            LatLng marker = new LatLng(36.97243199745305, -82.56017324262561);
           // Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Hunter J. Smith Dining Commons"));
           // AllMarkers.add(mark);
            Coord_number = 132;
        }
        if( name.contains("Thompson Hall")) {
            LatLng marker = new LatLng(36.97282049431341, -82.55976229125751);
           // Marker mark =  mMap.addMarker(new MarkerOptions().position(marker).title("Thompson Hall"));
           // AllMarkers.add(mark);
            Coord_number = 135;
        }
        if( name.contains("Asbury Hall")) {
            LatLng marker = new LatLng(36.97274149636506, -82.55916581699721);
         //   Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Asbury Hall"));
          //  AllMarkers.add(mark);
            Coord_number = 139;
        }
        if( name.contains("Martha Randolph Hall")) {
            LatLng marker = new LatLng(36.97354682270125, -82.55909096561946);
          ///  Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Martha Randolph Hall"));
          //  AllMarkers.add(mark);
            Coord_number = 305;
        }
        if( name.contains("Crocket Hall (Admissions Office)")) {
            LatLng marker = new LatLng(36.97055790648928, -82.56066188458206);
          //  Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Crocket Hall (Admissions Office)"));
          //  AllMarkers.add(mark);
            Coord_number = 77;
        }
        if( name.contains("Cantrell Hall")) {
            LatLng marker = new LatLng(36.97129507307334, -82.56021663787666);
          //  Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Cantrell Hall"));
          //  AllMarkers.add(mark);
            Coord_number = 308;
        }

        if( name.contains("Chapel of all Faiths")) {
            LatLng marker = new LatLng(36.97096077748099, -82.55998060349206);
          ///  Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Chapel of all Faiths"));
          //  AllMarkers.add(mark);
            Coord_number = 171;
        }
        if( name.contains("Henson Hall")) {
            LatLng marker = new LatLng(36.97204937568239, -82.55935833102355);
          //  Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Henson Hall"));
          //  AllMarkers.add(mark);
            Coord_number = 143;
        }
        if( name.contains("Smiddy Hall")) {
            LatLng marker = new LatLng(36.970227893533405, -82.55995378137858);
          //  Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Smiddy Hall"));
          //  AllMarkers.add(mark);
            Coord_number = 18;
        }
        if( name.contains("C. Bascom Slemp Student Center")) {
            LatLng marker = new LatLng(36.970583621705494, -82.55972847582962);
          //  Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("C. Bascom Slemp Student Center"));
          //  AllMarkers.add(mark);
            Coord_number = 56;
        }
        if( name.contains("Wyllie Hall")) {
            LatLng marker = new LatLng(36.97070791188466, -82.55911156776746);
         //   Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Wyllie Hall"));
         //   AllMarkers.add(mark);
            Coord_number = 49;
        }

        if( name.contains("UVA-Wise Library")) {
            LatLng marker = new LatLng(36.971457934490424, -82.55971238256465);
          //  Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("UVA-Wise Library"));
          //  AllMarkers.add(mark);
            Coord_number = 168;
        }
        if( name.contains("Zehmer Hall")) {
            LatLng marker = new LatLng(36.97117506968736, -82.55869314316784);
          //  Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Zehmer Hall"));
          //  AllMarkers.add(mark);
            Coord_number = 317;
        }
        if( name.contains("Darden Hall")) {
            LatLng marker = new LatLng(36.971655082060316, -82.55906328799826);
          //  Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Darden Hall"));
          ///  AllMarkers.add(mark);
            Coord_number = 318;
        }
        if( name.contains("Leonard W. Sandridge Science Center")) {
            LatLng marker = new LatLng(36.97175365565509, -82.55802795535666);
          //  Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Leonard W. Sandridge Science Center"));
           // AllMarkers.add(mark);
            Coord_number = 324;
        }
        if( name.contains("Betty J. Gilliam Sculpture Garden")) {
            LatLng marker = new LatLng(36.97163793881761, -82.5585590327151);
          //  Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Betty J. Gilliam Sculpture Garden"));
          //  AllMarkers.add(mark);
            Coord_number = 319;
        }
        if( name.contains("Physical Plant")) {
            LatLng marker = new LatLng(36.97442645746456, -82.55656261417435);
          //  Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Physical Plant"));
          //  AllMarkers.add(mark);
            Coord_number =351;
        }
        if( name.contains("Winston Ely Health and Wellness Center")) {
            LatLng marker = new LatLng(36.97034582909863, -82.55937144085671);
          //  Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Winston Ely Health and Wellness Center"));
          //  AllMarkers.add(mark);
            Coord_number = 31;
        }
        if( name.contains("Bower-Sturgill Hall")) {
            LatLng marker = new LatLng(36.96999374950577, -82.55859710648123);
          //  Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Bower-Sturgill Hall"));
          //  AllMarkers.add(mark);
            Coord_number = 35;
        }
        if( name.contains("Stallard Field (Baseball)")) {
            LatLng marker = new LatLng(36.96911941990102, -82.55809285121471);
          //  Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Stallard Field (Baseball)"));
        //    AllMarkers.add(mark);
            Coord_number = 67;
        }

        if( name.contains("Fred B. Greear Gym and Swimming Pool")) {
            LatLng marker = new LatLng(36.96980945537668, -82.55743302781167);
          //  Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Common Wealth"));
         //   AllMarkers.add(mark);
            Coord_number = 42;
        }

        if( name.contains("Humphreys Tennis Complex")) {
            LatLng marker = new LatLng(36.97135665502713, -82.55685903510364);
         //   Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Humphreys Tennis Complex"));
         //   AllMarkers.add(mark);
            Coord_number = 65;
        }

        if( name.contains("Women's Softball Field")) {
            LatLng marker = new LatLng(36.97092689771323, -82.55571754268155);
         //   Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Women's Softball Field"));
          //  AllMarkers.add(mark);
            Coord_number = 68;
        }
        if( name.contains("Intramural Sports")) {
            LatLng marker = new LatLng(36.972652570498916, -82.55382265627702);
         //   Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Intramural Sports"));
        //    AllMarkers.add(mark);
            Coord_number =361;
        }
        if( name.contains("Observatory")) {
            LatLng marker = new LatLng(36.97053447205514, -82.55175752072537);
         ///   Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Observatory"));
          //  AllMarkers.add(mark);
            Coord_number =374;
        }

        if( name.contains("Resource Center")) {
            LatLng marker = new LatLng(36.97223, -82.56573);
         ///   Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Resource Center"));
          //  AllMarkers.add(mark);
            Coord_number =409;
        }

        if( name.contains("Center for Teaching Excellence")) {
            LatLng marker = new LatLng(36.97091576054845, -82.56418429612896);
          //  Marker mark =   mMap.addMarker(new MarkerOptions().position(marker).title("Center for Teaching Excellence"));
          //  AllMarkers.add(mark);
            Coord_number =403;
        }
        if( name.contains("Wesley Foundation")) {
            LatLng marker = new LatLng(36.96981869149186, -82.56350910361056);
          //  Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Wesley Foundation"));
          //  AllMarkers.add(mark);
            Coord_number =424;
        }

        if( name.contains("Alumni Hall")) {
            LatLng marker = new LatLng(36.96961564106247, -82.56187436766942);
          //  Marker mark =    mMap.addMarker(new MarkerOptions().position(marker).title("Alumni Hall"));
          //  AllMarkers.add(mark);
            Coord_number =398;
        }

        // (Coord_number);

        /*
            Polyline polyline2 = mMap.addPolyline(new PolylineOptions().clickable(true).add(
                    new LatLng(Coords[start][1], Coords[start][0]),
                    new LatLng(Coords[start+1][1], Coords[start+1][0])));
            // new LatLng(36.97101392838035, -82.56078921697508)));
            */



    }//display



}