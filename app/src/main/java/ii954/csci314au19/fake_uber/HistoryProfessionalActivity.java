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

public class HistoryProfessionalActivity extends AppCompatActivity {

    //sidebar header variable declaration
    public TextView accountName;
    public TextView accountRating;

    //side bar variable declaration
    private DrawerLayout myDrawerLayout;
    private ActionBarDrawerToggle mySideBar;

    //variable declaration
    private Button jobHistoryButton;
    private Button transactionHistoryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_professional);

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
        jobHistoryButton = (Button) findViewById(R.id.jobHistoryButton);
        transactionHistoryButton = (Button) findViewById(R.id.transactionHistoryButton);

        //onclick listener for jobHistoryButton
        jobHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                finish();
                startActivity(new Intent(HistoryProfessionalActivity.this,JobHistoryProfessionalActivity.class));
            }
        });

        //onclick listener for transactionHistoryButton
        transactionHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                finish();
                startActivity(new Intent(HistoryProfessionalActivity.this,TransactionHistoryProfessionalActivity.class));
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
                        //go to edit account professional activity
                        finish();
                        startActivity(new Intent(HistoryProfessionalActivity.this,EditAccountProfessionalActivity.class));
                    }
                    break;

                    case R.id.nav_history:
                    {
                        //close side bar
                        myDrawerLayout.closeDrawer(Gravity.LEFT);
                    }
                    break;

                    case R.id.nav_jobs:
                    {
                        //go to job activity
                        finish();
                        startActivity(new Intent(HistoryProfessionalActivity.this,JobActivity.class));
                    }
                    break;

                    case R.id.nav_settings:
                    {
                        //go to settings page
                        finish();
                        startActivity(new Intent(HistoryProfessionalActivity.this,SettingsProfessionalActivity.class));
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
