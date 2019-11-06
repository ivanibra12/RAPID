package ii954.csci314au19.fake_uber;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PaymentMethodActivity extends AppCompatActivity {

    //variable declaration
    private String UID;
    private Button paypalButton;
    public static Activity activity;

    //variable declaration for membership payment
    private String membershipType;

    //variable declaration for pay2go
    private String amount;
    private String paymentText;
    private String requestID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        //enable action bar (display back button on action bar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //variable initialization
        paypalButton = (Button) findViewById(R.id.paypalButton);
        activity = this;

        //variable initialization for membership payment
        membershipType = "";

        //variable declaration for pay2go
        amount="";
        paymentText="";
        requestID="";

        //check for intent extras
        if (getIntent().hasExtra("UID"))
        {
            UID = getIntent().getExtras().getString("UID");
        }

        //check for membership intent extras
        if (getIntent().hasExtra("membershipType"))
        {
            membershipType = getIntent().getExtras().getString("membershipType");
            //Toast.makeText(getApplicationContext(),membershipType+"",Toast.LENGTH_SHORT).show();//to debug display membership type
        }

        //check for pay2go intent extras
        if (getIntent().hasExtra("amount"))
        {
            amount = getIntent().getExtras().getString("amount");
        }
        if (getIntent().hasExtra("paymentText"))
        {
            paymentText = getIntent().getExtras().getString("paymentText");
        }
        if (getIntent().hasExtra("requestID"))
        {
            requestID = getIntent().getExtras().getString("requestID");
        }


        paypalButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                if (membershipType.equalsIgnoreCase(""))    //if pay2go
                {
                    //go to paypal payment page
                    PaypalPayment paypalPayment = new PaypalPayment(amount,paymentText,getApplicationContext(),UID,requestID);
                }
                else    //if membership payment
                {
                    //go to paypal payment page
                    PaypalPayment newPayment = new PaypalPayment(membershipType,getApplicationContext(),UID);
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
    @Override
    public void onBackPressed() {


        if (membershipType.equalsIgnoreCase(""))    //if pay2go payment
        {
            finish();
        }
        else
        {
            //go back to select membership activity
            Intent newIntent = new Intent(PaymentMethodActivity.this, SelectMembershipActivity.class);
            newIntent.putExtra("membershipType", membershipType);
            newIntent.putExtra("UID", UID);
            finish();
            startActivity(newIntent);
        }

    }
}
