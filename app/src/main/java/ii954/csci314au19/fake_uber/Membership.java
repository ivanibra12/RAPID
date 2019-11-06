package ii954.csci314au19.fake_uber;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Membership
{
    //variable declaration
    private String MID;         //membershipID
    private String UID;
    private String duration;    //3 month, 6 month, 1 year
    private Date startDate;
    private Date endDate;
    private String transactionID;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;

    /**********************************************************************************************/
    //constructors
    public Membership()
    {
        MID = "";
        UID = "";
        duration = "";
        startDate = null;
        endDate = null;
        transactionID = "";
    }

    public Membership(String MID, String UID, String duration, Date startDate, Date endDate, String transactionID)
    {
        this.MID = MID;
        this.UID = UID;
        this.duration = duration;
        this.startDate = startDate;
        this.endDate = endDate;
        this.transactionID = transactionID;
    }

    public Membership(String UID, String duration, String transactionID)
    {
        this.UID = UID;
        this.duration = duration;
        this.transactionID = transactionID;

        startDate = getTodayDate();
        endDate = generateEndDate();
        MID = generateMID();
    }

    /**********************************************************************************************/
    //other methods
    public Date getTodayDate()          //get today date
    {
        calendar = Calendar.getInstance();

        return calendar.getTime();
    }

    public String getMembershipDetails()
    {
        String details="";

        details+="Membership ID : "+MID+"\n";
        details+="User ID : "+UID+"\n";
        details+="Duration : "+duration+"\n";
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        details+="Start date : "+simpleDateFormat.format(startDate)+"\n";
        details+="End date : "+simpleDateFormat.format(endDate)+"\n";
        details+="Transaction ID : "+transactionID;

        return details;
    }

    public Date generateEndDate()       //generate end date (startDate + duration)
    {
        calendar = Calendar.getInstance();

        if (startDate==null&&duration.equalsIgnoreCase(""))
        {
            return null;
        }
        else
        {
            if (duration.equalsIgnoreCase("3 month"))
            {
                calendar.add(Calendar.MONTH,3);
            }
            else if (duration.equalsIgnoreCase("6 month"))
            {
                calendar.add(Calendar.MONTH,6);
            }
            else if (duration.equalsIgnoreCase("1 year"))
            {
                calendar.add(Calendar.YEAR,1);
            }

            //return new date
            return calendar.getTime();
        }
    }

    public String generateMID()
    {
        String mid="";

        mid+="m";                                                   //start with "m"
        simpleDateFormat = new SimpleDateFormat("dd");
        mid+= simpleDateFormat.format(startDate);                   //+ day from startDate
        simpleDateFormat = new SimpleDateFormat("MM");
        mid+= simpleDateFormat.format(startDate);                   //+ month from startDate
        simpleDateFormat = new SimpleDateFormat("yyyy");
        mid+= simpleDateFormat.format(startDate);                   //+ year from startDate
        mid+=UID;                                                   //+ UID

        return mid;
    }

    /**********************************************************************************************/
    //set methods
    public void setMID(String MID)
    {
        this.MID = MID;
    }

    public void setUID(String UID)
    {
        this.UID = UID;
    }

    public void setDuration(String duration)
    {
        this.duration = duration;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    public void setTransactionID(String transactionID)
    {
        this.transactionID = transactionID;
    }

    //get methods
    public String getMID ()
    {
        return MID;
    }

    public String getUID()
    {
        return UID;
    }

    public String getDuration()
    {
        return duration;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public Date getEndDate ()
    {
        return endDate;
    }

    public String getTransactionID()
    {
        return transactionID;
    }

}
