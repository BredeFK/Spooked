package gamejam.spooked.com.spooked;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendListActivity extends AppCompatActivity {
    private ListView friendList;
    private FriendListAdapter adapter;
    private FirebaseDatabase db;
    private DatabaseReference friendRef;
    private FirebaseUser user;
    private FirebaseAuth auth;

    private static final String friends = "Friends";
    private ArrayList<User> friendArray = new ArrayList<>();

    // TODO add refresh?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton actionButton = (FloatingActionButton) findViewById(R.id.fab);
        actionButton.setImageResource(R.drawable.baseline_search_white_48);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FriendListActivity.this, SpookedNearbyActivity.class);
                startActivity(intent);
            }
        });

        setupListView();

    }

    private void setupListView(){
        friendList = findViewById(R.id.friendList);
        adapter = new FriendListAdapter(this, R.layout.friend_item);
        db = FirebaseDatabase.getInstance();
        friendRef = db.getReference(friends);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if(user != null)
            fillListView(user.getUid());
        else
            Toast.makeText(this, "Could not get friends :(", Toast.LENGTH_LONG).show();
    }

    private void fillListView(String Uid){
        // Order alphabetical
        friendRef.child(Uid).orderByChild("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendArray.clear();
                adapter.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    User friend = child.getValue(User.class);
                    adapter.add(friend);
                }
                friendList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Rip :(
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.action_logout){
            if(user != null)
                auth.signOut();
            Toast.makeText(this, "Logged out!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(FriendListActivity.this, UserActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_settings, menu);
        return true;
    }
}
