package ii954.csci314au19.fake_uber;

public class Report
{
    //variable declaration
    private String reportID;
    private String senderID;
    private String reportedUserID;
    private String jobID;
    private String report;

    /**********************************************************************************************/
    //constructors
    public Report()
    {
        reportID = "";
        senderID = "";
        reportedUserID = "";
        jobID = "";
        report = "";
    }

    public Report(String reportID, String senderID, String reportedUserID, String jobID, String report)
    {
        this.reportID = reportID;
        this.senderID = senderID;
        this.reportedUserID = reportedUserID;
        this.jobID = jobID;
        this.report = report;
    }

    /**********************************************************************************************/
    //set methods
    public void setReportID(String reportID)
    {
        this.reportID = reportID;
    }

    public void setSenderID(String senderID)
    {
        this.senderID = senderID;
    }

    public void setReportedUserID(String reportedUserID)
    {
        this.reportedUserID = reportedUserID;
    }

    public void setJobID(String jobID)
    {
        this.jobID = jobID;
    }

    public void setReport(String report)
    {
        this.report = report;
    }

    //get methods
    public String getReportID()
    {
        return reportID;
    }

    public String getSenderID()
    {
        return senderID;
    }

    public String getReportedUserID()
    {
        return reportedUserID;
    }

    public String getJobID()
    {
        return jobID;
    }

    public String getReport()
    {
        return report;
    }
}
