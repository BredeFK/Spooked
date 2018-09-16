package gamejam.spooked.com.spooked;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

public class NearbyAdapter extends ArrayAdapter<User> {
    private Activity activity;
    private int id;
    private SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference friendsRef = database.getReference("Friends");
    private DatabaseReference userRef = database.getReference("Users");

    public NearbyAdapter(Context context, int id) {
        super(context, id);
        this.activity = (Activity) context;
        this.id = id;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        float lat = preferences.getFloat("lat", 0);
        float lon = preferences.getFloat("lon", 0);

        if (convertView == null) {
            convertView = this.activity.getLayoutInflater().inflate(this.id, parent, false);
        }

        final User user = this.getItem(position);
        if (user == null)
            throw new NullPointerException();

        TextView name = convertView.findViewById(R.id.nearName);
        TextView distance = convertView.findViewById(R.id.nearDistance);
        TextView town = convertView.findViewById(R.id.nearTown);
        String location = getTownFromLatAndLon(user.getLastLatitude(), user.getLastLongitude());
        String distanceInKM = distance(lat, lon, user.getLastLatitude(), user.getLastLongitude()) + "km";
        //ConstraintLayout layout = convertView.findViewById(R.id.constraintID);

        if (user.getName() != null) {
            name.setText(user.getName());
        }

        distance.setText(distanceInKM);
        town.setText(location);


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String thisUser = preferences.getString("thisUserID", "");
                userExists(thisUser, user);
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("userID", user.getUid());
                intent.putExtra("userName", user.getName());
                activity.startActivity(intent);
            }
        });

        return convertView;
    }

    private void addFriend(final User thisUser, final User friend) {
        // TODO make sure the friend is not already added
        friendsRef.child(thisUser.getUid()).orderByChild("uid").equalTo(friend.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // Add friend
                    friendsRef.child(thisUser.getUid()).child(friend.getUid()).setValue(friend);
                    // Make friend add you >:)
                    friendsRef.child(friend.getUid()).child(thisUser.getUid()).setValue(thisUser);
                    // Feedback <3
                    Toast.makeText(getContext(), "Added " + friend.getName() + " to friendlist :D", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("NearbyAdapter:addFriend", "Already friends with " + friend.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getTownFromLatAndLon(double lat, double lon) {
        String city = null;
        String county = null;
        String country = null;
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            city = addresses.get(0).getAdminArea();
            county = addresses.get(0).getSubAdminArea();
            country = addresses.get(0).getCountryName();

        } catch (Exception e) {
            Log.w("Address error", e.getMessage());
        }
        if (county != null && city != null)
            return county + ", " + city;
        else if (country != null)
            return "[no city], " + country;
        else if (city != null)
            return city + ", [no county]";
        else
            return "[no city]";
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

    private void userExists(final String thisUser, final User friend) {
        userRef.child(thisUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    addFriend(user, friend);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
