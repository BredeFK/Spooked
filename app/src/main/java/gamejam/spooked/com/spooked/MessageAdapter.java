// Author: Brede Fritjof Klausen

package gamejam.spooked.com.spooked;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MessageAdapter extends ArrayAdapter<Message> {
    private Activity activity;
    private SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

    public MessageAdapter(Context context, int id) {
        super(context, id );
        this.activity = (Activity) context;
    }

    @Override
    @NonNull
    public View getView(int pos, View convertView, @NonNull ViewGroup container) {

        String thisUser = preferences.getString("thisUserID", "");
        Message messageRow = this.getItem(pos);
        if (messageRow == null) {
            throw new NullPointerException();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String convertedDate = sdf.format(messageRow.date);

        TextView user, message, date;

        // If the message is sent from this device
        if (messageRow.userID.equals(thisUser)) {
            convertView = this.activity.getLayoutInflater().inflate(R.layout.message_sent, container, false);
            user = convertView.findViewById(R.id.user_s_ID);
            message = convertView.findViewById(R.id.message_s_ID);
            date = convertView.findViewById(R.id.date_s_ID);
        } else {

            // else the message is received from another device
            convertView = this.activity.getLayoutInflater().inflate(R.layout.message_received, container, false);
            user = convertView.findViewById(R.id.user_r_ID);
            message = convertView.findViewById(R.id.message_r_ID);
            date = convertView.findViewById(R.id.date_r_ID);
        }


        user.setText(messageRow.userName);


        message.setText(messageRow.message);
        date.setText(convertedDate);

        return convertView;
    }
}
