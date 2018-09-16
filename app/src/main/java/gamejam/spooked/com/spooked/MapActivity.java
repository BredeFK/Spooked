package gamejam.spooked.com.spooked;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private boolean markerFocus = false;
    private Marker currentMarker = null;

    private GoogleMap mMap;

    private int currentDate;
    private String userID;
    private LatLng currentLatLng;

    private FirebaseAuth auth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private DatabaseReference userRef;
    private DatabaseReference friendRef;

    private ArrayList<Marker> markerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        currentLatLng = new LatLng(0, 0);

        //firebase auth
        auth = FirebaseAuth.getInstance();
        //retrieve userid
        userID = auth.getCurrentUser().getUid();

        //db connection and reference
        this.mDatabase = FirebaseDatabase.getInstance();
        this.myRef = mDatabase.getReference("spooks");
        this.userRef = mDatabase.getReference("Users");
        this.friendRef = mDatabase.getReference("Friends");

        this.currentDate = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime()));

        //data
        readFromDB();
        //map
        setupMapAndLocation();
        //fab
        setupFAB();

    }

    private void addToMap(LatLng pos, String text, int daysOld) {

        int ghost = 0;
        float visible = 1.0f;

        switch (daysOld) {
            case 0:
                ghost = R.mipmap.if_ghost1;
                visible = 1.0f;
                break;
            case 1:
                ghost = R.mipmap.if_ghost1;
                visible = 0.9f;
                break;
            case 2:
                ghost = R.mipmap.if_ghost2;
                visible = 0.8f;
                break;
            case 3:
                ghost = R.mipmap.if_ghost3;
                visible = 0.6f;
                break;
            case 4:
                ghost = R.mipmap.if_ghost4;
                visible = 0.4f;
                break;
            default:
                ghost = R.mipmap.if_ghost5;
                visible = 0.2f;
                break;
        }

        if (userID.equals(text)) {
            ghost = R.mipmap.if_ghost_me;
            visible = 1.0f;
        }

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(pos)
                //.title(text)
                .visible(true)
                .alpha(visible)
                .icon(BitmapDescriptorFactory.fromResource(ghost))
        );
        marker.setTag(text);
        markerList.add(marker);
    }

    private void deleteMarkers() {
        for (int i = 0; i < markerList.size(); i++) {
            markerList.get(i).remove();
        }

        markerList.clear();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        final String uid = (String) marker.getTag();

        if (userID.equals(uid)) {
            return false;
        }

        currentMarker = marker;
        markerFocus = true;
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.if_ghost6));
        marker.setAlpha(1.0f);

        ConstraintLayout fragment = (ConstraintLayout) findViewById(R.id.constlay);
        fragment.setVisibility(View.VISIBLE);

        fragment.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        Button button = (Button) findViewById(R.id.button); //TODO: functionality for this button
        Button button2 = (Button) findViewById(R.id.button2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendRef.child(userID).orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            addFriend(uid);

                        } else {
                            Toast.makeText(MapActivity.this, "You are already friends with this person!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, ChatActivity.class);
                intent.putExtra("userID", uid);
                startActivity(intent);
            }
        });

        return false;
    }

    private void addFriend(final String friendID) {
        final ArrayList<User> users = new ArrayList<>();
        users.clear();
        //  userRef.child(friendID).addListenerForSingleValueEvent(new ValueEventListener() {
        userRef.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    if (user.getUid().equals(userID)) {// || user.getUid().equals(friendID)){
                        users.add(0, user);
                    }
                    if (user.getUid().equals(friendID)) {
                        users.add(1, user);
                    }
                }
                if (users.size() == 2) {
                    // Add person(1)(friend) to person(0)s(this users) friend list
                    friendRef.child(users.get(0).getUid()).child(users.get(1).getUid()).setValue(users.get(1));
                    // Add person(0)(this user) to person(1)s(friend) friend list
                    friendRef.child(users.get(1).getUid()).child(users.get(0).getUid()).setValue(users.get(0));
                    Toast.makeText(MapActivity.this, "Added " + users.get(1).getName() + " to your friend list!", Toast.LENGTH_LONG).show();
                }
                /*
                if(dataSnapshot.exists()){
                    User user = dataSnapshot.getValue(User.class);
                    // Add friend
                    friendRef.child(userID).child(friendID).setValue(user);
                    // Make friend add you >:)
                    // friendRef.child(friendID).child(userID).setValue();
                    // Feedback <3
                }
                */
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMapClick(LatLng point) {
        if (markerFocus) {
            ConstraintLayout fragment = (ConstraintLayout) findViewById(R.id.constlay);
            fragment.setVisibility(View.INVISIBLE);

            if (currentMarker != null) {
                currentMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.if_ghost5));
                currentMarker.setAlpha(0.4f);
            }

            markerFocus = false;
        }
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
        this.mMap.setOnMarkerClickListener(this);
        this.mMap.setOnMapClickListener(this);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(60.7901781, 10.6834482), 15));
    }

    private void readFromDB() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                deleteMarkers(); //resets all markers - this is very unefficient but ye
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Spook spook = child.getValue(Spook.class);
                    addToMap(new LatLng(spook.getLatitude(), spook.getLongitude()), spook.getUserID(), currentDate - spook.getDate());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }


    private void setupMapAndLocation() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //check/request location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
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

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    private void setupFAB() {
        // FAB STUFF
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

                String key = myRef.push().getKey();
                myRef.child(key).setValue(new Spook(userID, currentLatLng.latitude, currentLatLng.longitude, currentDate));
                updateLocationToUserDB(currentLatLng.latitude, currentLatLng.longitude);
            }
        });
    }

    private void updateLocationToUserDB(double latitude, double longitude) {
        Log.d("FUCK BOI", "Latitude: " + latitude + " longitude: " + longitude);
        // TODO works sometimes :D

        DatabaseReference userRef = mDatabase.getReference("Users");//.child(userID);
        userRef.child(userID).child("lastLatitude").setValue(latitude);
        userRef.child(userID).child("lastLongitude").setValue(longitude);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MapActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
