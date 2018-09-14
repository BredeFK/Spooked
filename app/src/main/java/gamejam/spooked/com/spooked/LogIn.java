package gamejam.spooked.com.spooked;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class LogIn extends Fragment {
    private EditText editEmail;
    private EditText editpass;
    private Button buttonLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_in, container, false);
        initialise(view);

        return view;
    }

    private void initialise(View view){
        editEmail = view.findViewById(R.id.editEmailLogin);
        editpass = view.findViewById(R.id.editPasswordLogin);
        buttonLogin = view.findViewById(R.id.btnLogin);

    }



}
