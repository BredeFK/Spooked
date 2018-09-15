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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterFragment extends Fragment {
    private static final String TAG = "register";
    private static final String userString = "Users";
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private EditText editName;
    private EditText editEmail;
    private EditText editPass;
    private EditText editConfPass;
    private Button registerButton;
    private FirebaseAuth auth;
    private FirebaseUser user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        initialise(view);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser(editName.getText().toString() ,editEmail.getText().toString()
                        , editPass.getText().toString(), editConfPass.getText().toString());

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

    private void createUser(final String name, String email, String password, String confPass) {
        // If none are empty
        if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty()){
            //  If passwords match
            if(password.equals(confPass)) {
                // Create user and add username/displayName
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "createUserWithEmail:success");
                                    addNameToUser(name);
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(getActivity(), "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(getActivity(), "Passwords must match!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "No fields can be blank", Toast.LENGTH_SHORT).show();
        }
    }

    private void addNameToUser(final String displayName){
        user = auth.getCurrentUser();

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName).build();

        user.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(), "Welcome, " + displayName + "!", Toast.LENGTH_SHORT).show();
                    addToDatabase();
                }
            }
        });
    }

    private void addToDatabase(){
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference(userString);

        String id = user.getUid(); String name = user.getDisplayName(); String email = user.getEmail();

        User newUser = new User(id, name, email);
        userRef.child(id).setValue(newUser);
    }

}
