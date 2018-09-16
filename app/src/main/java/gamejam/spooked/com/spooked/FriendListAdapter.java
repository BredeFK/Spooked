package gamejam.spooked.com.spooked;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FriendListAdapter extends ArrayAdapter<User> {
    private Activity activity;
    private int id;

    public FriendListAdapter(Context context, int id){
        super(context,id);
        this.activity = (Activity)context;
        this.id = id;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = this.activity.getLayoutInflater().inflate(this.id, parent, false);
        }

        User user = this.getItem(position);
        if(user == null)
            throw new NullPointerException();

       // String row = this.getItem(position);
        TextView friend = convertView.findViewById(R.id.friendID);

        if (user.getName() != null){
            friend.setText(user.getName());
        }

        return convertView;
    }

    


}
