package ii954.csci314au19.fake_uber;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.provider.Contacts;

public class PaypalPayment
{
    //variable declaration
    private String amount;
    private String membershipType;
    private String paymentText;
    private String UID;
    private String requestID;
    private Context prevContext;;

    /**********************************************************************************************/
    //constructor for membership payment
    public PaypalPayment(String membershipType, Context prevContext, String UID)
    {
        //set variables
        this.membershipType = membershipType;
        this.prevContext = prevContext;
        this.UID = UID;

        //load extra variables
        loadPaymentText();
        loadPaymentAmount();

        //load activity
        loadActivity("membership");
    }

    //constructor for pay2go payment / other possible payment
    public PaypalPayment(String amount,String paymentText,Context prevContext, String UID, String requestID)
    {
        //set variables
        this.amount = amount;
        this.paymentText = paymentText;
        this.prevContext = prevContext;
        this.UID = UID;
        this.requestID = requestID;

        //load activity
        loadActivity("pay2go");
    }
    /**********************************************************************************************/
    //load activity
    private void loadActivity(String paymentType)
    {
        //go to paypal activity
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(PaypalPayment.this.prevContext,PaypalActivity.class);
        intent.putExtra("amount",amount);           //send amount
        intent.putExtra("paymentText",paymentText); //send payment details
        intent.putExtra("paymentType",paymentType); //send payment type //membership or pay2go
        intent.putExtra("UID",UID);                 //send UID (who make the payment)
        //if membership payment
        if (paymentType.equalsIgnoreCase("membership"))
        {
            intent.putExtra("membershipType", membershipType);   //send membership type //quarter or half or yearly;
        }
        else
        {
            intent.putExtra("requestID",requestID);             //send request ID
        }
        PaypalPayment.this.prevContext.startActivity(intent);
    }

    /**********************************************************************************************/
    //loadPaymentText method
    private void loadPaymentText()
    {
        if (membershipType.equalsIgnoreCase("quarter"))
        {
            paymentText = "3 month membership";
        }
        else if (membershipType.equalsIgnoreCase("half"))
        {
            paymentText = "6 month membership";
        }
        else if (membershipType.equalsIgnoreCase("yearly"))
        {
            paymentText = "annual membership";
        }
    }

    //loadPaymentAmount method
    private void loadPaymentAmount()
    {
        if (membershipType.equalsIgnoreCase("quarter"))
        {
            amount = "30";
        }
        else if (membershipType.equalsIgnoreCase("half"))
        {
            amount = "50";
        }
        else if (membershipType.equalsIgnoreCase("yearly"))
        {
            amount = "80";
        }
    }







}
