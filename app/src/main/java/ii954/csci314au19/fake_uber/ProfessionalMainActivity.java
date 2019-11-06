package ii954.csci314au19.fake_uber;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ProfessionalMainActivity extends AppCompatActivity {

    //sidebar header variable declaration
    public TextView accountName;
    public TextView accountRating;

    //side bar variable declaration
    private DrawerLayout myDrawerLayout;
    private ActionBarDrawerToggle mySideBar;

    //variable declaration
    public static ProgressDialog initializingProgressDialog;
    public static ProgressDialog initializingLocationProgressDialog;
    LocationManager locationManager;
    private String currentLongitude;
    private String currentLatitude;
    private boolean locationEnabled;
    private RecyclerView requestRecyclerView;
    private TextView emptyTextView;
    private RequestRapAdapter requestRapAdapter;
    private AlertDialog.Builder ongoingAlert;
    private AlertDialog.Builder alert;

    //static variable declaration
    static public String serverIPaddress;   //-----------//
    static public String serverStatus;      //  server   //
    static public int serverPort;           // variables //
    static public String userIPaddress;     //-----------//
    static public Professional loggedInUser;
    static public Activity activity;
    static public List<Request> requestList;
    static public List<String> distanceList;
    static public List<Job> jobList;
    static public Job ongoingJob;
    static public List<Transaction> transactionList;
    static public List<Response> responseList;

    static public Request requestSelected;
    static public String distanceSelected;

    //firebase database reference variable declaration
    private DatabaseReference professionalDatabase;
    private DatabaseReference serverDatabase;
    private DatabaseReference professionalOnlineDatabase;
    private DatabaseReference professionalRequestDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professional_main);

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

        //firebase database reference variable initialization
        professionalDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.professionalDatabaseName).toString());
        professionalOnlineDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.professionalOnlineDatabaseName).toString());
        serverDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.serverDatabaseName).toString());
        professionalRequestDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.professionalRequestDatabaseName).toString());

        //variable initialization
        locationEnabled = false;
        initializingProgressDialog = new ProgressDialog(this);
        initializingProgressDialog.setMessage("Initializing...");
        initializingProgressDialog.setCancelable(false);
        initializingLocationProgressDialog = new ProgressDialog(this);
        initializingLocationProgressDialog.setMessage("Initializing location...");
        initializingLocationProgressDialog.setCancelable(false);
        requestRecyclerView = (RecyclerView) findViewById(R.id.requestRecyclerView);
        requestRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyTextView = (TextView) findViewById(R.id.emptyTextView);
        jobList = new ArrayList<>();
        ongoingJob = null;
        ongoingAlert = new AlertDialog.Builder(this);
        alert = new AlertDialog.Builder(this);


        requestSelected = null;
        distanceSelected = "";

        //static variable initialization
        serverIPaddress = "";
        serverStatus = "";
        serverPort = 0;
        userIPaddress = "";
        loggedInUser = null;
        activity = this;
        requestList = new ArrayList<>();
        distanceList = new ArrayList<>();
        transactionList = new ArrayList<>();
        responseList = new ArrayList<>();

        //onclick listener on requestRecyclerView
        requestRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, requestRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position)
                    {
                        if (ongoingJob==null)
                        {
                            //get selected request and distance
                            requestSelected = requestList.get(position);
                            distanceSelected = distanceList.get(position);
                            goToRequestDetailsActivity();

                        }
                        else
                        {
                            //display message
                            ongoingAlert.setTitle(":(");
                            ongoingAlert.setMessage("Sorry, you can't view another request\nThere's an ongoing Job");
                            ongoingAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                            ongoingAlert.create().show();
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));



        checkLocationPermission();
    }

    @Override
    protected void onStart() {
        super.onStart();

        initializingProgressDialog.show();

        //set server details
        serverDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                serverIPaddress = dataSnapshot.child("serverIP").getValue(String.class);
                serverStatus = dataSnapshot.child("status").getValue(String.class);
                serverPort = dataSnapshot.child("port").getValue(Integer.class);

                /*
                Toast.makeText(getApplicationContext()
                        ,"server ip : "+serverIPaddress+"   status : "+serverStatus+"   port : "+serverPort
                        ,Toast.LENGTH_LONG).show(); //to debug display server ip and status*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        initializingLocationProgressDialog.show();

        //set user logged in
        professionalDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //check for each children in professional database
                for (DataSnapshot ds: dataSnapshot.getChildren())
                {
                    Professional professional = ds.getValue(Professional.class);

                    if (LoginActivity.email.equalsIgnoreCase(professional.getEmail()))
                    {
                        //set loggedInUser
                        loggedInUser = professional;
                        displayAccountDetails();

                        //initialize lists
                        initializeRequestList();
                        initializeJobList();
                        initializeResponseList();
                        initializeTransactionList();
                        break;
                    }
                }

                getUserIP();
                getCurrentLocation();
                initializingProgressDialog.dismiss();

                //Toast.makeText(getApplicationContext(),"uid : "+loggedInUser.getUID(),Toast.LENGTH_SHORT).show(); //to debug display uid
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        initializingLocationProgressDialog.show();
        getUserIP();
        getCurrentLocation();
        onStart();
    }

    @Override
    protected void onDestroy() {

        deleteCurrentLocation();

        super.onDestroy();
    }

    /**********************************************************************************************/
    //initialize response list method
    private void initializeResponseList()
    {
        //database listener to update responseList if there's changes
        DatabaseReference responseDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.professionalResponseDatabaseName).toString()).child(loggedInUser.getpID());

        responseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                //clear responseList
                responseList.clear();

                //for each response
                for (DataSnapshot ds:dataSnapshot.getChildren())
                {
                    responseList.add(ds.getValue(Response.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**********************************************************************************************/
    //initialize transaction list method
    private void initializeTransactionList()
    {
        //database listener to update transactionList if there's changes
        DatabaseReference transactionDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.professionalTransactionDatabaseName).toString()).child(loggedInUser.getpID());

        transactionDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                //clear transactionList
                transactionList.clear();

                //for each transaction
                for (DataSnapshot ds:dataSnapshot.getChildren())
                {
                    transactionList.add(ds.getValue(Transaction.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**********************************************************************************************/
    //initialize job list method
    private void initializeJobList()
    {
        //database listener to update jobList if there's changes
        DatabaseReference professionalDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.professionalJobDatabaseName).toString()).child(loggedInUser.getpID());

        professionalDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                //clear jobList
                jobList.clear();

                //for each job
                for (DataSnapshot ds:dataSnapshot.getChildren())
                {
                    jobList.add(ds.getValue(Job.class));

                    if (ds.getValue(Job.class).getStatus().equalsIgnoreCase("ongoing"))
                    {
                        ongoingJob = ds.getValue(Job.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*
        if (ongoingJob!=null)
        {
            alert.setMessage("You have ongoing job");
            alert.setPositiveButton("See job", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    //go to job activity
                    startActivity(new Intent(ProfessionalMainActivity.this,JobActivity.class));
                }
            });
            alert.show();
        }*/
    }

    /**********************************************************************************************/
    //go to RequestDetailsActivity method
    private void goToRequestDetailsActivity()
    {
        Intent intent = new Intent(ProfessionalMainActivity.this,RequestDetailsActivity.class);

        startActivity(intent);
    }

    /**********************************************************************************************/
    //initialize requestList method
    private void initializeRequestList()
    {
        professionalRequestDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.professionalRequestDatabaseName).toString()).child(loggedInUser.getpID());

        professionalRequestDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                distanceList.clear();
                requestList.clear();

                //for each request
                for (DataSnapshot ds:dataSnapshot.getChildren())
                {
                    //for each children
                    for (DataSnapshot dsChild:ds.getChildren())
                    {
                        if (dsChild.getKey().equalsIgnoreCase("distance"))
                        {
                            distanceList.add(dsChild.getValue(String.class));
                        }
                        else
                        {
                            requestList.add(dsChild.getValue(Request.class));
                        }
                    }
                }

                //load requestRecyclerView
                loadRequestRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**********************************************************************************************/
    //load request recycler view method
    private void loadRequestRecyclerView()
    {
        if (requestList.size()==0)
        {
            emptyTextView.setVisibility(View.VISIBLE);
        }
        else
        {
            emptyTextView.setVisibility(View.INVISIBLE);
        }

        requestRapAdapter = new RequestRapAdapter(getApplication(),requestList,distanceList);
        requestRecyclerView.setAdapter(requestRapAdapter);
    }

    /**********************************************************************************************/
    //get user location methods
    public void checkLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},101);
        }
    }

    public void getCurrentLocation()
    {
        try
        {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, new LocationListener() {
                @Override
                public void onLocationChanged(Location location)
                {
                    currentLatitude = String.valueOf(location.getLatitude());
                    currentLongitude = String.valueOf(location.getLongitude());

                    locationEnabled = true;
                    saveCurrentLocation();
                    initializingLocationProgressDialog.dismiss();
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s)
                {
                    Toast.makeText(getApplicationContext(),"Please enable gps and internet",Toast.LENGTH_LONG).show();
                    locationEnabled = false;
                    initializingLocationProgressDialog.dismiss();
                }
            });
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
    }

    /**********************************************************************************************/
    //save location method
    private void saveCurrentLocation()
    {
        professionalOnlineDatabase.child(loggedInUser.getpID()).child("ipAddress").setValue(userIPaddress);
        professionalOnlineDatabase.child(loggedInUser.getpID()).child("latitude").setValue(currentLatitude);
        professionalOnlineDatabase.child(loggedInUser.getpID()).child("longitude").setValue(currentLongitude);
        professionalOnlineDatabase.child(loggedInUser.getpID()).child("pid").setValue(loggedInUser.getpID());
    }

    /**********************************************************************************************/
    //delete location method
    private void deleteCurrentLocation()
    {
        professionalOnlineDatabase.child(loggedInUser.getpID()).setValue(null);
    }

    /**********************************************************************************************/
    //get user ip function
    private void getUserIP()
    {

        //get user IP address
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        userIPaddress = parseIntegerToNetAddress(wifiManager.getDhcpInfo().ipAddress).toString();
        userIPaddress = userIPaddress.substring(1);
        //Toast.makeText(getApplicationContext(),"User IP address : "+userIPaddress,Toast.LENGTH_LONG).show(); //to debug display user ip address

    }

    private InetAddress parseIntegerToNetAddress(int host)
    {
        byte[] addressBytes = { (byte)(0xff & host),
                (byte)(0xff & (host >> 8)),
                (byte)(0xff & (host >> 16)),
                (byte)(0xff & (host >> 24)) };

        try
        {
            return InetAddress.getByAddress(addressBytes);
        }
        catch (UnknownHostException e)
        {
            Toast.makeText(getApplicationContext(),"Failed to retrieve user IP address",Toast.LENGTH_LONG).show();
            throw new AssertionError();
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
                        //close side bar
                        myDrawerLayout.closeDrawer(Gravity.LEFT);
                    }
                    break;

                    case R.id.nav_account:
                    {
                        //go to edit account page
                        startActivity(new Intent(ProfessionalMainActivity.this,EditAccountProfessionalActivity.class));
                    }
                    break;

                    case R.id.nav_history:
                    {
                        //go to history page
                        startActivity(new Intent(ProfessionalMainActivity.this,HistoryProfessionalActivity.class));
                    }
                    break;

                    case R.id.nav_jobs:
                    {
                        //go to job activity
                        startActivity(new Intent(ProfessionalMainActivity.this,JobActivity.class));
                    }
                    break;

                    case R.id.nav_settings:
                    {
                        //go to settings page
                        startActivity(new Intent(ProfessionalMainActivity.this,SettingsProfessionalActivity.class));
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
        if (loggedInUser==null){
            accountName.setText("User name default");
            accountRating.setText("5.0");
        }
        else {
            accountName.setText(loggedInUser.getFirstName() + " " + loggedInUser.getLastName());
            accountRating.setText(String.format("%.1f",((double)loggedInUser.getRating()/(double)loggedInUser.getTotalRating())));
        }
    }

    /**********************************************************************************************/
    @Override
    public void onBackPressed() {

        //override and do nothing to disable back button
    }
    /**********************************************************************************************/
}
