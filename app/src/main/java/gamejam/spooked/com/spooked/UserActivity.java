package gamejam.spooked.com.spooked;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        initialise();
    }

    private void initialise(){
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        // TODO : make resource string
        String logIn = "Log in";
        String register = "Register";

        PageAdapter pageAdapter = new PageAdapter(getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.container);
        TabLayout layout = findViewById(R.id.tabs);

        pageAdapter.addFragment(new LogIn(), logIn);
        pageAdapter.addFragment(new register(), register);
        viewPager.setAdapter(pageAdapter);

        layout.setupWithViewPager(viewPager);
    }
}
