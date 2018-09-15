package gamejam.spooked.com.spooked;

import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

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
    private double lat = 60.814858;
    private double lon = 11.060269;
    private double distance;

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
        listNearby = findViewById(R.id.nearbyListView);
        adapter = new NearbyAdapter(this, R.layout.nearby_item);
        setupListView();

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
                        //adapter.add(nearbyUser);
                        }
                    }
                }
                sortList();
               // listNearby.setAdapter(adapter);
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

        // Sort by highest distance
        while (sortedUserList.size() != size) {
            for (int i = 0; i < userList.size(); i++) {
                distance = distance(lat, lon, userList.get(i).getLastLatitude(), userList.get(i).getLastLongitude());
                if (distance > highest) {
                    highest = distance;
                    index = i;
                }
                if(i == userList.size()-1){
                    sortedUserList.add(userList.get(index));
                    userList.remove(index);
                    highest = 0;
                }
            }
        }

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
