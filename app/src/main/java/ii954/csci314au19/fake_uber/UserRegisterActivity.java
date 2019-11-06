package ii954.csci314au19.fake_uber;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;
import io.ghyeok.stickyswitch.widget.StickySwitch;

import org.w3c.dom.Text;

import java.util.regex.Pattern;

public class UserRegisterActivity extends AppCompatActivity {

    //variable declaration
    private Button buttonRegister;
    private TextInputLayout emailInput;
    private TextInputLayout passwordInput;
    private TextInputLayout firstnameInput;
    private TextInputLayout lastnameInput;
    private TextInputLayout hpnumberInput;
    private ProgressDialog progressDialog;
    private StickySwitch stickySwitch;
    private TextView switchLabel;

    //firebase authentication variable declaration
    private FirebaseAuth firebaseAuth;

    //firebase realtime database reference variable declaration
    private DatabaseReference userDatabase;
    private DatabaseReference vehicleDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        //firebase authentication variable initialization
        firebaseAuth = FirebaseAuth.getInstance();

        //firebase realtime database reference variable initialization
        userDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.userDatabaseName).toString());
        vehicleDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.vehicleDatabaseName).toString());

        //variable initialization
        progressDialog = new ProgressDialog(this);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        emailInput = (TextInputLayout) findViewById(R.id.text_input_email);
        passwordInput = (TextInputLayout) findViewById(R.id.text_input_password);
        firstnameInput = (TextInputLayout) findViewById(R.id.text_input_FName);
        lastnameInput = (TextInputLayout) findViewById(R.id.text_input_LName);
        hpnumberInput = (TextInputLayout) findViewById(R.id.text_input_hpnumber);
        stickySwitch = (StickySwitch) findViewById(R.id.sticky_switch);
        switchLabel = (TextView) findViewById(R.id.switchLabel);

        //set listener for stickySwitch
        setSwitchListener();

        //set listener for register button
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (v == buttonRegister) {

                    //if register as user
                    if (stickySwitch.getDirection() == StickySwitch.Direction.LEFT) {

                        registerUser();
                    }
                    //if register as professional
                    else if (stickySwitch.getDirection() == StickySwitch.Direction.RIGHT) {

                        registerProfessional();
                    }
                }
            }
        });

        //enable action bar (display back button on action bar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //load textEdits if intent has extras
        loadExtras();

    }

    /**********************************************************************************************/
    private void loadExtras()
    {
        boolean hasExtra = false;

        //retrieve extras
        if (getIntent().hasExtra("email"))
        {
            emailInput.getEditText().setText(getIntent().getExtras().getString("email"));
            hasExtra = true;
        }
        if  (getIntent().hasExtra("firstname"))
        {
            firstnameInput.getEditText().setText(getIntent().getExtras().getString("firstname"));
            hasExtra = true;
        }
        if (getIntent().hasExtra("lastname"))
        {
            lastnameInput.getEditText().setText(getIntent().getExtras().getString("lastname"));
            hasExtra = true;
        }
        if (getIntent().hasExtra("hpnumber"))
        {
            hpnumberInput.getEditText().setText(getIntent().getExtras().getString("hpnumber"));
            hasExtra = true;
        }
        if (getIntent().hasExtra("password"))
        {
            passwordInput.getEditText().setText(getIntent().getExtras().getString("password"));
            hasExtra = true;
        }
        if (hasExtra)
        {
            stickySwitch.setDirection(StickySwitch.Direction.RIGHT);
        }
    }

    /**********************************************************************************************/
    //listener for StickySwitch
    private void setSwitchListener()
    {
        stickySwitch.setOnSelectedChangeListener(new StickySwitch.OnSelectedChangeListener() {
            @Override
            public void onSelectedChange(@NotNull StickySwitch.Direction direction, @NotNull String s) {

                if (direction == StickySwitch.Direction.LEFT)                                       //if USER
                {
                    //change switch label text
                    switchLabel.setText(getText(R.string.switchUserLabel).toString());

                    //change button text
                    buttonRegister.setText(getText(R.string.userSignupButton).toString());

                    Toast.makeText(getApplicationContext()
                            ,"You are registering as a user now",Toast.LENGTH_SHORT).show();
                }
                else if (direction == StickySwitch.Direction.RIGHT)                                 //if PROFESSIONAL
                {
                    //change switch label text
                    switchLabel.setText(getText(R.string.switchProfessionalLabel).toString());

                    //change button text
                    buttonRegister.setText(getText(R.string.professionalNextButton).toString());

                    Toast.makeText(getApplicationContext()
                            ,"You are registering as a professional now",Toast.LENGTH_SHORT).show();
                }
            }
        });
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
    //validation method
    private boolean inputValidated()
    {
        //get inputs
        final String firstname = firstnameInput.getEditText().getText().toString().trim();
        final String lastname = lastnameInput.getEditText().getText().toString().trim();
        final String hpnumber = hpnumberInput.getEditText().getText().toString().trim();
        final String email = emailInput.getEditText().getText().toString().trim();
        final String password = passwordInput.getEditText().getText().toString().trim();

        //empty validation
        if (TextUtils.isEmpty(firstname)){
            Toast.makeText(this,"Please enter first name",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (TextUtils.isEmpty(lastname)){
            Toast.makeText(this,"Please enter last name",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (TextUtils.isEmpty(hpnumber)){
            Toast.makeText(this,"Please enter mobile number",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password.", Toast.LENGTH_SHORT).show();
            return false;
        }

        //other validation
        if (hpnumber.length()<9)                                    //phone number at least 9 digit
        {
            Toast.makeText(this,"Invalid mobile number format",Toast.LENGTH_LONG).show();
            return false;
        }
        else if (!Patterns.PHONE.matcher(hpnumber).matches())       //phone number matches regex pattern
        {
            Toast.makeText(this,"Invalid mobile number format",Toast.LENGTH_LONG).show();
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())   //email matches regex pattern
        {
            Toast.makeText(this,"Invalid email format",Toast.LENGTH_LONG).show();
            return false;
        }
        else if (password.length()<8)                               //password at least 8 digit
        {
            Toast.makeText(this,"Password must have at least 8 characters"
                    ,Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    /**********************************************************************************************/
    //register professional (go to next page for document submission)
    private void registerProfessional()
    {
        //input validation
        if (!inputValidated())
        {
            return;
        }

        //get inputs
        String firstn = firstnameInput.getEditText().getText().toString().trim();
        String lastn = lastnameInput.getEditText().getText().toString().trim();
        final String hpnumber = hpnumberInput.getEditText().getText().toString().trim();
        final String email = emailInput.getEditText().getText().toString().trim();
        final String password = passwordInput.getEditText().getText().toString().trim();

        final String firstname = firstn.substring(0,1).toUpperCase()+firstn.substring(1,firstn.length());
        final String lastname = lastn.substring(0,1).toUpperCase()+lastn.substring(1,lastn.length());

        //open professionalDocSubmitActivity to submit certificate pictures
        Intent newIntent = new Intent(UserRegisterActivity.this,ProfessionalDocSubmitActivity.class);

        newIntent.putExtra("firstname",firstname);
        newIntent.putExtra("lastname",lastname);
        newIntent.putExtra("password",password);
        newIntent.putExtra("email",email);
        newIntent.putExtra("hpnumber",hpnumber);

        finish();
        startActivity(newIntent);
    }

    /**********************************************************************************************/
    //firebase authentication connection to register user
    private void registerUser()
    {
        //input validation
        if (!inputValidated())
        {
            return;
        }

        //get inputs
        String firstn = firstnameInput.getEditText().getText().toString().trim();
        String lastn = lastnameInput.getEditText().getText().toString().trim();
        final String hpnumber = hpnumberInput.getEditText().getText().toString().trim();
        final String email = emailInput.getEditText().getText().toString().trim();
        final String password = passwordInput.getEditText().getText().toString().trim();

        final String firstname = firstn.substring(0,1).toUpperCase()+firstn.substring(1,firstn.length());
        final String lastname = lastn.substring(0,1).toUpperCase()+lastn.substring(1,lastn.length());

        //display progress dialog
        progressDialog.setMessage("Registering User...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //firebase authentication connection to register user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //save user details in database
                        if (task.isSuccessful()) {

                            //create new user
                            User newUser = new User(email,firstname,lastname,hpnumber,5,1);

                            //get user new database key
                            String uid = newUser.getUID();

                            //enter user into user database
                            userDatabase.child(uid).setValue(newUser);

                            //create new field in vehicle database
                            vehicleDatabase.child(uid).child("totalVehicle").setValue(0);

                            //show Toast that user has been successfully registered
                            Toast.makeText(getApplicationContext()
                                    , "Register Successful", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                            //go to Select membership activity
                            finish();

                            Intent intent = new Intent(UserRegisterActivity.this,SelectMembershipActivity.class);
                            intent.putExtra("UID",uid); //send uid to next activity

                            startActivity(intent);

                            //startActivity(new Intent(UserRegisterActivity.this,LoginActivity.class));//for debug go to login page
                        }
                        else {

                            Toast.makeText(getApplicationContext()
                                    , "Register Unsuccessful. Please try again.", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    /**********************************************************************************************/
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        startActivity(new Intent(UserRegisterActivity.this,LoginActivity.class));
    }


}
