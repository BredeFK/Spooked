package gamejam.spooked.com.spooked;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class ChatActivity extends AppCompatActivity {
    private String friendID;
    private DatabaseReference messageRef;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ListView listMessages;
    private Button sendButton;
    private EditText newMessage;
    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // String friendName = getIntent().getStringExtra("userName");
        // setTitle(friendName);
        friendID = getIntent().getStringExtra("userID");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        messageRef = FirebaseDatabase.getInstance().getReference("Messages");
        listMessages = findViewById(R.id.messageListID);
        sendButton = findViewById(R.id.sendID);
        newMessage = findViewById(R.id.messageID);
        adapter = new MessageAdapter(ChatActivity.this, R.layout.message_received);

        fetchMessagesListener();
        sendMessageListener();
    }

    private void fetchMessagesListener(){
        messageRef.child(generateId(user.getUid(), friendID)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clear();
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    Message message = child.getValue(Message.class);
                    adapter.add(message);
                }
                listMessages.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w("ChatActivity", "Failed to read value in messageList.", databaseError.toException());

            }
        });
    }

    private void sendMessageListener(){
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!newMessage.getText().toString().isEmpty() && user != null){
                    long date = Calendar.getInstance().getTimeInMillis();
                    Message message = new Message(user.getDisplayName(), user.getUid(), newMessage.getText().toString(), date);
                    String messageKey = messageRef.push().getKey();
                    messageRef.child(generateId(user.getUid(), friendID)).child(messageKey).setValue(message);
                    newMessage.setText("");
                } else {
                    Toast.makeText(ChatActivity.this, "Message can not be empty and user might not be logged in!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private String generateId(String thisUser, String friend){
        // Sort alphabetical :)
        if(thisUser.compareTo(friend) > 0){
            return thisUser + friend;
        } else {
            return friend + thisUser;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.action_logout){
            if(user != null)
                auth.signOut();
            Toast.makeText(this, "Logged out!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ChatActivity.this, UserActivity.class);
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
