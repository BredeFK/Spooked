package gamejam.spooked.com.spooked;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onStart() {
        super.onStart();
        initialise();

        // User is not logged in
        if(user == null){
            Intent intent = new Intent(MainActivity.this, UserActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    private void initialise(){
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.action_logout){
            if(user != null)
                auth.signOut();
            Toast.makeText(this, "Logged out!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, UserActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_settings, menu);
        return true;
    }


    public void mapViewKjor(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

}
