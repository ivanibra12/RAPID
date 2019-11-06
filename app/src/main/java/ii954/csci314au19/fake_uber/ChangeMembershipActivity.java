package ii954.csci314au19.fake_uber;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;

public class ChangeMembershipActivity extends AppCompatActivity {

    //variable declaration
    private TextView membershipTextView;
    private TextView descriptionTextview;
    private TextView startDateTextView;
    private TextView endDateTextView;
    private Button upgradeButton;
    private Membership membership;
    private SimpleDateFormat simpleDateFormat;

    //firebase database reference variable declaration
    private DatabaseReference membershipDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_membership);

        //enable action bar (display back button on action bar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //variable initialization
        membershipTextView = (TextView) findViewById(R.id.membershipTextView);
        descriptionTextview = (TextView) findViewById(R.id.descriptionTextView);
        startDateTextView = (TextView) findViewById(R.id.startDateTextView);
        endDateTextView = (TextView) findViewById(R.id.endDateTextView);
        upgradeButton = (Button) findViewById(R.id.upgradeButton);
        membership = UserMainActivity.userMembership;
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        //firebase database reference variable declaration
        membershipDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.membershipDatabaseName).toString()).child(UserMainActivity.loggedInUser.getUID());

        //load details
        loadDetails();

        //listener for upgrade button
        upgradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //call upgrade function
                upgrade();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        membershipDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //load details
                loadDetails();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**********************************************************************************************/
    //upgrade function
    private void upgrade()
    {
        //go to selectMembership page
        finish();
        Intent intent = new Intent(ChangeMembershipActivity.this,SelectMembershipActivity.class);
        intent.putExtra("UID",UserMainActivity.loggedInUser.getUID());
        intent.putExtra("changeMembership",true);

        startActivity(intent);
    }

    /**********************************************************************************************/
    //load details function
    private void loadDetails()
    {
        //if user has membership
        if (membership!=null)
        {
            //load details
            if (membership.getDuration().equalsIgnoreCase("3 month"))
            {
                membershipTextView.setText(getText(R.string.quarterYearText).toString());
                descriptionTextview.setText(getText(R.string.quarterYearDesc).toString());
            }
            else if (membership.getDuration().equalsIgnoreCase("6 month"))
            {
                membershipTextView.setText(getText(R.string.halfYearText).toString());
                descriptionTextview.setText(getText(R.string.halfYearDesc).toString());
            }
            else if (membership.getDuration().equalsIgnoreCase("1 year"))
            {
                membershipTextView.setText(getText(R.string.annualText).toString());
                descriptionTextview.setText(getText(R.string.annualDesc).toString());
            }

            startDateTextView.setText("Start date : "+simpleDateFormat.format(membership.getStartDate()));
            endDateTextView.setText("End date : "+simpleDateFormat.format(membership.getEndDate()));

            //make upgrade button invisible and disabled
            upgradeButton.setEnabled(false);
            upgradeButton.setVisibility(View.INVISIBLE);
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

        //override and go to History activity
        finish();
        startActivity(new Intent(ChangeMembershipActivity.this,SettingsActivity.class));
    }
    /**********************************************************************************************/
}
