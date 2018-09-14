package gamejam.spooked.com.spooked;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialise();

        // User is not logged in
        if(user == null){
            Intent intent = new Intent(MainActivity.this, UserActivity.class);
            startActivity(intent);
        }
    }

    private void initialise(){
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }


    public void mapViewKjor(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

}
