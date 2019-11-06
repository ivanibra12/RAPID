package ii954.csci314au19.fake_uber;

import android.content.Intent;
import android.net.Uri;
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
import android.widget.FrameLayout;
import android.widget.TextView;

public class JobActivity extends AppCompatActivity {

    //variable declaration
    private Job ongoingJob;

    private TextView firstnameTextView;
    private TextView lastnameTextView;
    private TextView mobileNumberTextView;
    private TextView distanceTextView;
    private TextView vehicleBrandTextView;
    private TextView vehicleModelTextView;
    private TextView vehicleTypeTextView;
    private TextView vehicleYearTextView;
    private TextView vehicleRegnumTextView;
    private TextView vehicelInsurancenumTextView;
    private TextView problemTextView;
    private TextView problemDescriptionTextView;
    private Button navigateButton;
    private Button finishJobButton;
    private Button problemUnsolvedButton;
    private FrameLayout jobFrameLayout;
    private TextView emptyTextView;

    //side bar variable declaration
    private DrawerLayout myDrawerLayout;
    private ActionBarDrawerToggle mySideBar;

    //sidebar header variable declaration
    public TextView accountName;
    public TextView accountRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);

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
        ongoingJob = ProfessionalMainActivity.ongoingJob;

        firstnameTextView = (TextView) findViewById(R.id.firstnameTextView);
        lastnameTextView = (TextView) findViewById(R.id.lastnameTextView);
        mobileNumberTextView = (TextView) findViewById(R.id.mobileNumberTextView);
        distanceTextView = (TextView) findViewById(R.id.distanceTextView);
        vehicleBrandTextView = (TextView) findViewById(R.id.vehicleBrandTextView);
        vehicleModelTextView = (TextView) findViewById(R.id.vehicleModelTextView);
        vehicleTypeTextView = (TextView) findViewById(R.id.vehicleTypeTextView);
        vehicleYearTextView = (TextView) findViewById(R.id.vehicleYearTextView);
        vehicleRegnumTextView = (TextView) findViewById(R.id.vehicleRegnumTextView);
        vehicelInsurancenumTextView = (TextView) findViewById(R.id.vehicleInsurancenumTextView);
        problemTextView = (TextView) findViewById(R.id.problemTextView);
        problemDescriptionTextView = (TextView) findViewById(R.id.problemDescriptionTextView);
        navigateButton = (Button) findViewById(R.id.navigateButton);
        finishJobButton = (Button) findViewById(R.id.finishJobButton);
        problemUnsolvedButton = (Button) findViewById(R.id.problemUnsolvedButton);
        jobFrameLayout = (FrameLayout) findViewById(R.id.jobFrameLayout);
        emptyTextView = (TextView) findViewById(R.id.emptyTextView);

        //onclick listener for navigateButton
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                goToGoogleMaps();
            }
        });

        //onclick listener for finishJobButton
        finishJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                finishJob();
            }
        });

        //onclick listener for problemUnsolvedButton
        problemUnsolvedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                problemUnsolved();
            }
        });

        //load details
        loadDetails();
    }

    /**********************************************************************************************/
    //finish job method
    private void finishJob()
    {
        ProfessionalMainActivity.ongoingJob.setStatus("Solved");
        goToJobFinishActivity();
    }

    /**********************************************************************************************/
    //problemUnsolved method
    private void problemUnsolved()
    {
        ProfessionalMainActivity.ongoingJob.setStatus("Unsolved");
        goToJobFinishActivity();
    }

    /**********************************************************************************************/
    //goToJobFinishActivity method
    private void goToJobFinishActivity()
    {
        Intent intent = new Intent(JobActivity.this,JobFinishActivity.class);

        finish();
        startActivity(intent);
    }

    /**********************************************************************************************/
    //go to Google Maps method
    private void goToGoogleMaps()
    {
        String uri = "http://maps.google.com/maps?&daddr=" + ongoingJob.getRequest().getLocationLatitude() + "," + ongoingJob.getRequest().getLocationLongitude();

        //for navigation
        //String uri = "http://maps.google.com/maps?saddr=" + sourceLatitude + "," + sourceLongitude + "&daddr=" + destinationLatitude + "," + destinationLongitude;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    /**********************************************************************************************/
    //load details method
    private void loadDetails()
    {
        if (ongoingJob!=null)
        {
            emptyTextView.setVisibility(View.INVISIBLE);
            jobFrameLayout.setVisibility(View.VISIBLE);

            firstnameTextView.setText("First name : " + ongoingJob.getRequest().getUser().getFirstName());
            lastnameTextView.setText("Last name : " + ongoingJob.getRequest().getUser().getLastName());
            mobileNumberTextView.setText("Mobile number : " + ongoingJob.getRequest().getUser().getMobileNo());
            distanceTextView.setText("Distance : " + ongoingJob.getResponse().getDistance());
            vehicleBrandTextView.setText("Brand : " + ongoingJob.getRequest().getVehicle().getBrand());
            vehicleModelTextView.setText("Model : " + ongoingJob.getRequest().getVehicle().getModel());
            vehicleTypeTextView.setText("Type : " + ongoingJob.getRequest().getVehicle().getType());
            vehicleYearTextView.setText("Year : " + ongoingJob.getRequest().getVehicle().getYear());
            vehicleRegnumTextView.setText("Registration number : " + ongoingJob.getRequest().getVehicle().getRegNum());
            vehicelInsurancenumTextView.setText("Insurance number : " + ongoingJob.getRequest().getVehicle().getInsuranceNum());
            problemTextView.setText(ongoingJob.getRequest().getProblem());
            if (ongoingJob.getRequest().getAdditionalNote().equalsIgnoreCase("")) {
                problemDescriptionTextView.setText("Nothing");
            } else {
                problemDescriptionTextView.setText(ongoingJob.getRequest().getAdditionalNote());
            }
        }
        else
        {
            emptyTextView.setVisibility(View.VISIBLE);
            jobFrameLayout.setVisibility(View.INVISIBLE);
        }
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
                        //go to Edit Account Professional activity
                        finish();
                        startActivity(new Intent(JobActivity.this,EditAccountProfessionalActivity.class));
                    }
                    break;

                    case R.id.nav_history:
                    {
                        //go to Edit History Professional activity
                        finish();
                        startActivity(new Intent(JobActivity.this,HistoryProfessionalActivity.class));
                    }
                    break;

                    case R.id.nav_jobs:
                    {
                        //close side bar
                        myDrawerLayout.closeDrawer(Gravity.LEFT);
                    }
                    break;

                    case R.id.nav_settings:
                    {
                        //go to settings page
                        finish();
                        startActivity(new Intent(JobActivity.this,SettingsProfessionalActivity.class));
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
