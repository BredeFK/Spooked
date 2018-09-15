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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LogIn extends Fragment {
    private static final String TAG = "LogIn";
    private EditText editEmail;
    private EditText editpass;
    private Button buttonLogin;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_in, container, false);
        initialise(view);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(editEmail.getText().toString(), editpass.getText().toString());
            }
        });

        return view;
    }

    private void initialise(View view){
        editEmail = view.findViewById(R.id.editEmailLogin);
        editpass = view.findViewById(R.id.editPasswordLogin);
        buttonLogin = view.findViewById(R.id.btnLogin);
        auth = FirebaseAuth.getInstance();
    }

    private void loginUser(String email, String password){
        if(!email.isEmpty() && !password.isEmpty()){
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "createUserWithEmail:success");
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(getActivity(), "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

        } else {
            Toast.makeText(getActivity(), "No fields can be blank!", Toast.LENGTH_SHORT).show();
        }
    }



}
