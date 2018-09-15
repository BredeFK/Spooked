package gamejam.spooked.com.spooked;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class register extends Fragment {
    private static final String TAG = "register";
    private EditText editName;
    private EditText editEmail;
    private EditText editPass;
    private EditText editConfPass;
    private Button registerButton;
    private FirebaseAuth auth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        initialise(view);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser(editName.getText().toString() ,editEmail.getText().toString()
                        , editPass.getText().toString());

            }
        });

        return view;
    }

    private void initialise(View view) {
        editName = view.findViewById(R.id.editName);
        editEmail = view.findViewById(R.id.editEmail);
        editPass = view.findViewById(R.id.editPassword);
        editConfPass = view.findViewById(R.id.editPassword2);
        registerButton = view.findViewById(R.id.btnRegister);
        auth = FirebaseAuth.getInstance();
    }

    private void createUser(String name, String email, String password) {
        // If none are empty
        if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty()){
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        // TODO add displayname
                        Log.d(TAG, "createUserWithEmail:success");
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());

                    }
                }
            });
        }
    }
}
