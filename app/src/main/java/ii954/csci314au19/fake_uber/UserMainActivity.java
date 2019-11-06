package ii954.csci314au19.fake_uber;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserMainActivity extends AppCompatActivity implements OnMapReadyCallback {

    //google maps variable declaration
    private GoogleMap mMap;
    LocationManager locationManager;
    private String currentLongitude;
    private String currentLatitude;
    private Marker currentLocationMarker;
    private boolean locationEnabled;
    private List<Marker> professionalOnlineMarkers;

    //variable declaration
    public static ProgressDialog initializingProgressDialog;       //--------------//
    //public static ProgressDialog loadMembershipProgressDialog;    // initializing //
    //public static ProgressDialog loadTransactionProgressDialog;   //   progress   //
    public static ProgressDialog loadingMapProgressDialog;        //   dialogs    //
    //public static ProgressDialog loadUserRequestProgressDialog;   //--------------//
    private Spinner problemSpinner;
    private List<String> commonProblemList;
    private String defaultProblemMessage;
    private Spinner vehicleSpinner;
    private List<String> vehicleDetailList;
    private String defaultVehicleMessage;
    private Calendar calendar;
    private Date todayDate;
    private ImageButton currentLocationButton;
    private SimpleDateFormat simpleDateFormat;
    private AlertDialog.Builder alert;

    //static variable declaration
    static public String serverIPaddress;   //-----------//
    static public String serverStatus;      //  server   //
    static public int serverPort;           // variables //
    static public String userIPaddress;     //-----------//
    static public User loggedInUser;
    static public List<Car> userCars;
    static public Membership userMembership;
    static public List<Membership> membershipList;
    static public Request ongoingRequest;
    static public List<Request> userRequestList;
    static public List<Transaction> userTransactionList;
    static public Job ongoingJob;
    static public List<Job> jobList;

    //static variable for confirmHelpRequestActivity
    static public String problemChosen;
    static public Car vehicleChosen;

    //firebase realtime database reference variable declaration
    private DatabaseReference userDatabase;
    private DatabaseReference vehicleDatabase;
    private DatabaseReference serverDatabase;
    private DatabaseReference userTransactionDatabase;
    private DatabaseReference membershipDatabase;
    private DatabaseReference userRequestDatabase;
    private DatabaseReference professionalOnlineDatabase;

    //sidebar header variable declaration
    public TextView accountName;
    public TextView accountRating;

    //side bar variable declaration
    private DrawerLayout myDrawerLayout;
    private ActionBarDrawerToggle mySideBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        //variable initialization
        alert = new AlertDialog.Builder(this);

        //initialize problem list
        commonProblemList = new ArrayList<>();
        defaultProblemMessage = "Select problem";
        commonProblemList.add(defaultProblemMessage);
        String tempArr[] = getResources().getStringArray(R.array.commonProblemsArray);
        for (int i = 0; i < tempArr.length; i++) {
            commonProblemList.add(tempArr[i]);
        }

        //initializa vehicle list for spinner
        vehicleDetailList = new ArrayList<>();
        defaultVehicleMessage = "Select vehicle";
        vehicleDetailList.add(defaultVehicleMessage);

        //variable initialization
        problemSpinner = (Spinner) findViewById(R.id.problemSpinner);
        loadProblemSpinner();
        vehicleSpinner = (Spinner) findViewById(R.id.vehicleSpinner);
        currentLocationButton = (ImageButton) findViewById(R.id.currentLocationButton);
        calendar = Calendar.getInstance();
        todayDate = calendar.getTime();
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationEnabled = false;

        //firebase realtime database reference variable initialization
        userDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.userDatabaseName).toString());
        vehicleDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.vehicleDatabaseName).toString());
        serverDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.serverDatabaseName).toString());
        userTransactionDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.userTransactionDatabaseName).toString());
        membershipDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.membershipDatabaseName).toString());
        userRequestDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.userRequestDatabaseName).toString());
        professionalOnlineDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.professionalOnlineDatabaseName).toString());

        //google maps variable initialization
        currentLongitude = "";
        currentLatitude = "";
        professionalOnlineMarkers = new ArrayList<>();

        //static variable initialization
        userCars = new ArrayList<>();
        serverIPaddress = "";
        serverStatus = "";
        serverPort = 0;
        userIPaddress = "";
        userMembership = null;
        membershipList = new ArrayList<>();
        userTransactionList = new ArrayList<>();
        userRequestList = new ArrayList<>();
        ongoingRequest = null;
        jobList = new ArrayList<>();
        ongoingJob = null;

        //to get the menu icon and sidebar initialization
        myDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mySideBar = new ActionBarDrawerToggle(this, myDrawerLayout, R.string.open, R.string.close);
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

        //show progress dialog until activity initialization finished
        initializingProgressDialog = new ProgressDialog(this);
        initializingProgressDialog.setMessage("Initializing...");
        initializingProgressDialog.setCancelable(false);
        loadingMapProgressDialog = new ProgressDialog(this);
        loadingMapProgressDialog.setMessage("Loading map...");
        loadingMapProgressDialog.setCancelable(false);
        loadingMapProgressDialog.show();
        /*
        loadMembershipProgressDialog = new ProgressDialog(this);
        loadMembershipProgressDialog.setMessage("Initializing memberships...");
        loadMembershipProgressDialog.setCancelable(false);
        loadTransactionProgressDialog = new ProgressDialog(this);
        loadTransactionProgressDialog.setMessage("Initializing transactions...");
        loadTransactionProgressDialog.setCancelable(false);
        loadUserRequestProgressDialog = new ProgressDialog(this);
        loadUserRequestProgressDialog.setMessage("Initializing requests...");
        loadUserRequestProgressDialog.setCancelable(false);*/


        //listener for currentLocationButton
        currentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadingMapProgressDialog.show();
                getCurrentLocation();
            }
        });

        loadVehicleSpinner();
        getUserIP();
        checkLocationPermission();
        getCurrentLocation();
    }


    /**********************************************************************************************/
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

        loadingMapProgressDialog.show();

        //set user logged in
        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //check for each children in user database
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);

                    if (LoginActivity.email.equalsIgnoreCase(user.getEmail())) {
                        //set loggedInUser
                        loggedInUser = user;
                        displayAccountDetails();

                        /**------------**/
                        //get user vehicles
                        loadVehicleList();

                        //get user memberships history
                        loadMembershipList();

                        //get user transaction history
                        loadUserTransactionList();

                        //get user request history
                        loadUserRequestList();

                        //get user job list
                        loadJobList();
                        /**------------**/

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

        loadingMapProgressDialog.show();
        getCurrentLocation();
        onStart();
        getUserIP();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**********************************************************************************************/
    //load jobList method
    private void loadJobList()
    {
        DatabaseReference userJobDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.userJobDatabaseName).toString()).child(loggedInUser.getUID());

        //database listener to update jobList if there's changes
        userJobDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                //clear jobList
                jobList.clear();

                //for each job
                for (DataSnapshot ds:dataSnapshot.getChildren())
                {
                    Job job = ds.getValue(Job.class);

                    //add job to jobList
                    jobList.add(job);

                    //if job status is ongoing
                    if(job.getStatus().equalsIgnoreCase("ongoing"))
                    {
                        //set ongoing job
                        ongoingJob = job;

                        checkOngoingJob();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**********************************************************************************************/
    //check ongoing jobs method
    private void checkOngoingJob()
    {
        if (ongoingJob!=null)
        {
            DatabaseReference ongoingJobDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.ongoingJobDatabaseName).toString());

            ongoingJobDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (ongoingJob!=null)
                    {
                        //for each job
                        for (DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            if (ds.getValue(Job.class).getJobid().equalsIgnoreCase(ongoingJob.getJobid()))
                            {
                                ongoingJob = ds.getValue(Job.class);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        /*
        if (ongoingJob!=null)
        {
            alert.setMessage("You have ongoing request");
            alert.setPositiveButton("See request", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    //go to responses page
                    finish();
                    startActivity(new Intent(UserMainActivity.this,ResponsesActivity.class));
                }
            });
            alert.show();
        }*/
    }

    /**********************************************************************************************/
    //load userRequestList method
    final private void loadUserRequestList()
    {
        //loadUserRequestProgressDialog.show();

        //get user request history
        userRequestDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.userRequestDatabaseName).toString()).child(loggedInUser.getUID());

        userRequestDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //clear userRequestList
                userRequestList.clear();

                //for each request
                for (DataSnapshot ds:dataSnapshot.getChildren())
                {
                    //get request
                    Request newRequest = ds.getValue(Request.class);

                    //add to userRequestList
                    userRequestList.add(newRequest);

                    //if request status is ongoing
                    if (newRequest.getStatus().equalsIgnoreCase("ongoing"))
                    {
                        //set ongoing request
                        ongoingRequest = newRequest;
                    }
                }

                //remove progressDialog
                //loadUserRequestProgressDialog.cancel();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**********************************************************************************************/
    //load vehicleList
    final private void loadVehicleList()
    {
        //loadVehicleProgressDialog.show();

        //get user vehicles
        vehicleDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.vehicleDatabaseName).toString()).child(loggedInUser.getUID());

        vehicleDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //clear userCars list
                userCars.clear();

                //clear vehicleDetailList list
                vehicleDetailList.clear();
                vehicleDetailList.add(defaultVehicleMessage);

                //for each vehicle
                for (DataSnapshot ds: dataSnapshot.getChildren())
                {
                    //if not total vehicle
                    if (!ds.getKey().equalsIgnoreCase("totalVehicle"))
                    {
                        //get car
                        Car newCar = ds.getValue(Car.class);

                        //put in userCars arrayList
                        userCars.add(newCar);

                        //put in vehicleDetailsList arrayList
                        vehicleDetailList.add(newCar.getNickname()+" ("+newCar.getRegNum()+")");    //format Nickname (abc123)

                        //call loadVehicleSpinner function
                        loadVehicleSpinner();
                    }
                }

                //Toast.makeText(getApplicationContext(),"totalCar : "+userCars.size(),Toast.LENGTH_SHORT).show(); //to debug display totalCar

                //remove progress dialog
                //loadVehicleProgressDialog.cancel();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**********************************************************************************************/
    //load userTransactionList
    final private void loadUserTransactionList()
    {
        //loadTransactionProgressDialog.show();

        //get user transaction history
        userTransactionDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.userTransactionDatabaseName).toString()).child(loggedInUser.getUID());

        userTransactionDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //clear userTransactionList
                userTransactionList.clear();

                //for each user transaction inside the database
                for (DataSnapshot ds:dataSnapshot.getChildren())
                {
                    //get transaction
                    Transaction newTransaction = ds.getValue(Transaction.class);

                    //put it in userTransactionList
                    userTransactionList.add(newTransaction);
                }

                //cancel progressDialog
                //loadTransactionProgressDialog.cancel();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**********************************************************************************************/
    //load membershipList()
    final private void loadMembershipList()
    {
        //loadMembershipProgressDialog.show();

        //get user memberships history
        membershipDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.membershipDatabaseName).toString()).child(loggedInUser.getUID());

        membershipDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //clear membershipList
                membershipList.clear();

                //for each membership inside the database
                for (DataSnapshot ds:dataSnapshot.getChildren())
                {
                    //get membership
                    Membership newMembership = ds.getValue(Membership.class);

                    //put it in membershipList
                    membershipList.add(newMembership);
                }

                //define whether user is a member
                defineMembership();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**********************************************************************************************/
    //define membership function
    final private void defineMembership()
    {
        //if user has never sign up for a membership
        if (membershipList.size()==0)
        {
            userMembership = null;
        }
        else
        {
            Membership latestMembership = membershipList.get(0);
            Date latestEndDate = latestMembership.getEndDate();

            for (int i=1;i<membershipList.size();i++)
            {
                Membership curMembership = membershipList.get(i);

                Date curEndDate = curMembership.getEndDate();

                if (latestEndDate.before(curEndDate))
                {
                    latestMembership = curMembership;
                    latestEndDate = curEndDate;
                }
            }

            if (latestEndDate.after(todayDate)||latestEndDate.equals(todayDate))
            {
                userMembership = latestMembership;
            }
            else
            {
                userMembership = null;
            }
        }

        //cancel progressDialog
        //loadMembershipProgressDialog.cancel();

        //displayMembership();
    }

    //display membership function for debug
    final private void displayMembership()
    {
        AlertDialog.Builder alertMembership = new AlertDialog.Builder(this);
        if (userMembership==null)
        {
            alertMembership.setMessage("Not a member");
        }
        else
        {
            alertMembership.setMessage(userMembership.getMembershipDetails());
        }
        alertMembership.create().show();
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
    //load vehicle spinner function
    private void loadVehicleSpinner()
    {
        ArrayAdapter<String> vehicleAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,vehicleDetailList)
        {
            @Override
            public boolean isEnabled(int position)
            {
                //disable the first item
                if(position == 0)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent)
            {
                View spinnerview = super.getDropDownView(position, convertView, parent);

                TextView spinnertextview = (TextView) spinnerview;

                if(position == 0) {

                    //Set the color of disabled item to fade (grey)
                    spinnertextview.setTextColor(Color.parseColor("#bcbcbc"));
                }
                else {

                    //set color to black
                    spinnertextview.setTextColor(Color.BLACK);

                }
                return spinnerview;
            }
        };
        vehicleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //set adapter
        vehicleSpinner.setAdapter(vehicleAdapter);
    }

    //load vehicle spinner function
    private void loadProblemSpinner()
    {
        ArrayAdapter<String> problemAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,commonProblemList)
        {
            @Override
            public boolean isEnabled(int position)
            {
                //disable the first item
                if(position == 0)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent)
            {
                View spinnerview = super.getDropDownView(position, convertView, parent);

                TextView spinnertextview = (TextView) spinnerview;

                if(position == 0) {

                    //Set the color of disabled item to fade (grey)
                    spinnertextview.setTextColor(Color.parseColor("#bcbcbc"));
                }
                else {

                    //set color to black
                    spinnertextview.setTextColor(Color.BLACK);

                }
                return spinnerview;
            }
        };
        problemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //set adapter
        problemSpinner.setAdapter(problemAdapter);
    }

    /**********************************************************************************************/
    //SHOW GOOGLE MAP
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        loadProfessionalOnlineMarker();
    }

    public void loadProfessionalOnlineMarker()
    {
        professionalOnlineDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                //remove each marker in list
                for (int i = 0;i<professionalOnlineMarkers.size();i++)
                {
                    professionalOnlineMarkers.get(i).remove();
                }
                professionalOnlineMarkers.clear();

                //for each professional
                for (DataSnapshot ds:dataSnapshot.getChildren())
                {
                    //if not total
                    if(!ds.getKey().equalsIgnoreCase("total"))
                    {
                        double latitude = 0.0;
                        double longitude = 0.0;
                        Marker currentMarker = null;

                        //for each children
                        //get latitude and longitude
                        for (DataSnapshot dsChild:ds.getChildren())
                        {
                            if (dsChild.getKey().equalsIgnoreCase("latitude"))
                            {
                                latitude = Double.parseDouble(dsChild.getValue(String.class));
                            }
                            else if (dsChild.getKey().equalsIgnoreCase("longitude"))
                            {
                                longitude = Double.parseDouble(dsChild.getValue(String.class));
                            }
                        }

                        LatLng location = new LatLng(latitude,longitude);
                        currentMarker = mMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.fromResource(R.drawable.service_32px)));
                        professionalOnlineMarkers.add(currentMarker);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void checkLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED
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

                    // Add a marker in current location and move the camera
                    if (currentLocationMarker!=null)
                    {
                        currentLocationMarker.remove();
                    }

                    LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());

                    currentLocationMarker = mMap.addMarker(new MarkerOptions().position(currentLoc).title("You are here"));
                    currentLocationMarker.setVisible(true);
                    //mMap.addMarker(new MarkerOptions().position(currentLoc).title("You are here"));
                    float zoom = (float)17.0;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc,zoom));

                    //cancel progressDialog
                    locationEnabled = true;
                    loadingMapProgressDialog.cancel();
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
                    loadingMapProgressDialog.cancel();
                }
            });
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
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
                        finish();
                        startActivity(new Intent(UserMainActivity.this,EditAccountActivity.class));
                    }
                    break;

                    case R.id.nav_vehicles:
                    {
                        //go to my vehicle page
                        finish();
                        startActivity(new Intent(UserMainActivity.this,MyVehicleActivity.class));
                    }
                    break;

                    case R.id.nav_history:
                    {
                        //go to history page
                        finish();
                        startActivity(new Intent(UserMainActivity.this,HistoryActivity.class));
                    }
                    break;

                    case R.id.nav_responses:
                    {
                        //go to responses page
                        finish();
                        startActivity(new Intent(UserMainActivity.this,ResponsesActivity.class));
                    }
                    break;

                    case R.id.nav_settings:
                    {
                        //go to settings page
                        finish();
                        startActivity(new Intent(UserMainActivity.this,SettingsActivity.class));
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
    //WHEN SEND HELP BUTTON IS PRESSED
    public void requestHelp(View v)
    {
        //if there's an ongoing request
        if (ongoingRequest!=null)
        {
            //display message
            AlertDialog.Builder ongoingAlert = new AlertDialog.Builder(this);
            ongoingAlert.setTitle(":(");
            ongoingAlert.setMessage("Sorry, you can't request another help\nThere's an ongoing help request");
            ongoingAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });
            ongoingAlert.create().show();
            return;
        }
        //if location is disabled
        else if (!locationEnabled)
        {
            //display message
            Toast.makeText(getApplicationContext(),"Please enable gps and internet",Toast.LENGTH_LONG).show();
            return;
        }
        //if server is offline
        else if (serverStatus.equalsIgnoreCase("offline"))
        {
            //display message
            Toast.makeText(getApplicationContext(),"Server is offline, try again later",Toast.LENGTH_LONG).show();
            return;
        }
        else
        {
            //validation
            if (problemSpinner.getSelectedItem().toString().equalsIgnoreCase(defaultProblemMessage))     //if user haven't selected any problem
            {
                Toast.makeText(getApplicationContext(),"Please select problem",Toast.LENGTH_SHORT).show();
            }
            else if (vehicleSpinner.getSelectedItem().toString().equalsIgnoreCase(defaultVehicleMessage))
            {
                Toast.makeText(getApplicationContext(),"Please select vehicle",Toast.LENGTH_SHORT).show();
            }
            else
            {
                //get problem
                problemChosen = problemSpinner.getSelectedItem().toString();

                //get vehicle
                vehicleChosen = userCars.get(vehicleSpinner.getSelectedItemPosition()-1);


                //send request to server
                //SendMessage sendMessage = new SendMessage();
                //sendMessage.execute("request,"+UserMainActivity.loggedInUser.getUID()+","+UserMainActivity.vehicleChosen.getRegNum()+","+problemChosen);
                //Toast.makeText(this,"Help has been requested",Toast.LENGTH_SHORT).show();


                //go to confirmHelpRequestActivity page
                Intent intent = new Intent(UserMainActivity.this,ConfirmHelpRequestActivity.class);
                intent.putExtra("longitude",currentLongitude);
                intent.putExtra("latitude",currentLatitude);
                intent.putExtra("userIP",userIPaddress);

                finish();
                startActivity(intent);


            }
        }


    }

    /**********************************************************************************************/
    @Override
    public void onBackPressed() {

        //override and do nothing to disable back button
    }
    /**********************************************************************************************/


    /*
    private void getLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            mLocationPermissionGranted = true;
        }
        else
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        mLocationPermissionGranted = false;
        switch (requestCode)
        {
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION:
            {
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI()
    {
        if (mMap==null)
        {
            return;
        }
        try
        {
            if (mLocationPermissionGranted)
            {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
            else
            {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        }
        catch(SecurityException e)
        {
            Log.e("Exception: %s",e.getMessage());
        }
    }

    private void getDeviceLocation()
    {
        try
        {
            if (mLocationPermissionGranted)
            {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful())
                        {
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude()
                                            ,mLastKnownLocation.getLongitude()),DEFAULT_ZOOM));
                        }
                        else
                        {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation,DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        }
        catch (SecurityException e)
        {
            Log.e("Exception: %s",e.getMessage());
        }
    }
    */

}
