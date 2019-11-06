package ii954.csci314au19.fake_uber;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditAccountProfessionalActivity extends AppCompatActivity implements DialogInterface.OnClickListener{

    //variable declaration
    private Button saveChangesButton;
    private TextInputLayout emailInput;
    private TextInputLayout firstnameInput;
    private TextInputLayout lastnameInput;
    private TextInputLayout hpnumberInput;
    private ProgressDialog progressDialog;
    private Professional newUser;

    //side bar variable declaration
    private DrawerLayout myDrawerLayout;
    private ActionBarDrawerToggle mySideBar;

    //sidebar header variable declaration
    public TextView accountName;
    public TextView accountRating;

    //firebase realtime user database reference variable declaration
    private DatabaseReference professionalDatabase;
    private String professionalRef;
    private String firstname;
    private String lastname;
    private String hpnumber;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account_professional);

        //variable initialization
        saveChangesButton = (Button) findViewById(R.id.save_changes_button);
        emailInput = (TextInputLayout) findViewById(R.id.emailEditText);
        firstnameInput = (TextInputLayout) findViewById(R.id.fnameEditText);
        lastnameInput = (TextInputLayout) findViewById(R.id.lnameEditText);
        hpnumberInput = (TextInputLayout) findViewById(R.id.hpnumberEditText);
        progressDialog = new ProgressDialog(this);

        //firebase realtime user database reference variable initialization
        professionalDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.professionalDatabaseName).toString());

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

        //set listener for save changes button
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        //load user details
        loadDetails();
    }

    /**********************************************************************************************/
    @Override
    protected void onStart() {
        super.onStart();

        professionalDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren())
                {
                    Professional current = ds.getValue(Professional.class);
                    //if email in database is the same as loggedinuser email
                    if (current.getEmail().equalsIgnoreCase(ProfessionalMainActivity.loggedInUser.getEmail()))
                    {
                        //get user key in database
                        professionalRef = ds.getKey();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**********************************************************************************************/
    //save changes function when save changes button is clicked
    private void saveChanges()
    {
        firstname = firstnameInput.getEditText().getText().toString();
        lastname = lastnameInput.getEditText().getText().toString();
        hpnumber = hpnumberInput.getEditText().getText().toString();
        email = emailInput.getEditText().getText().toString();

        //no changes validation
        if ((firstname.equalsIgnoreCase(ProfessionalMainActivity.loggedInUser.getFirstName()))
                &&(lastname.equalsIgnoreCase(ProfessionalMainActivity.loggedInUser.getLastName()))
                &&(hpnumber.equalsIgnoreCase(ProfessionalMainActivity.loggedInUser.getMobileNo())))
        {
            Toast.makeText(this,"There's no changes to be made",Toast.LENGTH_LONG).show();
        }
        else
        {
            //empty validation
            if (firstname.equalsIgnoreCase(""))
            {
                Toast.makeText(this,"Please enter first name",Toast.LENGTH_SHORT).show();
            }
            else if (lastname.equalsIgnoreCase(""))
            {
                Toast.makeText(this,"Please enter last name",Toast.LENGTH_SHORT).show();
            }
            else if (hpnumber.equalsIgnoreCase(""))
            {
                Toast.makeText(this,"Please enter phone number",Toast.LENGTH_SHORT).show();
            }
            else
            {
                //display alert dialog to get confirmation
                AlertDialog.Builder confirmation = new AlertDialog.Builder(this);
                confirmation.setMessage("Confirm changes?");
                confirmation.setNegativeButton("Cancel", this);
                confirmation.setPositiveButton("Yes", this);
                confirmation.create().show();
            }
        }
    }

    //listener for alert dialog
    @Override
    public void onClick(DialogInterface dialog, int which) {

        switch (which)
        {
            case -2: //NegativeButton (Cancel)
            {
                dialog.dismiss();
            }
            break;

            case -3: //NeutralButton (Dismiss)
            {
                dialog.dismiss();
            }
            break;

            case -1: //PositiveButton (Yes)
            {
                //save changes
                saving();
            }
            break;
        }
    }

    //saving method after receiving confirmation
    private void saving()
    {
        //display progress dialog
        progressDialog.setMessage("Saving...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //create new user details with old pid
        String oldUserRef = ProfessionalMainActivity.loggedInUser.getpID();
        Professional oldUser = ProfessionalMainActivity.loggedInUser;
        newUser = new Professional(email,firstname,lastname,hpnumber,oldUserRef
                ,oldUser.getLicenseFrontLink(),oldUser.getLicenseBackLink(),oldUser.getRating()
                ,oldUser.getTotalRating(),oldUser.isVerified());

        /*
        //delete old user values from database
        DatabaseReference oldUserRef;
        oldUserRef = FirebaseDatabase.getInstance()
                .getReference(getText(R.string.userDatabaseName).toString()).child(userRef);
        oldUserRef.removeValue();
       */

        //update new professional to database
        professionalDatabase.child(oldUserRef).setValue(newUser);

        //assign new user details to loggedinuser
        ProfessionalMainActivity.loggedInUser = new Professional(newUser.getEmail(),newUser.getFirstName()
                ,newUser.getLastName(),newUser.getMobileNo(),newUser.getpID(),newUser.getLicenseFrontLink()
                ,newUser.getLicenseBackLink(),newUser.getRating()
                ,newUser.getTotalRating(),newUser.isVerified());

        //cancel progress dialog
        progressDialog.cancel();
        Toast.makeText(this,"Changes saved !",Toast.LENGTH_LONG).show();

        //update details with new user details
        loadDetails();
    }

    /**********************************************************************************************/
    //load details function
    private void loadDetails()
    {
        firstnameInput.getEditText().setText(ProfessionalMainActivity.loggedInUser.getFirstName());
        lastnameInput.getEditText().setText(ProfessionalMainActivity.loggedInUser.getLastName());
        hpnumberInput.getEditText().setText(ProfessionalMainActivity.loggedInUser.getMobileNo());
        emailInput.setEnabled(true);
        emailInput.getEditText().setText(ProfessionalMainActivity.loggedInUser.getEmail());
        emailInput.setEnabled(false);
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
                        onBackPressed();
                    }
                    break;

                    case R.id.nav_account:
                    {
                        //close side bar
                        myDrawerLayout.closeDrawer(Gravity.LEFT);
                    }
                    break;

                    case R.id.nav_history:
                    {
                        //go to historyProfessional activity
                        finish();
                        startActivity(new Intent(EditAccountProfessionalActivity.this,HistoryProfessionalActivity.class));
                    }
                    break;

                    case R.id.nav_jobs:
                    {
                        //go to job activity
                        finish();
                        startActivity(new Intent(EditAccountProfessionalActivity.this,JobActivity.class));
                    }
                    break;

                    case R.id.nav_settings:
                    {
                        //go to settings page
                        finish();
                        startActivity(new Intent(EditAccountProfessionalActivity.this,SettingsProfessionalActivity.class));
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
        if (ProfessionalMainActivity.loggedInUser==null){
            accountName.setText("User name default");
            accountRating.setText("5.0");
        }
        else {
            accountName.setText(ProfessionalMainActivity.loggedInUser.getFirstName() + " " + ProfessionalMainActivity.loggedInUser.getLastName());
            accountRating.setText(String.format("%.1f",((double)ProfessionalMainActivity.loggedInUser.getRating()/(double)ProfessionalMainActivity.loggedInUser.getTotalRating())));
        }
    }

    /**********************************************************************************************/
    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
    /**********************************************************************************************/

}
