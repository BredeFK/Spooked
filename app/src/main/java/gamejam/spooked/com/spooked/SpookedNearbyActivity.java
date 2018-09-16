package gamejam.spooked.com.spooked;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class SpookedNearbyActivity extends AppCompatActivity {
    private ArrayList<User> userList = new ArrayList<>();
    private static final String userString = "Users";
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private FirebaseUser user;
    private ListView listNearby;
    private NearbyAdapter adapter;
  //  public double lat;
  //  public double lon;
    private double distance;
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spooked_nearby);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference(userString);
        user = auth.getCurrentUser();

        initPos();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        listNearby = findViewById(R.id.nearbyListView);
        adapter = new NearbyAdapter(this, R.layout.nearby_item);
        setupListView();
    }

    private void initPos(){
        //check/request location permissions
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(SpookedNearbyActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }

        // LOCATION MANAGER
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // LOCATION UPDATES LISTENER
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                editor = preferences.edit();
                editor.putFloat("lat", (float)location.getLatitude());
                editor.putFloat("lon", (float)location.getLongitude());
                editor.apply();
               /* lat = location.getLatitude();
                lon = location.getLongitude();

                adapter.setLat(lat);
                adapter.setLon(lon);*/
                Log.d("HELLO", location.getLatitude() + " " + location.getLongitude());
            }
            public void onStatusChanged(String provider, int status, Bundle extras) { }
            public void onProviderEnabled(String provider) { }
            public void onProviderDisabled(String provider) { }
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

    }

    private void setupListView(){
        if(user != null)
            fillListView();
        else
            Toast.makeText(this, "You are not logged in", Toast.LENGTH_LONG).show();
    }

    private void fillListView(){
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    User nearbyUser = child.getValue(User.class);
                    if(nearbyUser != null){
                     if(!nearbyUser.getUid().equals(user.getUid()) && nearbyUser.hasLocation()){
                         userList.add(nearbyUser);
                        }
                    }
                }
                sortList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Rip :(
            }
        });
    }

    private void sortList(){
        // initialise variables
        ArrayList<User> sortedUserList = new ArrayList<>(); sortedUserList.clear();
        int index = 0; int size = userList.size(); double highest = 0;
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final float lat = preferences.getFloat("lat", 0);
        final float lon = preferences.getFloat("lon", 0);


        Log.d("HELLO2", lat + " " + lon);


        // Sort by highest distance
        while (sortedUserList.size() != size) {
            for (int i = 0; i < userList.size(); i++) {
                distance = distance(lat,lon, userList.get(i).getLastLatitude(), userList.get(i).getLastLongitude());
                if (distance >= highest) {
                    highest = distance;
                    index = i;
                }
                if(i == userList.size()-1){
                    sortedUserList.add(userList.get(index));
                    Log.d("MONSTER", userList.get(index).getName());
                    userList.remove(index);
                    highest = 0;
                }
            }
        }

        adapter.clear();
        // Reverse list (sort by lowest) and display
        for (int i = sortedUserList.size()-1; i >= 0; i--) {
            Log.d("SORTED", sortedUserList.get(i).getName());
            adapter.add(sortedUserList.get(i));
        }
        listNearby.setAdapter(adapter);

    }

    // https://dzone.com/articles/distance-calculation-using-3
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;

        return Math.floor(dist * 100) / 100;
    }
    // Source done
}
