package ii954.csci314au19.fake_uber;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Job
{
    //variable declaration
    private String jobid;
    private Request request;
    private Response response;
    private Date jobDateTime;
    private String status;  //ongoing, solved, unsolved
    private String report;

    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;

    /**********************************************************************************************/
    //constructors
    public Job()
    {
        calendar = Calendar.getInstance();

        jobid = "";
        request = null;
        response = null;
        jobDateTime = null;
        status = "";
        report = "";
    }

    public Job(String jobid, Request request, Response response, Date jobDateTime, String status, String report)
    {
        calendar = Calendar.getInstance();

        this.jobid = jobid;
        this.request = request;
        this.response = response;
        this.jobDateTime = jobDateTime;
        this.status = status;
        this.report = report;
    }

    public Job(Request request, Response response, String report)
    {
        calendar = Calendar.getInstance();

        this.request = request;
        this.response = response;
        this.report = report;

        this.jobDateTime = generateTodayDate();
        this.jobid = generateJobId();
        this.status = "ongoing";
    }

    /**********************************************************************************************/
    //other methods

    private String generateJobId()
    {
        //example jxx000xxddmmyyxx000xxhhmmss
        String id = "";

        id+="j";                                                //  starts with j
        id+=request.getUser().getUID();                         //+ request user id

        simpleDateFormat = new SimpleDateFormat("dd");
        id+=simpleDateFormat.format(jobDateTime);               //+ job day
        simpleDateFormat = new SimpleDateFormat("MM");
        id+=simpleDateFormat.format(jobDateTime);               //+ job month
        simpleDateFormat = new SimpleDateFormat("yyyy");
        id+=simpleDateFormat.format(jobDateTime);               //+ job year

        id+=response.getProfessional().getpID();                //+ response professional id

        simpleDateFormat = new SimpleDateFormat("HH");
        id+=simpleDateFormat.format(jobDateTime);               //+ job hour
        simpleDateFormat = new SimpleDateFormat("mm");
        id+=simpleDateFormat.format(jobDateTime);               //+ job minute
        simpleDateFormat = new SimpleDateFormat("ss");
        id+=simpleDateFormat.format(jobDateTime);               //+ job second

        return id;
    }

    private Date generateTodayDate()    //generate today date
    {
        Date today = calendar.getTime();

        return today;
    }

    public String getJobStringDate()    //return response date in string
    {
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(jobDateTime);
    }

    public String getJobStringTime()    //return response time in string
    {
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        return simpleDateFormat.format(jobDateTime);
    }

    public String getJobDetails()
    {
        String details = "";

        details+="Job id : "+jobid+"\n";
        details+="Request id : "+request.getRequestID()+"\n";
        details+="Response id : "+response.getResponseid()+"\n";
        details+="Job date : "+getJobStringDate()+"\n";
        details+="Job time : "+getJobStringTime()+"\n";
        details+="Status : "+status+"\n";
        details+="Report : "+report+"\n";

        return details;
    }

    /**********************************************************************************************/
    //set methods
    public void setJobid(String jobid)
    {
        this.jobid = jobid;
    }

    public void setRequest(Request request)
    {
        this.request = request;
    }

    public void setResponse(Response response)
    {
        this.response = response;
    }

    public void setJobDateTime(Date jobDateTime)
    {
        this.jobDateTime = jobDateTime;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public void setReport(String report)
    {
        this.report = report;
    }

    //get methods
    public String getJobid()
    {
        return jobid;
    }

    public Request getRequest()
    {
        return request;
    }

    public Response getResponse()
    {
        return response;
    }

    public Date getJobDateTime()
    {
        return jobDateTime;
    }

    public String getStatus()
    {
        return status;
    }

    public String getReport()
    {
        return report;
    }
}
