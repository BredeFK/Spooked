package gamejam.spooked.com.spooked;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
        actionButton.setImageResource(R.drawable.baseline_search_black_24);
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
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if(user != null)
            fillListView(user.getUid());
        else
            Toast.makeText(this, "Could not get friends :(", Toast.LENGTH_LONG).show();
    }

    private void fillListView(String Uid){
        friendRef.child(Uid).orderByValue().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendArray.clear();
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
}
