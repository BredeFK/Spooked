package gamejam.spooked.com.spooked;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

        final User user = this.getItem(position);
        if(user == null)
            throw new NullPointerException();

       // String row = this.getItem(position);
        TextView friend = convertView.findViewById(R.id.friendID);

        if (user.getName() != null){
            friend.setText(user.getName());
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("userID", user.getUid());
                intent.putExtra("userName", user.getName());
                activity.startActivity(intent);
            }
        });

        return convertView;
    }


    


}
