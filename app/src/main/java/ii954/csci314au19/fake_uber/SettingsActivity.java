package ii954.csci314au19.fake_uber;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    //variable declaration
    private Button changeMembershipButton;
    private Button updateSecurityButton;
    private Button signOutButton;

    //side bar variable declaration
    private DrawerLayout myDrawerLayout;
    private ActionBarDrawerToggle mySideBar;

    //sidebar header variable declaration
    public TextView accountName;
    public TextView accountRating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //to get the menu icon and sidebar initialization
        myDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mySideBar = new ActionBarDrawerToggle(this,myDrawerLayout,R.string.open,R.string.close);
        myDrawerLayout.addDrawerListener(mySideBar);
        mySideBar.syncState();
        navListener();

        //sidebar header variable initialization
        NavigationView nav = (NavigationView) findViewById(R.id.sidebar);
        View navHeader = nav.getHeaderView(0);
        accountName = (TextView) navHeader.findViewById(R.id.header_nav_account_name);
        accountRating = (TextView) navHeader.findViewById(R.id.header_nav_account_rating);

        //display menu button to open side bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //load account details in side bar
        displayAccountDetails();



        //variable initialization
        changeMembershipButton = (Button) findViewById(R.id.changeMembershipButton);
        updateSecurityButton = (Button) findViewById(R.id.updateSecurityButton);
        signOutButton = (Button) findViewById(R.id.signOutButton);

        //set listener for changeMembershipButton
        changeMembershipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //go to changeMembership page
                finish();
                startActivity(new Intent(SettingsActivity.this,ChangeMembershipActivity.class));
            }
        });

        //set listener for updateSecurityButton
        updateSecurityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //go to updateSecurity page
                finish();
                startActivity(new Intent(SettingsActivity.this,UpdateSecurityActivity.class));
            }
        });

        //set listener for signOutButton
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //sign out user
                LoginActivity.firebaseAuth.signOut();

                //go to Login page
                finish();
                startActivity(new Intent(SettingsActivity.this,LoginActivity.class));
            }
        });

    }

    /**********************************************************************************************/
    //SIDEBAR

    //override function to open side bar when pressing home button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mySideBar.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //sidebar listener
    private void navListener()
    {
        NavigationView navView = (NavigationView) findViewById(R.id.sidebar);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.nav_home:
                    {
                        //go to user main menu page
                        finish();
                        startActivity(new Intent(SettingsActivity.this,UserMainActivity.class));
                    }
                    break;

                    case R.id.nav_account:
                    {
                        //go to account page
                        startActivity(new Intent(SettingsActivity.this,EditAccountActivity.class));
                    }
                    break;

                    case R.id.nav_vehicles:
                    {
                        //go to my vehicle page
                        finish();
                        startActivity(new Intent(SettingsActivity.this,MyVehicleActivity.class));
                    }
                    break;

                    case R.id.nav_history:
                    {
                        //go to history page
                        finish();
                        startActivity(new Intent(SettingsActivity.this,HistoryActivity.class));
                    }
                    break;

                    case R.id.nav_responses:
                    {
                        // go to responses page
                        finish();
                        startActivity(new Intent(SettingsActivity.this,ResponsesActivity.class));
                    }
                    break;

                    case R.id.nav_settings:
                    {
                        //close side bar
                        myDrawerLayout.closeDrawer(Gravity.LEFT);
                    }
                    break;
                }

                return true;
            }
        });
    }

    /**********************************************************************************************/
    //change account name in side bar with account name of userloggedin
    private void displayAccountDetails ()
    {
        if (UserMainActivity.loggedInUser==null){
            accountName.setText("User name default");
            accountRating.setText("5.0");

        }
        else {
            accountName.setText(UserMainActivity.loggedInUser.getFirstName() + " " + UserMainActivity.loggedInUser.getLastName());
            accountRating.setText(String.format("%.1f",((double)UserMainActivity.loggedInUser.getRating()/(double)UserMainActivity.loggedInUser.getTotalRating())));
        }
    }

    /**********************************************************************************************/
    @Override
    public void onBackPressed() {

        //override and go to user main menu page
        finish();
        startActivity(new Intent(SettingsActivity.this,UserMainActivity.class));
    }
    /**********************************************************************************************/
}
