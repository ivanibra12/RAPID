package ii954.csci314au19.fake_uber;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
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
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ResponsesActivity extends AppCompatActivity {

    //variable declaration
    private RecyclerView responsesRecyclerView;
    private TextView emptyTextView;
    private LinearLayout currentRequestLayout;
    private TextView requestIDTextView;
    private TextView vehicleTextView;
    private TextView requestDateTextView;
    private TextView requestTimeTextView;
    private TextView problemTextView;
    private TextView problemDescTextView;
    private Request ongoingRequest;
    private SimpleDateFormat simpleDateFormat;
    private LinearLayout responsesLayout;
    private TextView noResponsesTextView;
    private List<Response> responseList;
    private ResponseAdapter responseAdapter;
    private Button cancelRequestButton;
    private List<String> professionalID;
    private Response responseSelected;
    private Dialog responsePopup;
    private AlertDialog.Builder alert;
    private Job newJob;
    private Calendar calendar;

    //firebase database reference variable declaration
    private DatabaseReference userRequestDatabase;

    //side bar variable declaration
    private DrawerLayout myDrawerLayout;
    private ActionBarDrawerToggle mySideBar;

    //sidebar header variable declaration
    public TextView accountName;
    public TextView accountRating;

    //firebase database reference variable declaration
    private DatabaseReference userResponseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responses);

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
        userResponseDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.userResponseDatabaseName).toString());

        //load account details in side bar
        displayAccountDetails();

        if (UserMainActivity.ongoingJob!=null)
        {
            finish();
            startActivity(new Intent(ResponsesActivity.this,JobOngoingActivity.class));
        }

        //firebase database reference variable initialization
        userRequestDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.userRequestDatabaseName).toString()).child(UserMainActivity.loggedInUser.getUID());

        //variable initialization
        responsesRecyclerView = (RecyclerView) findViewById(R.id.responsesRecyclerView);
        responsesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyTextView = (TextView) findViewById(R.id.emptyTextView);
        currentRequestLayout = (LinearLayout) findViewById(R.id.currentRequestLayout);
        requestIDTextView = (TextView) findViewById(R.id.requestIDTextView);
        vehicleTextView = (TextView) findViewById(R.id.vehicleTextView);
        requestDateTextView = (TextView) findViewById(R.id.requestDateTextView);
        requestTimeTextView = (TextView) findViewById(R.id.requestTimeTextView);
        problemTextView = (TextView) findViewById(R.id.problemTextView);
        problemDescTextView = (TextView) findViewById(R.id.problemDescTextView);
        responsesLayout = (LinearLayout) findViewById(R.id.responsesLayout);
        noResponsesTextView = (TextView) findViewById(R.id.noResponsesTextView);
        ongoingRequest = UserMainActivity.ongoingRequest;
        responseList = new ArrayList<>();
        cancelRequestButton= (Button) findViewById(R.id.cancelRequestButton);
        professionalID = new ArrayList<>();
        responseSelected = null;
        responsePopup = new Dialog(this);
        alert = new AlertDialog.Builder(this);
        newJob = null;

        //onclick listener on responseRecyclerView
        responsesRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, responsesRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position)
                    {
                        //get response selected at position
                        responseSelected = responseList.get(position);

                        showPopupResponse();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));

        //onclick listener for cancelRequestButton
        cancelRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                alert.setMessage("Are you sure you want to cancel this request ?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        cancelRequest();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        //load details
        loadDetails();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //listener on user request database to load details if anything changes
        userRequestDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //load details
                loadDetails();

                //if there's no ongoing request
                if (UserMainActivity.ongoingRequest==null)
                {
                    //set empty text view to visible
                    emptyTextView.setVisibility(View.VISIBLE);
                    //set current request layout to invisible
                    currentRequestLayout.setVisibility(View.INVISIBLE);
                    //set cancel request button to invisible
                    cancelRequestButton.setVisibility(View.INVISIBLE);
                    //set responses layout to invisible
                    responsesLayout.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (ongoingRequest!=null)
        {
            //listener on user response database to load responseRecyclerView if there's changes
            userResponseDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.userResponseDatabaseName).toString())
                    .child(UserMainActivity.loggedInUser.getUID()).child(ongoingRequest.getRequestID());

            userResponseDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //clear responseList
                    responseList.clear();

                    //for each response
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Response response = ds.getValue(Response.class);

                        //add to arrayList
                        responseList.add(response);
                    }

                    loadResponseRecyclerView();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //listener on professionalRequest database
            DatabaseReference professionalRequestDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.professionalRequestDatabaseName).toString());

            professionalRequestDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //clear professionalID list
                    professionalID.clear();

                    //for each professional
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        //get key
                        String id = ds.getKey();

                        //for each request
                        for (DataSnapshot dsChild : ds.getChildren()) {
                            if (dsChild.getKey().equalsIgnoreCase(ongoingRequest.getRequestID())) {
                                //add id to professionalID list
                                professionalID.add(id);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    /**********************************************************************************************/
    //show popup response method
    private void showPopupResponse()
    {
        responsePopup.setContentView(R.layout.popup_response);

        //variable declaration inside popup
        TextView idTextView = (TextView) responsePopup.findViewById(R.id.idTextView);
        TextView nameTextView = (TextView) responsePopup.findViewById(R.id.nameTextView);
        TextView mobileNumberTextView = (TextView) responsePopup.findViewById(R.id.mobileNumberTextView);
        TextView distanceTextView = (TextView) responsePopup.findViewById(R.id.distanceTextView);
        TextView ratingTextView = (TextView) responsePopup.findViewById(R.id.ratingTextView);
        TextView declineTextView = (TextView) responsePopup.findViewById(R.id.declineTextView);
        TextView acceptTextView = (TextView) responsePopup.findViewById(R.id.acceptTextView);

        //load details inside popup
        idTextView.setText(responseSelected.getProfessional().getpID());
        nameTextView.setText(responseSelected.getProfessional().getFirstName()+" "+responseSelected.getProfessional().getLastName());
        mobileNumberTextView.setText(responseSelected.getProfessional().getMobileNo());
        ratingTextView.setText(String.format("%.2f",(double)((double)responseSelected.getProfessional().getRating()/(double)responseSelected.getProfessional().getTotalRating()))+" star");
        distanceTextView.setText(responseSelected.getDistance());

        //onclick listener for declineTextView
        declineTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                alert.setMessage("Are you sure you want to decline this response ?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        declineResponse();
                        dialog.dismiss();
                        responsePopup.dismiss();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        //onclick listener for acceptTextView
        acceptTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                acceptResponse();
            }
        });

        responsePopup.show();
    }

    /**********************************************************************************************/
    //decline response method
    private void declineResponse()
    {
        //update response status
        DatabaseReference responseDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.responseDatabaseName).toString());
        DatabaseReference userResponseDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.userResponseDatabaseName).toString())
                .child(UserMainActivity.loggedInUser.getUID()).child(ongoingRequest.getRequestID());
        DatabaseReference professionalResponseDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.professionalResponseDatabaseName).toString());

        Response response = responseSelected;
        response.setStatus("Declined");

        responseDatabase.child(response.getResponseid()).setValue(response);
        userResponseDatabase.child(response.getResponseid()).setValue(null);
        professionalResponseDatabase.child(response.getProfessional().getpID()).child(response.getResponseid()).setValue(response);

        responseList.remove(responseSelected);
    }

    /**********************************************************************************************/
    //accept response method
    private void acceptResponse()
    {
        //update response status
        DatabaseReference responseDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.responseDatabaseName).toString());
        DatabaseReference userResponseDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.userResponseDatabaseName).toString())
                .child(UserMainActivity.loggedInUser.getUID()).child(ongoingRequest.getRequestID());
        DatabaseReference professionalResponseDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.professionalResponseDatabaseName).toString());

        for(int i=0;i<responseList.size();i++)
        {
            Response response = responseList.get(i);

            if (response!=responseSelected)
            {
                response.setStatus("Declined");

                responseDatabase.child(response.getResponseid()).setValue(response);
                userResponseDatabase.child(response.getResponseid()).setValue(null);
                professionalResponseDatabase.child(response.getProfessional().getpID()).child(response.getResponseid()).setValue(response);
            }
            else
            {
                response.setStatus("Accepted");

                responseDatabase.child(response.getResponseid()).setValue(response);
                userResponseDatabase.child(response.getResponseid()).setValue(null);
                professionalResponseDatabase.child(response.getProfessional().getpID()).child(response.getResponseid()).setValue(response);
            }
        }

        DatabaseReference professionalRequestDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.professionalRequestDatabaseName).toString());

        for (int i=0;i<professionalID.size();i++)
        {
            professionalRequestDatabase.child(professionalID.get(i)).child(ongoingRequest.getRequestID()).setValue(null);
        }

        responseSelected.setStatus("Accepted");
        createNewJob();
    }

    /**********************************************************************************************/
    //create new job method
    private void createNewJob()
    {
        newJob = new Job(ongoingRequest,responseSelected,"");

        //save job details in database
        DatabaseReference ongoingJobDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.ongoingJobDatabaseName).toString());
        DatabaseReference jobDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.jobDatabaseName).toString());
        DatabaseReference userJobDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.userJobDatabaseName).toString());
        DatabaseReference professionalJobDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.professionalJobDatabaseName).toString());

        ongoingJobDatabase.child(newJob.getJobid()).setValue(newJob);
        jobDatabase.child(newJob.getJobid()).setValue(newJob);
        userJobDatabase.child(newJob.getRequest().getUser().getUID()).child(newJob.getJobid()).setValue(newJob);
        professionalJobDatabase.child(newJob.getResponse().getProfessional().getpID()).child(newJob.getJobid()).setValue(newJob);

        onBackPressed();
    }

    /**********************************************************************************************/
    //cancel request method
    private void cancelRequest()
    {
        //update request status
        DatabaseReference ongoingRequestDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.ongoingRequestDatabaseName).toString());
        DatabaseReference requestDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.requestDatabaseName).toString());
        DatabaseReference userRequestDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.userRequestDatabaseName).toString()).child(UserMainActivity.loggedInUser.getUID());
        DatabaseReference professionalRequestDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.professionalRequestDatabaseName).toString());

        Request request = ongoingRequest;
        request.setStatus("Canceled");

        ongoingRequestDatabase.child(request.getRequestID()).setValue(null);
        requestDatabase.child(request.getRequestID()).setValue(request);
        userRequestDatabase.child(request.getRequestID()).setValue(request);
        for (int i=0;i<professionalID.size();i++)
        {
            professionalRequestDatabase.child(professionalID.get(i)).child(request.getRequestID()).setValue(null);
        }

        //update response status
        DatabaseReference responseDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.responseDatabaseName).toString());
        DatabaseReference userResponseDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.userResponseDatabaseName).toString())
                .child(UserMainActivity.loggedInUser.getUID()).child(request.getRequestID());
        DatabaseReference professionalResponseDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.professionalResponseDatabaseName).toString());

        for(int i=0;i<responseList.size();i++)
        {
            Response response = responseList.get(i);
            response.setStatus("Declined");

            responseDatabase.child(response.getResponseid()).setValue(response);
            userResponseDatabase.child(response.getResponseid()).setValue(null);
            professionalResponseDatabase.child(response.getProfessional().getpID()).child(response.getResponseid()).setValue(response);
        }

        //if not a member
        if (UserMainActivity.userMembership==null)
        {
            //create refund transaction
            calendar = Calendar.getInstance();
            simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
            String id = ongoingRequest.getUser().getUID() + simpleDateFormat.format(calendar.getTime());
            String desc = "Refund (" + ongoingRequest.getRequestID() + ")";
            Transaction transaction = new Transaction(id, ongoingRequest.getUser().getUID(), desc, 69.69);

            //save transaction to database
            DatabaseReference outgoingTransactionDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.outgoingTransactionDatabaseName).toString());
            DatabaseReference userTransactionDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.userTransactionDatabaseName).toString());

            outgoingTransactionDatabase.child(transaction.getTransactionID()).setValue(transaction);
            userTransactionDatabase.child(ongoingRequest.getUser().getUID()).child(transaction.getTransactionID()).setValue(transaction);
        }


        onBackPressed();
    }

    /**********************************************************************************************/
    //load responseRecyclerView method
    private void loadResponseRecyclerView()
    {
        //if there's no response
        if (responseList.size()==0)
        {
            noResponsesTextView.setVisibility(View.VISIBLE);
        }
        else
        {
            noResponsesTextView.setVisibility(View.INVISIBLE);
        }

        responseAdapter = new ResponseAdapter(getApplication(),responseList);
        responsesRecyclerView.setAdapter(responseAdapter);
    }

    /**********************************************************************************************/
    //load details function
    private void loadDetails()
    {
        if (ongoingRequest!=null)
        {
            requestIDTextView.setText("Request ID : " + ongoingRequest.getRequestID());
            vehicleTextView.setText("Vehicle : " + ongoingRequest.getVehicle().getNickname() + " (" + ongoingRequest.getVehicle().getRegNum() + ")");
            simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            requestDateTextView.setText("Request date : " + simpleDateFormat.format(ongoingRequest.getRequestDateTime()));
            simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            requestTimeTextView.setText("Request time : " + simpleDateFormat.format(ongoingRequest.getRequestDateTime()));
            problemTextView.setText("Problem : " + ongoingRequest.getProblem());
            problemDescTextView.setText("Additional note : " + ongoingRequest.getAdditionalNote());
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
                        startActivity(new Intent(ResponsesActivity.this,UserMainActivity.class));
                    }
                    break;

                    case R.id.nav_account:
                    {
                        //go to account page
                        startActivity(new Intent(ResponsesActivity.this,EditAccountActivity.class));
                    }
                    break;

                    case R.id.nav_vehicles:
                    {
                        //go to my vehicle page
                        finish();
                        startActivity(new Intent(ResponsesActivity.this,MyVehicleActivity.class));
                    }
                    break;

                    case R.id.nav_history:
                    {
                        //go to history page
                        finish();
                        startActivity(new Intent(ResponsesActivity.this,HistoryActivity.class));
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
                        startActivity(new Intent(ResponsesActivity.this,SettingsActivity.class));
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
        startActivity(new Intent(ResponsesActivity.this,UserMainActivity.class));
    }
    /**********************************************************************************************/
}
