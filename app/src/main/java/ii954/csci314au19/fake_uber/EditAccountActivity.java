package ii954.csci314au19.fake_uber;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditAccountActivity extends AppCompatActivity implements DialogInterface.OnClickListener{

    //variable declaration
    private Button saveChangesButton;
    private TextInputLayout emailInput;
    private TextInputLayout firstnameInput;
    private TextInputLayout lastnameInput;
    private TextInputLayout hpnumberInput;
    private ProgressDialog progressDialog;
    private User newUser;

    //side bar variable declaration
    private DrawerLayout myDrawerLayout;
    private ActionBarDrawerToggle mySideBar;

    //sidebar header variable declaration
    public TextView accountName;
    public TextView accountRating;

    //firebase realtime user database reference variable declaration
    private DatabaseReference userDatabase;
    private String userRef;
    private String firstname;
    private String lastname;
    private String hpnumber;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        //variable initialization
        saveChangesButton = (Button) findViewById(R.id.save_changes_button);
        emailInput = (TextInputLayout) findViewById(R.id.emailEditText);
        firstnameInput = (TextInputLayout) findViewById(R.id.fnameEditText);
        lastnameInput = (TextInputLayout) findViewById(R.id.lnameEditText);
        hpnumberInput = (TextInputLayout) findViewById(R.id.hpnumberEditText);
        progressDialog = new ProgressDialog(this);

        //firebase realtime user database reference variable initialization
        userDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.userDatabaseName).toString());

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

        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren())
                {
                    User current = ds.getValue(User.class);
                    //if email in database is the same as loggedinuser email
                    if (current.getEmail().equalsIgnoreCase(UserMainActivity.loggedInUser.getEmail()))
                    {
                        //get user key in database
                        userRef = ds.getKey();
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
        if ((firstname.equalsIgnoreCase(UserMainActivity.loggedInUser.getFirstName()))
                &&(lastname.equalsIgnoreCase(UserMainActivity.loggedInUser.getLastName()))
                &&(hpnumber.equalsIgnoreCase(UserMainActivity.loggedInUser.getMobileNo())))
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

        //create new user details with old uid
        String oldUserRef = UserMainActivity.loggedInUser.getUID();
        newUser = new User(email,firstname,lastname,hpnumber,oldUserRef,UserMainActivity.loggedInUser.getRating(),UserMainActivity.loggedInUser.getTotalRating());

        /*
        //delete old user values from database
        DatabaseReference oldUserRef;
        oldUserRef = FirebaseDatabase.getInstance()
                .getReference(getText(R.string.userDatabaseName).toString()).child(userRef);
        oldUserRef.removeValue();
       */

        //update new user to database
        userDatabase.child(oldUserRef).setValue(newUser);

        //assign new user details to loggedinuser
        UserMainActivity.loggedInUser = new User(newUser.getEmail(),newUser.getFirstName()
                ,newUser.getLastName(),newUser.getMobileNo(),newUser.getRating(),newUser.getTotalRating());

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
        firstnameInput.getEditText().setText(UserMainActivity.loggedInUser.getFirstName());
        lastnameInput.getEditText().setText(UserMainActivity.loggedInUser.getLastName());
        hpnumberInput.getEditText().setText(UserMainActivity.loggedInUser.getMobileNo());
        emailInput.setEnabled(true);
        emailInput.getEditText().setText(UserMainActivity.loggedInUser.getEmail());
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
                        startActivity(new Intent(EditAccountActivity.this,UserMainActivity.class));
                    }
                    break;

                    case R.id.nav_account:
                    {
                        //close side bar
                        myDrawerLayout.closeDrawer(Gravity.LEFT);
                    }
                    break;

                    case R.id.nav_vehicles:
                    {
                        //go to my vehicle page
                        finish();
                        startActivity(new Intent(EditAccountActivity.this,MyVehicleActivity.class));
                    }
                    break;

                    case R.id.nav_history:
                    {
                        //go to history page
                        finish();
                        startActivity(new Intent(EditAccountActivity.this,HistoryActivity.class));
                    }
                    break;

                    case R.id.nav_responses:
                    {
                        //go to responses page
                        finish();
                        startActivity(new Intent(EditAccountActivity.this,ResponsesActivity.class));
                    }
                    break;

                    case R.id.nav_settings:
                    {
                        //go to settings page
                        finish();
                        startActivity(new Intent(EditAccountActivity.this,SettingsActivity.class));
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
        startActivity(new Intent(EditAccountActivity.this,UserMainActivity.class));
    }
    /**********************************************************************************************/

}
