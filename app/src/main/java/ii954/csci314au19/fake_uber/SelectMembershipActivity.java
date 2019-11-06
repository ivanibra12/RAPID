package ii954.csci314au19.fake_uber;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

public class SelectMembershipActivity extends AppCompatActivity {

    //variable declaration
    private Switch pay2goSwitch;
    private Switch quarterSwitch;
    private Switch halfSwitch;
    private Switch yearlySwitch;
    private String membershipType;
    private Button continueButton;
    private LinearLayout pay2goLayout;
    private LinearLayout quarterLayout;
    private LinearLayout halfLayout;
    private LinearLayout yearlyLayout;
    private String UID;

    //static variable declaration
    public static Activity activity;
    public static boolean changeMembership;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_membership);

        //enable action bar (display back button on action bar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //variable initialization
        membershipType = "pay2go";//pay2go/quarter/half/annual
        pay2goSwitch = (Switch) findViewById(R.id.pay2goSwitch);
        quarterSwitch = (Switch) findViewById(R.id.quarterYearSwitch);
        halfSwitch = (Switch) findViewById(R.id.halfYearSwitch);
        yearlySwitch = (Switch) findViewById(R.id.annualSwitch);
        continueButton = (Button) findViewById(R.id.continueButton);
        pay2goLayout = (LinearLayout) findViewById(R.id.pay2goLayout);
        quarterLayout = (LinearLayout) findViewById(R.id.quarterYearLayout);
        halfLayout = (LinearLayout) findViewById(R.id.halfYearLayout);
        yearlyLayout = (LinearLayout) findViewById(R.id.annualLayout);
        activity = this;
        UID = "";
        changeMembership = false;


        //onclick listener for pay2goLayout
        pay2goLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay2goSwitch.setChecked(true);
            }
        });

        //onclick listener for quarterLayout
        quarterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quarterSwitch.setChecked(true);
            }
        });

        //onclick listener for halfLayout
        halfLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                halfSwitch.setChecked(true);
            }
        });

        //onclick listener for yearlyLayout
        yearlyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yearlySwitch.setChecked(true);
            }
        });

        //listener for pay2goSwitch
        pay2goSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //if pay2goSwitch is checked
                if (isChecked)
                {
                    //set membership type
                    membershipType = "pay2go";

                    //uncheck other switches
                    quarterSwitch.setChecked(false);
                    halfSwitch.setChecked(false);
                    yearlySwitch.setChecked(false);
                }
                else
                {
                    if (membershipType.equalsIgnoreCase("pay2go"))
                    {
                        membershipType="";
                    }
                }
            }
        });

        //listener for quarterSwitch
        quarterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //if quarterSwitch is checked
                if (isChecked)
                {
                    //set membership type
                    membershipType = "quarter";

                    //uncheck other switches
                    pay2goSwitch.setChecked(false);
                    halfSwitch.setChecked(false);
                    yearlySwitch.setChecked(false);
                }
                else
                {
                    if (membershipType.equalsIgnoreCase("quarter"))
                    {
                        membershipType="";
                    }
                }
            }
        });

        //listener for halfSwitch
        halfSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //if halfSwitch is checked
                if (isChecked)
                {
                    //set membership type
                    membershipType = "half";

                    //uncheck other switches
                    pay2goSwitch.setChecked(false);
                    quarterSwitch.setChecked(false);
                    yearlySwitch.setChecked(false);
                }
                else
                {
                    if (membershipType.equalsIgnoreCase("half"))
                    {
                        membershipType = "";
                    }
                }
            }
        });

        //listener for yearlySwitch
        yearlySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //if yearlySwitch is checked
                if (isChecked)
                {
                    //set membership type
                    membershipType = "yearly";

                    //uncheck other switches
                    pay2goSwitch.setChecked(false);
                    quarterSwitch.setChecked(false);
                    halfSwitch.setChecked(false);
                }
                else
                {
                    if (membershipType.equalsIgnoreCase("yearly"))
                    {
                        membershipType="";
                    }
                }
            }
        });

        //on click listener for continue button
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //membership type validation
                if (membershipType.equalsIgnoreCase(""))
                {
                    //show error toast
                    Toast.makeText(getApplicationContext(),"Please choose membership type",Toast.LENGTH_LONG).show();
                }
                else
                {
                    //if pay2go
                    if (membershipType.equalsIgnoreCase("pay2go"))
                    {
                        //if from change membership
                        if (changeMembership)
                        {
                            //go to ChangeMembership page
                            finish();
                            startActivity(new Intent(SelectMembershipActivity.this,ChangeMembershipActivity.class));
                        }
                        else
                        {
                            //go to login page, sign up successful
                            Toast.makeText(getApplicationContext(), "Membership updated", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(SelectMembershipActivity.this, LoginActivity.class));
                        }
                    }
                    else
                    {
                        //continue to Payment method activity
                        Intent newIntent = new Intent(SelectMembershipActivity.this,PaymentMethodActivity.class);
                        newIntent.putExtra("membershipType",membershipType);    //send membership type chosen
                        newIntent.putExtra("UID",UID);                          //send UID

                        finish();
                        startActivity(newIntent);
                        //Toast.makeText(getApplicationContext(),"continue to payment",Toast.LENGTH_LONG).show();// to debug
                    }
                }
            }
        });

        //check for intent extras
        if (getIntent().hasExtra("membershipType"))
        {
            membershipType = getIntent().getExtras().getString("membershipType");
            //Toast.makeText(getApplicationContext(),membershipType+"",Toast.LENGTH_SHORT).show();//to debug display membership type
        }
        if (getIntent().hasExtra("UID"))
        {
            UID = getIntent().getExtras().getString("UID");
        }
        if (getIntent().hasExtra("changeMembership"))
        {
            changeMembership = getIntent().getExtras().getBoolean("changeMembership");
        }


        //load membershipType
        if (membershipType.equalsIgnoreCase("quarter"))
        {
            quarterSwitch.setChecked(true);
        }
        else if (membershipType.equalsIgnoreCase("half"))
        {
            halfSwitch.setChecked(true);
        }
        else if (membershipType.equalsIgnoreCase("yearly"))
        {
            yearlySwitch.setChecked(true);
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

        //if from change membership
        if (changeMembership)
        {
            //go back to ChangeMembership page
            finish();
            startActivity(new Intent(SelectMembershipActivity.this,ChangeMembershipActivity.class));
        }
        else
        {
            //go back to login activity
            //select membership doesn't needed to be done now
            //membership default is pay2go
            Intent newIntent = new Intent(SelectMembershipActivity.this, LoginActivity.class);
            finish();
            startActivity(newIntent);
            Toast.makeText(getApplicationContext(), "Membership updated", Toast.LENGTH_SHORT).show();
        }
    }
}
