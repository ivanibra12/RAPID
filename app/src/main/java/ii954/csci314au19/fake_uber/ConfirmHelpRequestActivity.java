package ii954.csci314au19.fake_uber;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class ConfirmHelpRequestActivity extends AppCompatActivity implements DialogInterface.OnClickListener{

    //variable declaration
    private TextView nicknameTextView;
    private TextView brandTextView;
    private TextView modelTextView;
    private TextView yearTextView;
    private TextView typeTextView;
    private TextView regnumTextView;
    private TextView insurancenumTextView;
    private TextView problemTextView;
    private TextView textCountTextView;
    private TextInputLayout additionalMessageInputLayout;
    private Button confirmButton;
    private AlertDialog.Builder confirmAlert;

    private String problem;
    private Car vehicle;
    private String longitude;
    private String latitude;
    private String userIP;
    private String amount;
    private Request request;

    //static variable declaration
    public static TextView paymentSuccessfulTextView;

    //firebase database reference variable declaration
    private DatabaseReference requestDatabase;
    private DatabaseReference userRequestDatabase;
    private DatabaseReference ongoingRequestDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_help_request);

        //enable action bar (display back button on action bar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //firebase database reference variable initialization
        requestDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.requestDatabaseName).toString());
        userRequestDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.userRequestDatabaseName).toString());
        ongoingRequestDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.ongoingRequestDatabaseName).toString());

        //variable initialization
        problem = UserMainActivity.problemChosen;
        vehicle = UserMainActivity.vehicleChosen;
        amount = getText(R.string.pay2goPrice).toString();
        request = null;
        nicknameTextView = (TextView) findViewById(R.id.vehicleNicknameTextView);
        brandTextView = (TextView) findViewById(R.id.vehicleBrandTextView);
        modelTextView = (TextView) findViewById(R.id.vehicleModelTextView);
        yearTextView = (TextView) findViewById(R.id.vehicleYearTextView);
        typeTextView = (TextView) findViewById(R.id.vehicleTypeTextView);
        regnumTextView = (TextView) findViewById(R.id.vehicleRegnumTextView);
        insurancenumTextView = (TextView) findViewById(R.id.vehicleInsurancenumTextView);
        problemTextView = (TextView) findViewById(R.id.problemTextView);
        textCountTextView = (TextView) findViewById(R.id.textCountTextView);
        additionalMessageInputLayout = (TextInputLayout) findViewById(R.id.additionalMessageInputLayout);
        confirmButton = (Button) findViewById(R.id.confirmButton);

        confirmAlert = new AlertDialog.Builder(this);
        confirmAlert.setMessage("Help has been requested !");
        confirmAlert.setPositiveButton("OK", this);

        //static variable initialization
        paymentSuccessfulTextView = (TextView) findViewById(R.id.paymentSuccessfulTextView);

        //load view details
        loadDetails();

        //listener for additionalMessageInputLayout to display wordCount
        additionalMessageInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //get textCount
                int textCount = s.length();

                //display wordCount
                textCountTextView.setText(textCount+"/300");

                //Toast.makeText(getApplicationContext(),textCount+"",Toast.LENGTH_SHORT).show(); //to debug display word count
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //listener for confirmButton
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //call confirmRequestHelp method
                confirmRequestHelp();
            }
        });


        //get extras from intent
        if (getIntent().hasExtra("longitude"))
        {
            longitude = getIntent().getExtras().getString("longitude");
        }
        if (getIntent().hasExtra("latitude"))
        {
            latitude = getIntent().getExtras().getString("latitude");
        }
        if (getIntent().hasExtra("userIP"))
        {
            userIP = getIntent().getExtras().getString("userIP");
        }


        //listener when payment is successful
        paymentSuccessfulTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                onPaymentSuccessful();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    /**********************************************************************************************/
    //listener for alert dialog
    @Override
    public void onClick(DialogInterface dialog, int which) {

        switch (which)
        {
            case -2: //NegativeButton (Cancel)
            {
                goToResponses();
            }
            break;

            case -3: //NeutralButton (Dismiss)
            {
                goToResponses();
            }
            break;

            case -1: //PositiveButton (OK)
            {
                goToResponses();
            }
            break;
        }
    }

    /**********************************************************************************************/
    //confirm request help method
    private void confirmRequestHelp()
    {
        //get additional notes if any
        String additionalNote = additionalMessageInputLayout.getEditText().getText().toString();

        //create new Request
        request = new Request(UserMainActivity.loggedInUser,vehicle,userIP,longitude,latitude,problem,additionalNote);

        //if user is not a member
        if (UserMainActivity.userMembership==null)
        {
            //pause this activity and go to payment method activity
            Intent intent = new Intent(ConfirmHelpRequestActivity.this,PaymentMethodActivity.class);
            intent.putExtra("amount",amount);
            intent.putExtra("paymentText","Pay2Go");
            intent.putExtra("UID",UserMainActivity.loggedInUser.getUID());
            intent.putExtra("requestID",request.getRequestID());

            startActivity(intent);

            //go to payment page
            //PaypalPayment paypalPayment = new PaypalPayment(amount,"Pay2Go",getApplicationContext(),UserMainActivity.loggedInUser.getUID(),request.getRequestID());
        }
        else
        {
            onPaymentSuccessful();
        }




    }

    private void onPaymentSuccessful()
    {
        //save details to database
        requestDatabase.child(request.getRequestID()).setValue(request);
        userRequestDatabase.child(UserMainActivity.loggedInUser.getUID()).child(request.getRequestID()).setValue(request);
        ongoingRequestDatabase.child(request.getRequestID()).setValue(request);

        //send request to server
        SendMessage sendMessage = new SendMessage();
        sendMessage.execute("request,"+request.getRequestID());
        //sendMessage.execute("request,"+UserMainActivity.loggedInUser.getUID()+","+UserMainActivity.vehicleChosen.getRegNum()+","+UserMainActivity.problemChosen);

        //Toast.makeText(this,"Help has been requested",Toast.LENGTH_SHORT).show();

        //go to responses page
        //confirmAlert.setMessage(request.getRequestDetails()+"");
        confirmAlert.create().show();
    }

    private void goToResponses()
    {
        finish();
        startActivity(new Intent(ConfirmHelpRequestActivity.this,ResponsesActivity.class));
    }

    /**********************************************************************************************/
    //load details method
    private void loadDetails()
    {
        nicknameTextView.setText(vehicle.getNickname());
        brandTextView.setText("Brand : "+vehicle.getBrand());
        modelTextView.setText("Model : "+vehicle.getModel());
        yearTextView.setText("Year : "+vehicle.getYear());
        typeTextView.setText("Vehicle type : "+vehicle.getType());
        regnumTextView.setText("Registration number : "+vehicle.getRegNum());
        insurancenumTextView.setText("Insurance number : "+vehicle.getInsuranceNum());
        problemTextView.setText(problem);
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

        //override and go to Main activity
        finish();
        startActivity(new Intent(ConfirmHelpRequestActivity.this,UserMainActivity.class));
    }
    /**********************************************************************************************/
}
