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
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ReportActivity extends AppCompatActivity {

    //variable declaration
    private Job jobSelected;
    private boolean fromUser;
    private String reportText;
    private Report report;

    private TextInputLayout pidTextInput;
    private TextInputLayout jobidTextInput;
    private TextInputLayout reportTextInput;
    private Button reportButton;

    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;
    private AlertDialog.Builder confirmationAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        //enable action bar (display back button on action bar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //variable declaration
        jobSelected = null;
        fromUser = false;
        reportText = "";
        report = null;

        pidTextInput = (TextInputLayout) findViewById(R.id.pidTextInput);
        jobidTextInput = (TextInputLayout) findViewById(R.id.jobidTextInput);
        reportTextInput = (TextInputLayout) findViewById(R.id.reportTextInput);
        reportButton = (Button) findViewById(R.id.reportButton);

        calendar = Calendar.getInstance();
        confirmationAlert = new AlertDialog.Builder(this);

        //get extras
        retrieveExtras();

        //load details
        loadDetails();

        //onclick listener for reportButton
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                sendReport();
            }
        });
    }

    /**********************************************************************************************/
    //send report method
    private void sendReport()
    {
        //get input
        reportText = reportTextInput.getEditText().getText().toString().trim();

        //empty validation
        if (reportText.equalsIgnoreCase(""))
        {
            Toast.makeText(getApplicationContext(),"Please enter complaints",Toast.LENGTH_SHORT).show();
            return;
        }

        confirmationAlert.setMessage("Confirm send report ?");
        confirmationAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //create new report
                simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
                String id = "rep";
                if (fromUser)
                {
                    id += jobSelected.getRequest().getUser().getUID()+jobSelected.getResponse().getProfessional().getpID()+simpleDateFormat.format(calendar.getTime());
                    report = new Report(id,jobSelected.getRequest().getUser().getUID(),jobSelected.getResponse().getProfessional().getpID(),jobSelected.getJobid(),reportText);
                }
                else
                {
                    id += jobSelected.getResponse().getProfessional().getpID()+jobSelected.getRequest().getUser().getUID()+simpleDateFormat.format(calendar.getTime());
                    report = new Report(id,jobSelected.getResponse().getProfessional().getpID(),jobSelected.getRequest().getUser().getUID(),jobSelected.getJobid(),reportText);
                }

                //save to database
                DatabaseReference reportDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.reportDatabaseName).toString());

                reportDatabase.child(report.getReportID()).setValue(report);

                Toast.makeText(getApplicationContext(),"Report sent !!!",Toast.LENGTH_SHORT).show();

                //go back
                dialog.dismiss();
                onBackPressed();
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

    /**********************************************************************************************/
    //load details method
    private void loadDetails()
    {
        if (fromUser)
        {
            pidTextInput.getEditText().setText(jobSelected.getResponse().getProfessional().getpID());
            pidTextInput.setHint("Professional ID");
            pidTextInput.getEditText().setHint("Professional ID");
        }
        else
        {
            pidTextInput.getEditText().setText(jobSelected.getRequest().getUser().getUID());
            pidTextInput.setHint("User ID");
            pidTextInput.getEditText().setHint("User ID");
        }
        jobidTextInput.getEditText().setText(jobSelected.getJobid());
    }

    /**********************************************************************************************/
    //get extras from intent method
    private void retrieveExtras()
    {
        if (getIntent().hasExtra("fromUser"))
        {
            fromUser = getIntent().getExtras().getBoolean("fromUser");

            if (fromUser)
            {
                jobSelected = JobHistoryActivity.jobSelected;
                getSupportActionBar().setTitle("Report Professional");
            }
            else
            {
                jobSelected = JobHistoryProfessionalActivity.jobSelected;
                getSupportActionBar().setTitle("Report User");
            }
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

        if (fromUser)
        {
            //override and go to jobServiceDetails activity
            finish();
            startActivity(new Intent(ReportActivity.this,JobServiceDetailsActivity.class));
        }
        else
        {
            //override and go to jobDetails activity
            finish();
            startActivity(new Intent(ReportActivity.this,JobDetailsActivity.class));
        }

    }
    /**********************************************************************************************/
}
