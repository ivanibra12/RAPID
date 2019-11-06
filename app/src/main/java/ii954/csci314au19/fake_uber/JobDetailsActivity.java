package ii954.csci314au19.fake_uber;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;

public class JobDetailsActivity extends AppCompatActivity {

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

    private TextView jobDateTextView;
    private TextView jobTimeTextView;
    private TextView jobStatusTextView;

    private Button reportButton;

    private Job jobSelected;
    private SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        //enable action bar (display back button on action bar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

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

        jobDateTextView = (TextView) findViewById(R.id.jobDateTextView);
        jobTimeTextView = (TextView) findViewById(R.id.jobTimeTextView);
        jobStatusTextView = (TextView) findViewById(R.id.jobStatusTextView);

        reportButton = (Button) findViewById(R.id.reportButton);

        jobSelected = JobHistoryProfessionalActivity.jobSelected;

        //load details
        loadDetails();

        //onclick listener for reportButton
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                reportUser();
            }
        });


    }

    /**********************************************************************************************/
    //report professional method
    private void reportUser()
    {
        Intent intent = new Intent(JobDetailsActivity.this,ReportActivity.class);
        intent.putExtra("fromUser",false);

        finish();
        startActivity(intent);
    }

    /**********************************************************************************************/
    //load details method
    private void loadDetails()
    {
        requestIDTextView.setText("Request ID : " + jobSelected.getRequest().getRequestID());
        vehicleTextView.setText("Vehicle : " + jobSelected.getRequest().getVehicle().getNickname() + " (" + jobSelected.getRequest().getVehicle().getRegNum() + ")");
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        requestDateTextView.setText("Request date : " + simpleDateFormat.format(jobSelected.getRequest().getRequestDateTime()));
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        requestTimeTextView.setText("Request time : " + simpleDateFormat.format(jobSelected.getRequest().getRequestDateTime()));
        problemTextView.setText("Problem : " + jobSelected.getRequest().getProblem());
        problemDescTextView.setText("Additional note : " + jobSelected.getRequest().getAdditionalNote());

        firstnameTextView.setText("First name : "+jobSelected.getRequest().getUser().getFirstName());
        lastnameTextView.setText("Last name : "+jobSelected.getRequest().getUser().getLastName());
        mobileNumberTextView.setText("Mobile number : "+jobSelected.getRequest().getUser().getMobileNo());

        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        jobDateTextView.setText("Date created : "+simpleDateFormat.format(jobSelected.getJobDateTime()));
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        jobTimeTextView.setText("Time created : "+simpleDateFormat.format(jobSelected.getJobDateTime()));
        jobStatusTextView.setText("Job status : "+jobSelected.getStatus());
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

        //override and go to jobHistory activity
        finish();
        startActivity(new Intent(JobDetailsActivity.this,JobHistoryProfessionalActivity.class));
    }
    /**********************************************************************************************/
}
