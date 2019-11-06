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

public class SettingsProfessionalActivity extends AppCompatActivity {

    //variable declaration
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
        setContentView(R.layout.activity_settings_professional);

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
        updateSecurityButton = (Button) findViewById(R.id.updateSecurityButton);
        signOutButton = (Button) findViewById(R.id.signOutButton);

        //set listener for updateSecurityButton
        updateSecurityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //go to updateSecurity page
                finish();
                startActivity(new Intent(SettingsProfessionalActivity.this,UpdateSecurityProfessionalActivity.class));
            }
        });

        //set listener for signOutButton
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //sign out user
                LoginActivity.firebaseAuth.signOut();

                //go to Login page
                ProfessionalMainActivity.activity.finish();
                finish();
                startActivity(new Intent(SettingsProfessionalActivity.this,LoginActivity.class));
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
                        onBackPressed();
                    }
                    break;

                    case R.id.nav_account:
                    {
                        //go to account page
                        finish();
                        startActivity(new Intent(SettingsProfessionalActivity.this,EditAccountProfessionalActivity.class));
                    }
                    break;

                    case R.id.nav_history:
                    {
                        //go to history professional page
                        finish();
                        startActivity(new Intent(SettingsProfessionalActivity.this,HistoryProfessionalActivity.class));
                    }
                    break;

                    case R.id.nav_jobs:
                    {
                        //go to job activity
                        finish();
                        startActivity(new Intent(SettingsProfessionalActivity.this,JobActivity.class));
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
        if (ProfessionalMainActivity.loggedInUser==null){
            accountName.setText("User name default");
            accountRating.setText("5.0");
        }
        else {
            accountName.setText(ProfessionalMainActivity.loggedInUser.getFirstName() + " " + ProfessionalMainActivity.loggedInUser.getLastName());
            accountRating.setText(String.format("%.1f",((double)ProfessionalMainActivity.loggedInUser.getRating()/(double)ProfessionalMainActivity.loggedInUser.getTotalRating())));
        }
    }

    /**********************************************************************************************/
    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
    /**********************************************************************************************/
}
