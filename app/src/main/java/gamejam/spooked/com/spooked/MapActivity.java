package gamejam.spooked.com.spooked;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private int currentDate;
    private String userID;
    private LatLng currentLatLng;

    private FirebaseAuth auth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    private ArrayList<Marker> markerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //firebase auth
        auth = FirebaseAuth.getInstance();
        //retrieve userid
        userID = auth.getCurrentUser().getUid();

        //db connection and reference
        this.mDatabase = FirebaseDatabase.getInstance();
        this.myRef = mDatabase.getReference("spooks");

        this.currentDate = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime()));

        //data
        readFromDB();
        //map
        setupMapAndLocation();
        //fab
        setupFAB();


    }

    private void addToMap(LatLng pos, String text, int daysOld){

        int ghost = 0;

        switch (daysOld){
            case 0: ghost = R.mipmap.if_ghost1; break;
            case 1: ghost = R.mipmap.if_ghost1; break;
            case 2: ghost = R.mipmap.if_ghost2; break;
            case 3: ghost = R.mipmap.if_ghost3; break;
            case 4: ghost = R.mipmap.if_ghost4; break;
            default: ghost = R.mipmap.if_ghost5; break;
        }

        Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(pos)
                            .title(text)
                            .visible(true)
                            .icon(BitmapDescriptorFactory.fromResource(ghost))
        );

        markerList.add(marker);
    }

    private void deleteMarkers(){
        for (int i = 0; i < markerList.size(); i++) {
            markerList.get(i).remove();
        }

        markerList.clear();
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
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style));

            if (!success) {
                Log.e("HEI", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("HEI", "Can't find style. Error: ", e);
        }
        this.mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(60.7901781, 10.6834482), 15));
    }

    private void readFromDB(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                deleteMarkers(); //resets all markers - this is very unefficient but ye
                for(DataSnapshot child: dataSnapshot.getChildren()) {
                    Spook spook = child.getValue(Spook.class);
                    addToMap(new LatLng(spook.getLatitude(), spook.getLongitude()), spook.getUserID(), currentDate-spook.getDate());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }


    private void setupMapAndLocation(){
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //check/request location permissions
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(MapActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }

        // LOCATION MANAGER
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // LOCATION UPDATES LISTENER
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
                currentLatLng = pos;
            }
            public void onStatusChanged(String provider, int status, Bundle extras) { }
            public void onProviderEnabled(String provider) { }
            public void onProviderDisabled(String provider) { }
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    private void setupFAB(){
        // FAB STUFF
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

                String key = myRef.push().getKey();
                myRef.child(key).setValue(new Spook(userID, currentLatLng.latitude, currentLatLng.longitude, currentDate));
            }
        });
    }
}
