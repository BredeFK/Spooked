package gamejam.spooked.com.spooked;

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

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SpookedNearbyActivity extends AppCompatActivity {
    private ArrayList<User> userList = new ArrayList<>();
    private static final String userString = "Users";
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private FirebaseUser user;
    private ListView listNearby;
    private NearbyAdapter adapter;

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
        Log.d("DISTANCE", "" + distance(0,0, 50,50));
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    User nearbyUser = child.getValue(User.class);
                    if(nearbyUser != null){
                     if(!nearbyUser.getUid().equals(user.getUid()) && nearbyUser.hasLocation()){
                        adapter.add(nearbyUser);
                        }
                    }
                }
                listNearby.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Rip :(
            }
        });
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

        // Format to only two decimal points
        DecimalFormat df = new DecimalFormat(".##");
        return Double.parseDouble(df.format(dist));
    }
    // Source done

}
