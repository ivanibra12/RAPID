package ii954.csci314au19.fake_uber;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Response
{
    //variable declaration
    private String responseid;
    private Professional professional;
    private String distance;
    private Date responseDateTime;
    private Request request;
    private String status;//waiting or accepted or declined
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;

    /**********************************************************************************************/
    //constructors
    public Response()
    {
        calendar = Calendar.getInstance();

        responseid = "";
        professional = null;
        distance = "";
        responseDateTime = null;
        request = null;
        status = "";
    }

    public Response(String responseid, Professional professional, String distance, Date responseDateTime, Request request, String status)
    {
        calendar = Calendar.getInstance();

        this.responseid = responseid;
        this.professional = professional;
        this.distance = distance;
        this.responseDateTime = responseDateTime;
        this.request = request;
        this.status = status;
    }

    public Response(Professional professional, String distance, Request request)
    {
        calendar = Calendar.getInstance();

        this.professional = professional;
        this.distance = distance;
        this.request = request;

        responseDateTime = generateTodayDate();
        responseid = generateResponseid();
        status = "waiting";
    }

    /**********************************************************************************************/
    //other methods

    private String generateResponseid() //generate responseid method
    {
        //ex repp000ppddMMyyyyrzz000zz31129999regnum235959HHmmss
        String id = "";

        id+="re";                                                   //  starts with re
        id+=professional.getpID();                                  //+ professional id

        simpleDateFormat = new SimpleDateFormat("dd");
        id+=simpleDateFormat.format(responseDateTime);              //+ response day
        simpleDateFormat = new SimpleDateFormat("MM");
        id+=simpleDateFormat.format(responseDateTime);              //+ response month
        simpleDateFormat = new SimpleDateFormat("yyyy");
        id+=simpleDateFormat.format(responseDateTime);              //+ response year

        id+=request.getRequestID();                                 //+ request id

        simpleDateFormat = new SimpleDateFormat("HH");
        id+=simpleDateFormat.format(responseDateTime);              //+ response hour
        simpleDateFormat = new SimpleDateFormat("mm");
        id+=simpleDateFormat.format(responseDateTime);              //+ response minute
        simpleDateFormat = new SimpleDateFormat("ss");
        id+=simpleDateFormat.format(responseDateTime);              //+ response second

        return id;
    }

    private Date generateTodayDate()    //generate today date
    {
        Date today = calendar.getTime();

        return today;
    }

    public String getResponseStringDate()    //return response date in string
    {
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(responseDateTime);
    }

    public String getResponseStringTime()    //return response time in string
    {
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        return simpleDateFormat.format(responseDateTime);
    }

    public String getResponseDetails()
    {
        String details = "";

        details+="Response id : "+responseid+"\n";
        details+="Professional id : "+professional.getpID()+"\n";
        details+="Distance : "+distance+"\n";
        details+="Response date : "+getResponseStringDate()+"\n";
        details+="Response time : "+getResponseStringTime()+"\n";
        details+="Request id : "+request.getRequestID()+"\n";
        details+="Status : "+status+"\n";

        return details;
    }

    /**********************************************************************************************/
    //set methods
    public void setResponseid(String responseid)
    {
        this.responseid = responseid;
    }

    public void setProfessional(Professional professional)
    {
        this.professional = professional;
    }

    public void setDistance(String distance)
    {
        this.distance = distance;
    }

    public void setResponseDateTime(Date responseDateTime)
    {
        this.responseDateTime = responseDateTime;
    }

    public void setRequest(Request request)
    {
        this.request = request;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    //get methods
    public String getResponseid()
    {
        return responseid;
    }

    public Professional getProfessional()
    {
        return professional;
    }

    public String getDistance()
    {
        return distance;
    }

    public Date getResponseDateTime()
    {
        return responseDateTime;
    }

    public Request getRequest()
    {
        return request;
    }

    public String getStatus()
    {
        return status;
    }
}
