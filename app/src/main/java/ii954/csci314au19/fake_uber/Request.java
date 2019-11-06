package ii954.csci314au19.fake_uber;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Request
{
    //variable declaration
    private String requestID;
    private User user;
    private Car vehicle;
    private String ipAddress;
    private String locationLongitude;
    private String locationLatitude;
    private Date requestDateTime;
    private String problem;
    private String additionalNote;
    private String status;//ongoing or cancelled or solved or unsolved
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;

    /**********************************************************************************************/
    //constructors
    public Request()
    {
        calendar = Calendar.getInstance();

        requestID = "";
        user = null;
        vehicle = null;
        ipAddress = "";
        locationLatitude = "";
        locationLongitude = "";
        requestDateTime = null;
        problem = "";
        additionalNote = "";
        status = "";
    }

    public Request(String requestID, User user, Car vehicle, String ipAddress
            , String locationLongitude, String locationLatitude
            , Date requestDateTime, String problem, String additionalNote, String status)
    {
        calendar = Calendar.getInstance();

        this.requestID = requestID;
        this.user = user;
        this.vehicle = vehicle;
        this.ipAddress = ipAddress;
        this.locationLongitude = locationLongitude;
        this.locationLatitude = locationLatitude;
        this.requestDateTime = requestDateTime;
        this.problem = problem;
        this.additionalNote = additionalNote;
        this.status = status;
    }

    public Request(User user, Car vehicle, String ipAddress
            , String locationLongitude, String locationLatitude
            , String problem, String additionalNote)
    {
        calendar = Calendar.getInstance();

        this.user = user;
        this.vehicle = vehicle;
        this.ipAddress = ipAddress;
        this.locationLongitude = locationLongitude;
        this.locationLatitude = locationLatitude;
        this.problem = problem;
        this.additionalNote = additionalNote;

        this.requestDateTime = generateTodayDate();
        this.requestID = generateRequestID();
        status = "ongoing";
    }

    /**********************************************************************************************/
    //other methods
    private String generateRequestID()  //generate request id
    {
        //ex rzz000zz31129999regnum235959
        String id = "";

        id+="r";                                                    //starts with r
        id+=user.getUID();                                          //+ user id

        simpleDateFormat = new SimpleDateFormat("dd");
        id+=simpleDateFormat.format(requestDateTime);               //+ request day
        simpleDateFormat = new SimpleDateFormat("MM");
        id+=simpleDateFormat.format(requestDateTime);               //+ request month
        simpleDateFormat = new SimpleDateFormat("yyyy");
        id+=simpleDateFormat.format(requestDateTime);               //+ request year

        id+=vehicle.getRegNum();                                    //+ vehicle regnum

        simpleDateFormat = new SimpleDateFormat("HH");
        id+=simpleDateFormat.format(requestDateTime);               //+ request hour
        simpleDateFormat = new SimpleDateFormat("mm");
        id+=simpleDateFormat.format(requestDateTime);               //+ request minute
        simpleDateFormat = new SimpleDateFormat("ss");
        id+=simpleDateFormat.format(requestDateTime);               //+ request second

        return id;
    }

    private Date generateTodayDate()    //generate today date
    {
        Date today = calendar.getTime();

        return today;
    }

    public String getRequestStringDate()    //return request date in string
    {
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(requestDateTime);
    }

    public String getRequestStringTime()    //return request time in string
    {
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        return simpleDateFormat.format(requestDateTime);
    }

    public String getRequestDetails()   //return request details in string
    {
        String details="";

        details+="Request ID : "+requestID+"\n";
        details+="User ID : "+user.getUID()+"\n";
        details+="Vehicle regnum : "+vehicle.getRegNum()+"\n";
        details+="User IP address : "+ipAddress+"\n";
        details+="Longitude : "+locationLongitude+"\n";
        details+="Latitude : "+locationLatitude+"\n";
        details+="Request date : "+getRequestStringDate()+"\n";
        details+="Request time : "+getRequestStringTime()+"\n";
        details+="Problem : "+problem+"\n";
        details+="Additional note : "+additionalNote+"\n";
        details+="Status : "+status;

        return details;
    }

    /**********************************************************************************************/
    //set methods
    public void setRequestID(String requestID)
    {
        this.requestID = requestID;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public void setVehicle(Car vehicle)
    {
        this.vehicle = vehicle;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public void setLocationLongitude(String locationLongitude)
    {
        this.locationLongitude = locationLongitude;
    }

    public void setLocationLatitude(String locationLatitude)
    {
        this.locationLatitude = locationLatitude;
    }

    public void setRequestDateTime(Date requestDateTime)
    {
        this.requestDateTime = requestDateTime;
    }

    public void setProblem(String problem)
    {
        this.problem = problem;
    }

    public void setAdditionalNote(String additionalNote)
    {
        this.additionalNote = additionalNote;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    //get methods
    public String getRequestID()
    {
        return requestID;
    }

    public User getUser()
    {
        return user;
    }

    public Car getVehicle()
    {
        return vehicle;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public String getLocationLongitude()
    {
        return locationLongitude;
    }

    public String getLocationLatitude()
    {
        return locationLatitude;
    }

    public Date getRequestDateTime()
    {
        return requestDateTime;
    }

    public String getProblem()
    {
        return problem;
    }

    public String getAdditionalNote()
    {
        return additionalNote;
    }

    public String getStatus()
    {
        return status;
    }
}
