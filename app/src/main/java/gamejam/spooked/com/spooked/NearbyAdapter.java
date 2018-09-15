package gamejam.spooked.com.spooked;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NearbyAdapter extends ArrayAdapter<User> {
    private Activity activity;
    private int id;

    public NearbyAdapter(Context context, int id){
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

        TextView name = convertView.findViewById(R.id.nearName);
        TextView distance = convertView.findViewById(R.id.nearDistance);
        TextView town = convertView.findViewById(R.id.nearTown);



        if (user.getName() != null){
            name.setText(user.getName());
        }

        distance.setText("1.1km");
        town.setText("Gjøvik, Oppland");

        return convertView;
    }
}
