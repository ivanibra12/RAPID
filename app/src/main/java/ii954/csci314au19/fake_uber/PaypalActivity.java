package ii954.csci314au19.fake_uber;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

public class PaypalActivity extends AppCompatActivity implements DialogInterface.OnClickListener{

    //variable declaration
    public static final int PAYPAL_REQUEST_CODE = 6969;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PaypalConfig.PAYPAL_CLIENT_ID);

    private ProgressDialog progressDialog;
    private String amount;
    private String paymentText;
    private String paymentType;
    private AlertDialog.Builder paymentStatus;
    private String transactionID;
    private String UID;
    private String membershipType;
    private String requestID;
    private SimpleDateFormat simpleDateFormat;
    private boolean isPaymentSuccessful;

    //firebase database reference variable declaration
    private DatabaseReference membershipDatabase;
    private DatabaseReference transactionDatabase;
    private DatabaseReference userTransactionDatabase;

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal);

        //firebase database reference variable initialization
        membershipDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.membershipDatabaseName).toString());
        transactionDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.transactionDatabaseName).toString());
        userTransactionDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.userTransactionDatabaseName).toString());

        //variable initialization
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving...");
        progressDialog.setCancelable(false);
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        amount="0";
        paymentText="";
        transactionID = "";
        UID = "";
        membershipType="";      //membership if not ""
        requestID="";           //pay2go if not ""
        isPaymentSuccessful = false;
        paymentStatus = new AlertDialog.Builder(this);
        paymentStatus.setTitle("Payment Status");
        paymentStatus.setPositiveButton("OK", this);


        //get extras from intent
        if (getIntent().hasExtra("amount"))
        {
            amount = getIntent().getExtras().getString("amount");
        }
        if (getIntent().hasExtra("paymentText"))
        {
            paymentText = getIntent().getExtras().getString("paymentText");
        }
        if (getIntent().hasExtra("paymentType"))
        {
            paymentType = getIntent().getExtras().getString("paymentType");
        }
        if (getIntent().hasExtra("UID"))
        {
            UID = getIntent().getExtras().getString("UID");
        }
        if (getIntent().hasExtra("membershipType"))
        {
            membershipType = getIntent().getExtras().getString("membershipType");
        }
        if (getIntent().hasExtra("requestID"))
        {
            requestID = getIntent().getExtras().getString("requestID");
        }

        //process payment
        processPayment();
    }

    //listener for alert dialog
    @Override
    public void onClick(DialogInterface dialog, int which) {

        switch (which)
        {
            case -2: //NegativeButton (Cancel)
            {
                paymentSuccessful();
            }
            break;

            case -3: //NeutralButton (Dismiss)
            {
                paymentSuccessful();
            }
            break;

            case -1: //PositiveButton (Yes)
            {
                paymentSuccessful();
            }
            break;
        }
    }

    /**********************************************************************************************/
    public void processPayment()
    {
        this.amount = amount;
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amount)), "AUD",
                "Payment for "+paymentText, PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    /**********************************************************************************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if(requestCode == PAYPAL_REQUEST_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null)
                {
                    try
                    {
                        //get payment details in string
                        String details = getPaymentDetails(confirmation.toJSONObject().getJSONObject("response"),amount);

                        //save details in database
                        saving();

                        //display payment details
                        paymentStatus.setMessage(details);
                        paymentStatus.create().show();
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            else if (resultCode == Activity.RESULT_CANCELED)
            {
                Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }
        else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
        {
            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    /**********************************************************************************************/
    //saving details in database
    private void saving()
    {
        progressDialog.show();

        String duration = "";
        String description = "";


        if (requestID.equalsIgnoreCase(""))     //if membership payment
        {
            if (membershipType.equalsIgnoreCase("quarter")) {
                duration = "3 month";
                description += "3 month membership payment";
            } else if (membershipType.equalsIgnoreCase("half")) {
                duration = "6 month";
                description += "6 month membership payment";
            } else if (membershipType.equalsIgnoreCase("yearly")) {
                duration = "1 year";
                description += "Annual membership payment";
            }
        }
        else    //if pay2go payment
        {
            description+="Pay2Go payment ("+requestID+")";
        }

        //create new transaction
        Transaction transaction = new Transaction(transactionID,UID,description,Double.parseDouble(amount));

        //save transaction details to database
        transactionDatabase.child(transactionID).setValue(transaction);
        userTransactionDatabase.child(UID).child(transactionID).setValue(transaction);

        //if it's a membership payment, save membership details to database
        if (paymentType.equalsIgnoreCase("membership"))
        {
            //create new membership
            Membership membership = new Membership(UID, duration, transactionID);

            //save membership details to database
            membershipDatabase.child(UID).child(membership.getMID()).setValue(membership);
        }


        /*
        //to debug display transaction details
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(transaction.getTransactionDetails());
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        alert.create().show();
        */

        /*//to debug display membership details
        String message = "MID : "+membership.getMID()
                +"\nUID : "+membership.getUID()
                +"\nDuration : "+membership.getDuration()
                +"\nStartDate : "+simpleDateFormat.format(membership.getStartDate())
                +"\nEndDate : "+simpleDateFormat.format(membership.getEndDate())
                +"\nPaymentID : "+membership.getPaymentID();

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(message);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        alert.create().show();
        */

        progressDialog.cancel();
    }

    /**********************************************************************************************/
    //get payment details method
    private String getPaymentDetails(JSONObject response, String paymentAmount)
    {
        String details = "\n";

        try
        {
            transactionID = response.getString("id");
            details+="Transaction ID : " + transactionID + "\n\n";
            details+="Status    : " + response.getString("state") + "\n";
            details+="Amount : A$ " + paymentAmount;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return details;
    }

    /**********************************************************************************************/
    //payment successful
    private void paymentSuccessful()
    {

        if (paymentType.equalsIgnoreCase("membership"))
        {
            //if from change membership
            if (SelectMembershipActivity.changeMembership)
            {
                //close select membership activity
                SelectMembershipActivity.activity.finish();
                startActivity(new Intent(SelectMembershipActivity.activity.getApplicationContext(),ChangeMembershipActivity.class));
            }
            else
            {
                //close select membership activity
                SelectMembershipActivity.activity.finish();
                startActivity(new Intent(SelectMembershipActivity.activity.getApplicationContext(), LoginActivity.class));
            }
        }
        /*
        else if (!requestID.equalsIgnoreCase(""))
        {
            onBackPressed();
            Toast.makeText(getApplicationContext(),"Payment successfully made",Toast.LENGTH_SHORT).show();
        }
        else
        {
            onBackPressed();
        }*/

        //close payment method activity
        PaymentMethodActivity.activity.finish();
        Toast.makeText(getApplicationContext(),"Payment successfully made",Toast.LENGTH_SHORT).show();

        isPaymentSuccessful = true;

        onBackPressed();
    }


    /**********************************************************************************************/
    @Override
    public void onBackPressed() {

        if (!requestID.equalsIgnoreCase("")&&isPaymentSuccessful)
        {
            ConfirmHelpRequestActivity.paymentSuccessfulTextView.setText("successful");
        }

        finish();
    }
}
