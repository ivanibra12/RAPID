package ii954.csci314au19.fake_uber;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.Calendar;

public class JobFinishActivity extends AppCompatActivity {

    //variable declaration
    private TextInputLayout reportTextInput;
    private TextView statusTextView;
    private LinearLayout ratingLinearLayout;
    private ImageView star1;
    private ImageView star2;
    private ImageView star3;
    private ImageView star4;
    private ImageView star5;
    private Button submitReportButton;

    private Job ongoingJob;
    private int star;
    private String report;
    private AlertDialog.Builder confirmationAlert;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_finish);

        //enable action bar (display back button on action bar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //variable initialization
        reportTextInput = (TextInputLayout) findViewById(R.id.reportTextInput);
        statusTextView = (TextView) findViewById(R.id.statusTextView);
        ratingLinearLayout = (LinearLayout) findViewById(R.id.ratingLinearLayout);
        star1 = (ImageView) findViewById(R.id.star1);
        star2 = (ImageView) findViewById(R.id.star2);
        star3 = (ImageView) findViewById(R.id.star3);
        star4 = (ImageView) findViewById(R.id.star4);
        star5 = (ImageView) findViewById(R.id.star5);
        submitReportButton = (Button) findViewById(R.id.submitReportButton);

        ongoingJob = ProfessionalMainActivity.ongoingJob;
        star = 5;
        report = "";
        confirmationAlert = new AlertDialog.Builder(this);

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

        //onclick listener for submitReportButton
        submitReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                confirmationAlert.setMessage("Confirm end job?");
                confirmationAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        submitReport();
                    }
                });
                confirmationAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
                confirmationAlert.show();
            }
        });


        //load details
        loadDetails();
    }

    /**********************************************************************************************/
    //submit report method
    private void submitReport()
    {
        //get inputs
        report = reportTextInput.getEditText().getText().toString().trim();

        ongoingJob.setReport(report);
        User user = ongoingJob.getRequest().getUser();
        user.setRating(user.getRating()+(double)star);
        user.setTotalRating(user.getTotalRating()+1);

        //save to database
        DatabaseReference professionalJobDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.professionalJobDatabaseName).toString()).child(ongoingJob.getResponse().getProfessional().getpID());
        DatabaseReference jobDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.jobDatabaseName).toString());
        DatabaseReference ongoingJobDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.ongoingJobDatabaseName).toString());
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.userDatabaseName).toString()).child(user.getUID());

        professionalJobDatabase.child(ongoingJob.getJobid()).setValue(ongoingJob);
        jobDatabase.child(ongoingJob.getJobid()).setValue(ongoingJob);
        ongoingJobDatabase.child(ongoingJob.getJobid()).setValue(ongoingJob);
        userDatabase.setValue(user);

        //if job is solved
        if (ongoingJob.getStatus().equalsIgnoreCase("solved"))
        {
            //create new transaction
            calendar = Calendar.getInstance();
            simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
            String id = ongoingJob.getResponse().getProfessional().getpID() + simpleDateFormat.format(calendar.getTime());
            String desc = "Job payment (" + ongoingJob.getJobid() + ")";
            Transaction transaction = new Transaction(id, ongoingJob.getResponse().getProfessional().getpID(), desc, 10);

            //save transaction to database
            DatabaseReference outgoingTransactionDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.outgoingTransactionDatabaseName).toString());
            DatabaseReference professionalTransactionDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.professionalTransactionDatabaseName).toString());

            outgoingTransactionDatabase.child(transaction.getTransactionID()).setValue(transaction);
            professionalTransactionDatabase.child(ongoingJob.getResponse().getProfessional().getpID()).child(transaction.getTransactionID()).setValue(transaction);
        }

        ProfessionalMainActivity.ongoingJob = null;
        finish();
    }

    /**********************************************************************************************/
    //load details method
    private void loadDetails()
    {
        statusTextView.setText(ongoingJob.getStatus());

        if (ongoingJob.getStatus().equalsIgnoreCase("unsolved"))
        {
            ratingLinearLayout.setVisibility(View.INVISIBLE);
        }
        else
        {
            ratingLinearLayout.setVisibility(View.VISIBLE);
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

        //override and go to Job activity
        finish();
        startActivity(new Intent(JobFinishActivity.this,JobActivity.class));
    }
    /**********************************************************************************************/
}
