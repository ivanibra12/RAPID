package ii954.csci314au19.fake_uber;

import  android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyVehicleActivity extends AppCompatActivity {

    //variable declaration
    private RecyclerView vehicleRecyclerView;
    private Button addVehicleButton;
    private VehicleAdapter vAdapter;
    private TextView emptyTextView;


    //side bar variable declaration
    private DrawerLayout myDrawerLayout;
    private ActionBarDrawerToggle mySideBar;

    //sidebar header variable declaration
    public TextView accountName;
    public TextView accountRating;

    //firebase database reference variable
    private DatabaseReference vehicleDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vehicle);

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
        emptyTextView = (TextView) findViewById(R.id.emptyTextView);
        addVehicleButton = (Button) findViewById(R.id.addVehicle);
        vehicleRecyclerView = (RecyclerView) findViewById(R.id.vehicleRecyclerView);
        vehicleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //vehicleRecyclerView.setHasFixedSize(true);


        //set listener when user click on vehicleRecyclerView
        vehicleRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, vehicleRecyclerView, new RecyclerItemClickListener.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(View view, int position)
                    {
                        //get car at selected position
                        Car newCar = UserMainActivity.userCars.get(position);

                        //call editVehicle function
                        editVehicle(newCar);
                    }

                    @Override
                    public void onLongItemClick(View view, int position)
                    {

                    }
                 }
        ));

        //set listener for addVehicleButton
        addVehicleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //call addVehicle function
                addVehicle();
            }
        });
    }

    /**********************************************************************************************/
    @Override
    protected void onStart() {
        super.onStart();

        //get vehicle database reference
        vehicleDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.vehicleDatabaseName).toString()).child(UserMainActivity.loggedInUser.getUID());

        //add listener to refresh recyclerView if there's changes in database
        vehicleDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                //load user cars
                vAdapter = new VehicleAdapter(getApplication(),UserMainActivity.userCars);
                vehicleRecyclerView.setAdapter(vAdapter);

                //if user have 0 cars
                if (UserMainActivity.userCars.size()==0)
                {
                    //display a toast and make emptyTextView visible
                    Toast.makeText(getApplicationContext(),"Please add a vehicle",Toast.LENGTH_LONG).show();
                    emptyTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

    }

    /**********************************************************************************************/
    //editVehicle function
    private void editVehicle(Car vehicle)
    {
        //Toast.makeText(getApplicationContext(),"EDIT",Toast.LENGTH_SHORT).show();

        //create new intent
        Intent newIntent = new Intent(MyVehicleActivity.this,EditVehicleActivity.class);

        //put extras into intent
        newIntent.putExtra("brand",vehicle.getBrand());
        newIntent.putExtra("model",vehicle.getModel());
        newIntent.putExtra("regnum",vehicle.getRegNum());
        newIntent.putExtra("insurancenum",vehicle.getInsuranceNum());
        newIntent.putExtra("nickname",vehicle.getNickname());
        newIntent.putExtra("year",vehicle.getYear());
        newIntent.putExtra("type",vehicle.getType());

        //go to edit vehicle page
        finish();
        startActivity(newIntent);
    }

    //addVehicle function
    private void addVehicle()
    {
        //Toast.makeText(getApplicationContext(),"ADD",Toast.LENGTH_SHORT).show();
        //go to add vehicle page
        finish();
        startActivity(new Intent(MyVehicleActivity.this,AddVehicleActivity.class));
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
                        startActivity(new Intent(MyVehicleActivity.this,UserMainActivity.class));
                    }
                    break;

                    case R.id.nav_account:
                    {
                        //go to edit account page
                        finish();
                        startActivity(new Intent(MyVehicleActivity.this,EditAccountActivity.class));
                    }
                    break;

                    case R.id.nav_vehicles:
                    {
                        //close side bar
                        myDrawerLayout.closeDrawer(Gravity.LEFT);
                    }
                    break;

                    case R.id.nav_history:
                    {
                        //go to history page
                        finish();
                        startActivity(new Intent(MyVehicleActivity.this,HistoryActivity.class));
                    }
                    break;

                    case R.id.nav_responses:
                    {
                        //go to responses page
                        finish();
                        startActivity(new Intent(MyVehicleActivity.this,ResponsesActivity.class));
                    }
                    break;

                    case R.id.nav_settings:
                    {
                        //go to settings page
                        finish();
                        startActivity(new Intent(MyVehicleActivity.this,SettingsActivity.class));
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
        startActivity(new Intent(MyVehicleActivity.this,UserMainActivity.class));
    }
    /**********************************************************************************************/





}
