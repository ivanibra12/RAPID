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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;

public class JobOngoingActivity extends AppCompatActivity {

    //variable declaration
    private TextView requestIDTextView;
    private TextView vehicleTextView;
    private TextView requestDateTextView;
    private TextView requestTimeTextView;
    private TextView problemTextView;
    private TextView problemDescTextView;

    private TextView firstnameTextView;
    private TextView lastnameTextView;
    private TextView mobileNumberTextView;
    private TextView distanceTextView;

    private TextView statusTextView;

    private LinearLayout ratingLinearLayout;
    private ImageView star1;
    private ImageView star2;
    private ImageView star3;
    private ImageView star4;
    private ImageView star5;

    private Button submitRatingButton;

    private Job ongoingJob;
    private SimpleDateFormat simpleDateFormat;
    private int star;

    //side bar variable declaration
    private DrawerLayout myDrawerLayout;
    private ActionBarDrawerToggle mySideBar;

    //sidebar header variable declaration
    public TextView accountName;
    public TextView accountRating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_ongoing);

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

        //variable initialization
        requestIDTextView = (TextView) findViewById(R.id.requestIDTextView);
        vehicleTextView = (TextView) findViewById(R.id.vehicleTextView);
        requestDateTextView = (TextView) findViewById(R.id.requestDateTextView);
        requestTimeTextView = (TextView) findViewById(R.id.requestTimeTextView);
        problemTextView = (TextView) findViewById(R.id.problemTextView);
        problemDescTextView = (TextView) findViewById(R.id.problemDescTextView);

        firstnameTextView = (TextView) findViewById(R.id.firstnameTextView);
        lastnameTextView = (TextView) findViewById(R.id.lastnameTextView);
        mobileNumberTextView = (TextView) findViewById(R.id.mobileNumberTextView);
        distanceTextView = (TextView) findViewById(R.id.distanceTextView);

        statusTextView = (TextView) findViewById(R.id.statusTextView);

        ratingLinearLayout = (LinearLayout) findViewById(R.id.ratingLinearLayout);
        star1 = (ImageView) findViewById(R.id.star1);
        star2 = (ImageView) findViewById(R.id.star2);
        star3 = (ImageView) findViewById(R.id.star3);
        star4 = (ImageView) findViewById(R.id.star4);
        star5 = (ImageView) findViewById(R.id.star5);

        submitRatingButton = (Button) findViewById(R.id.submitRatingButton);

        ongoingJob = UserMainActivity.ongoingJob;
        simpleDateFormat = null;
        star = 5;

        //onclick listener for stars
        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                star = 1;
                star1.setImageResource(R.mipmap.baseline_star_black_36dp);
                star2.setImageResource(R.mipmap.baseline_star_border_black_36dp);
                star3.setImageResource(R.mipmap.baseline_star_border_black_36dp);
                star4.setImageResource(R.mipmap.baseline_star_border_black_36dp);
                star5.setImageResource(R.mipmap.baseline_star_border_black_36dp);
                Toast.makeText(getApplicationContext(),star+" star",Toast.LENGTH_SHORT).show();
            }
        });
        star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                star = 2;
                star1.setImageResource(R.mipmap.baseline_star_black_36dp);
                star2.setImageResource(R.mipmap.baseline_star_black_36dp);
                star3.setImageResource(R.mipmap.baseline_star_border_black_36dp);
                star4.setImageResource(R.mipmap.baseline_star_border_black_36dp);
                star5.setImageResource(R.mipmap.baseline_star_border_black_36dp);
                Toast.makeText(getApplicationContext(),star+" star",Toast.LENGTH_SHORT).show();
            }
        });
        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                star = 3;
                star1.setImageResource(R.mipmap.baseline_star_black_36dp);
                star2.setImageResource(R.mipmap.baseline_star_black_36dp);
                star3.setImageResource(R.mipmap.baseline_star_black_36dp);
                star4.setImageResource(R.mipmap.baseline_star_border_black_36dp);
                star5.setImageResource(R.mipmap.baseline_star_border_black_36dp);
                Toast.makeText(getApplicationContext(),star+" star",Toast.LENGTH_SHORT).show();
            }
        });
        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                star = 4;
                star1.setImageResource(R.mipmap.baseline_star_black_36dp);
                star2.setImageResource(R.mipmap.baseline_star_black_36dp);
                star3.setImageResource(R.mipmap.baseline_star_black_36dp);
                star4.setImageResource(R.mipmap.baseline_star_black_36dp);
                star5.setImageResource(R.mipmap.baseline_star_border_black_36dp);
                Toast.makeText(getApplicationContext(),star+" star",Toast.LENGTH_SHORT).show();
            }
        });
        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                star = 5;
                star1.setImageResource(R.mipmap.baseline_star_black_36dp);
                star2.setImageResource(R.mipmap.baseline_star_black_36dp);
                star3.setImageResource(R.mipmap.baseline_star_black_36dp);
                star4.setImageResource(R.mipmap.baseline_star_black_36dp);
                star5.setImageResource(R.mipmap.baseline_star_black_36dp);
                Toast.makeText(getApplicationContext(),star+" star",Toast.LENGTH_SHORT).show();
            }
        });

        //onclick listener for submitRatingButton
        submitRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                submitRating();
            }
        });

        //loadDetails
        loadDetails();

    }

    /**********************************************************************************************/
    //submit rating method
    private void submitRating()
    {
        Professional professional = ongoingJob.getResponse().getProfessional();
        professional.setRating(professional.getRating()+(double)star);
        professional.setTotalRating(professional.getTotalRating()+1);

        //save updated professional details in database
        DatabaseReference userJobDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.userJobDatabaseName).toString()).child(ongoingJob.getRequest().getUser().getUID());
        DatabaseReference ongoingJobDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.ongoingJobDatabaseName).toString());
        DatabaseReference professionalDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.professionalDatabaseName).toString()).child(professional.getpID());

        userJobDatabase.child(ongoingJob.getJobid()).setValue(ongoingJob);
        ongoingJobDatabase.child(ongoingJob.getJobid()).setValue(null);
        professionalDatabase.setValue(professional);

        Request request = UserMainActivity.ongoingRequest;
        request.setStatus(ongoingJob.getStatus());

        //save updated request details in database
        DatabaseReference userRequestDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.userRequestDatabaseName).toString()).child(request.getUser().getUID());
        DatabaseReference requestDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.requestDatabaseName).toString());
        DatabaseReference ongoingRequestDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.ongoingRequestDatabaseName).toString());

        userRequestDatabase.child(request.getRequestID()).setValue(request);
        requestDatabase.child(request.getRequestID()).setValue(request);
        ongoingRequestDatabase.child(request.getRequestID()).setValue(null);

        UserMainActivity.ongoingJob = null;
        UserMainActivity.ongoingRequest = null;
        onBackPressed();
    }

    /**********************************************************************************************/
    //load details method
    private void loadDetails()
    {
        requestIDTextView.setText("Request ID : " + ongoingJob.getRequest().getRequestID());
        vehicleTextView.setText("Vehicle : " + ongoingJob.getRequest().getVehicle().getNickname() + " (" + ongoingJob.getRequest().getVehicle().getRegNum() + ")");
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        requestDateTextView.setText("Request date : " + simpleDateFormat.format(ongoingJob.getRequest().getRequestDateTime()));
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        requestTimeTextView.setText("Request time : " + simpleDateFormat.format(ongoingJob.getRequest().getRequestDateTime()));
        problemTextView.setText("Problem : " + ongoingJob.getRequest().getProblem());
        problemDescTextView.setText("Additional note : " + ongoingJob.getRequest().getAdditionalNote());

        firstnameTextView.setText("First name : "+ongoingJob.getResponse().getProfessional().getFirstName());
        lastnameTextView.setText("Last name : "+ongoingJob.getResponse().getProfessional().getLastName());
        mobileNumberTextView.setText("Mobile number : "+ongoingJob.getResponse().getProfessional().getMobileNo());
        distanceTextView.setText("Distance : "+ongoingJob.getResponse().getDistance());

        statusTextView.setText(ongoingJob.getStatus());

        if (ongoingJob.getStatus().equalsIgnoreCase("ongoing"))
        {
            ratingLinearLayout.setVisibility(View.INVISIBLE);
            submitRatingButton.setVisibility(View.INVISIBLE);
        }
        else
        {
            ratingLinearLayout.setVisibility(View.VISIBLE);
            submitRatingButton.setVisibility(View.VISIBLE);
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
                        startActivity(new Intent(JobOngoingActivity.this,UserMainActivity.class));
                    }
                    break;

                    case R.id.nav_account:
                    {
                        //go to account page
                        startActivity(new Intent(JobOngoingActivity.this,EditAccountActivity.class));
                    }
                    break;

                    case R.id.nav_vehicles:
                    {
                        //go to my vehicle page
                        finish();
                        startActivity(new Intent(JobOngoingActivity.this,MyVehicleActivity.class));
                    }
                    break;

                    case R.id.nav_history:
                    {
                        //go to history page
                        finish();
                        startActivity(new Intent(JobOngoingActivity.this,HistoryActivity.class));
                    }
                    break;

                    case R.id.nav_responses:
                    {
                        //close side bar
                        myDrawerLayout.closeDrawer(Gravity.LEFT);
                    }
                    break;

                    case R.id.nav_settings:
                    {
                        //go to settings activity
                        finish();
                        startActivity(new Intent(JobOngoingActivity.this,SettingsActivity.class));
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
        startActivity(new Intent(JobOngoingActivity.this,UserMainActivity.class));
    }
    /**********************************************************************************************/
}
