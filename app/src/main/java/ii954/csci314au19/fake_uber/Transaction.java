package ii954.csci314au19.fake_uber;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Transaction
{
    //variable declaration
    private String transactionID;
    private Date transactionDateTime;   //date and time
    private String UID;
    private String paymentDescription;
    private double amount;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;

    /**********************************************************************************************/
    //constructors
    public Transaction()
    {
        transactionID = "";
        transactionDateTime=null;
        UID="";
        paymentDescription="";
        amount=0.0;
    }

    public Transaction(String transactionID, String UID, String paymentDescription, double amount)
    {
        this.transactionID = transactionID;
        this.transactionDateTime = getTodayDate();
        this.UID = UID;
        this.paymentDescription = paymentDescription;
        this.amount = amount;
    }
    /**********************************************************************************************/
    //other methods
    public Date getTodayDate()
    {
        calendar = Calendar.getInstance();

        return calendar.getTime();
    }

    public String getTransactionDetails()
    {
        String details = "";
        details+="Transaction ID : "+transactionID+"\n";
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        details+="Date : "+simpleDateFormat.format(transactionDateTime)+"\n";
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        details+="Time : "+simpleDateFormat.format(transactionDateTime)+"\n";
        details+="User : "+UID+"\n";
        details+="Amount : A$ "+amount+"\n";
        details+="Payment Description : "+paymentDescription+"\n";

        return details;
    }

    /**********************************************************************************************/
    //set methods
    public void setTransactionID(String transactionID)
    {
        this.transactionID = transactionID;
    }

    public void setTransactionDateTime(Date transactionDateTime)
    {
        this.transactionDateTime = transactionDateTime;
    }

    public void setUID(String UID)
    {
        this.UID = UID;
    }

    public void setPaymentDescription(String paymentDescription)
    {
        this.paymentDescription = paymentDescription;
    }

    public void setAmount(double amount)
    {
        this.amount = amount;
    }

    //get methods

    public String getTransactionID()
    {
        return transactionID;
    }

    public Date getTransactionDateTime()
    {
        return transactionDateTime;
    }

    public String getUID()
    {
        return UID;
    }

    public String getPaymentDescription()
    {
        return paymentDescription;
    }

    public double getAmount()
    {
        return amount;
    }
}
