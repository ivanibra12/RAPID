package ii954.csci314au19.fake_uber;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class RequestDetailsActivity extends AppCompatActivity {

    //variable declaration
    private Request requestSelected;
    private String distanceSelected;

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
    private Button viewLocationButton;
    private Button acceptButton;
    private Button declineButton;
    private Response newResponse;

    //firebase database reference variable declaration
    private DatabaseReference responseDatabase;
    private DatabaseReference userResponseDatabase;
    private DatabaseReference professionalResponseDatabase;
    private DatabaseReference professionalRequestDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);

        //enable action bar (display back button on action bar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //firebase database reference variable initialization
        responseDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.responseDatabaseName).toString());
        userResponseDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.userResponseDatabaseName).toString());
        professionalResponseDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.professionalResponseDatabaseName).toString());
        professionalRequestDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.professionalRequestDatabaseName).toString());

        //variable initialization
        requestSelected = ProfessionalMainActivity.requestSelected;
        distanceSelected = ProfessionalMainActivity.distanceSelected;

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
        viewLocationButton = (Button) findViewById(R.id.viewLocationButton);
        acceptButton = (Button) findViewById(R.id.acceptButton);
        declineButton = (Button) findViewById(R.id.declineButton);
        newResponse = null;

        //onclick listener for viewLocationButton
        viewLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                goToGoogleMaps();
            }
        });

        //onclick listener for acceptButton
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                acceptRequest();
            }
        });

        //onclick listener for declineButton
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                declineRequest();
            }
        });

        //load details
        loadDetails();

    }

    /**********************************************************************************************/
    //accept request method
    private void acceptRequest()
    {
        //create new Response
        newResponse = new Response(ProfessionalMainActivity.loggedInUser,distanceSelected,requestSelected);

        //save response in database
        professionalResponseDatabase.child(newResponse.getProfessional().getpID()).child(newResponse.getResponseid()).setValue(newResponse);
        responseDatabase.child(newResponse.getResponseid()).setValue(newResponse);
        userResponseDatabase.child(newResponse.getRequest().getUser().getUID()).child(newResponse.getRequest().getRequestID()).child(newResponse.getResponseid()).setValue(newResponse);

        //remove request from professionalRequest database
        professionalRequestDatabase.child(ProfessionalMainActivity.loggedInUser.getpID()).child(requestSelected.getRequestID()).setValue(null);

        onBackPressed();
    }

    /**********************************************************************************************/
    //decline request method
    private void declineRequest()
    {
        //delete request from professionalRequest database
        professionalRequestDatabase.child(ProfessionalMainActivity.loggedInUser.getpID()).child(requestSelected.getRequestID()).setValue(null);

        onBackPressed();
    }

    /**********************************************************************************************/
    //go to Google Maps method
    private void goToGoogleMaps()
    {
        String uri = "geo:"+requestSelected.getLocationLatitude()+","+requestSelected.getLocationLongitude()
                +"?q="+requestSelected.getLocationLatitude()+","+requestSelected.getLocationLongitude();

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
        firstnameTextView.setText("First name : "+requestSelected.getUser().getFirstName());
        lastnameTextView.setText("Last name : "+requestSelected.getUser().getLastName());
        mobileNumberTextView.setText("Mobile number : "+requestSelected.getUser().getMobileNo());
        distanceTextView.setText("Distance : "+distanceSelected);
        vehicleBrandTextView.setText("Brand : "+requestSelected.getVehicle().getBrand());
        vehicleModelTextView.setText("Model : "+requestSelected.getVehicle().getModel());
        vehicleTypeTextView.setText("Type : "+requestSelected.getVehicle().getType());
        vehicleYearTextView.setText("Year : "+requestSelected.getVehicle().getYear());
        vehicleRegnumTextView.setText("Registration number : "+requestSelected.getVehicle().getRegNum());
        vehicelInsurancenumTextView.setText("Insurance number : "+requestSelected.getVehicle().getInsuranceNum());
        problemTextView.setText(requestSelected.getProblem());
        if (requestSelected.getAdditionalNote().equalsIgnoreCase(""))
        {
            problemDescriptionTextView.setText("Nothing");
        }
        else
        {
            problemDescriptionTextView.setText(requestSelected.getAdditionalNote());
        }
    }

    /**********************************************************************************************/
    //override listener method for back button on action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //if id = back button
        if (item.getItemId()==android.R.id.home)
        {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**********************************************************************************************/
    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
    /**********************************************************************************************/
}
