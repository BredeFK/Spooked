package gamejam.spooked.com.spooked;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class register extends Fragment {
    private EditText editName;
    private EditText editEmail;
    private EditText editPass;
    private EditText editConfPass;
    private Button registerButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        initialise(view);

        return view;
    }

    private void initialise(View view){
        editName = view.findViewById(R.id.editName);
        editEmail = view.findViewById(R.id.editEmail);
        editPass = view.findViewById(R.id.editPassword);
        editConfPass = view.findViewById(R.id.editPassword2);
        registerButton = view.findViewById(R.id.btnRegister);
    }

}
