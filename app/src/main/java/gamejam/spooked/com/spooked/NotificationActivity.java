package gamejam.spooked.com.spooked;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificationActivity extends IntentService {

    private NotificationChannel channel;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private PendingIntent pendingIntent;
    private SharedPreferences preferences;

    private Context context = this;

    private String userID;
    private String sendersUserID;

    private boolean boot = true;

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = mDatabase.getReference("Messages");
    private DatabaseReference ref2;

    public NotificationActivity(){super("NotficationActivity");}

    @Override
    public void onHandleIntent(@Nullable Intent intent) {

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userID = preferences.getString("thisUserID", "REEEEEEEEEEEEEEEEEEEEEEEEEEEE");


        this.notificationManager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);

        // Create the NotificationChannel only on API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.channel = new NotificationChannel(
                    context.getString(R.string.channel_id),
                    context.getString(R.string.channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(context.getString(R.string.channel_description));

            // Register the channel with the system
            notificationManager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(context, channel.getId());
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child: dataSnapshot.getChildren()) {
                    if (child.getKey().contains(userID)) {

                        ref2 = child.getRef();
                        ref2.addValueEventListener(new ValueEventListener() {


                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int messages = 0;

                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    if (!child.getValue(Message.class).getUserID().equals(userID)) {
                                        messages++;
                                        sendersUserID = child.getValue(Message.class).getUserID();
                                    }
                                }

                                if (boot) {
                                    preferences.edit().putInt(ref2.getKey(), messages).apply();
                                    boot = false;
                                }else{

                                    int oldCount = preferences.getInt(ref2.getKey(), 0);

                                    if (messages > oldCount) {
                                        sendNotification(messages - oldCount);
                                        preferences.edit().putInt(ref2.getKey(), messages).apply();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                //rip
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // rip
            }
        });

    }

    private void sendNotification(int diff) {
        Intent mainIntent = new Intent(NotificationActivity.this, ChatActivity.class);
        mainIntent.putExtra("userID", sendersUserID); //TODO: WHY DOESNT IT WORK
        Log.d("FUCK YOU", "sendNotification: "+ sendersUserID);
        pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);

        // Build the Notification
        this.builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(diff+" New messages!")
                .setContentText("Check them out now")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(this.pendingIntent)
                .setAutoCancel(true);
        this.notificationManager.notify(0, builder.build());

    }
}