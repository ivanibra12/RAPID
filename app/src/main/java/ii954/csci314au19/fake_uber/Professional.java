package ii954.csci314au19.fake_uber;

public class Professional
{
    //variable declaration
    private String email;
    private String firstName;
    private String lastName;
    private String mobileNo;
    private String pID;
    private String licenseFrontLink;
    private String licenseBackLink;
    private double rating;
    private int totalRating;
    private boolean verified;

    /**********************************************************************************************/
    //default constructor
    public Professional()
    {
        email = "";
        firstName = "";
        lastName = "";
        mobileNo ="";
        pID = "";
        licenseFrontLink ="";
        licenseBackLink ="";
        rating = 5;
        totalRating = 1;
        verified = false;
    }

    //non_default constructor
    public Professional (String email, String firstName, String lastName, String mobileNo
            , String licenseFrontLink, String licenseBackLink, double rating, int totalRating)
    {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNo = mobileNo;
        this.pID = generatePID();
        this.licenseFrontLink = licenseFrontLink;
        this.licenseBackLink = licenseBackLink;
        this.rating = rating;
        this.totalRating = totalRating;
        this.verified = false;
    }

    public Professional (String email, String firstName, String lastName, String mobileNo)
    {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNo = mobileNo;
        this.pID = generatePID();
        this.licenseFrontLink = "";
        this.licenseBackLink = "";
        this.rating = 5;
        this.totalRating = 1;
        this.verified = false;
    }

    public Professional (String email, String firstName, String lastName, String mobileNo
            , String pID, String licenseFrontLink, String licenseBackLink, double rating, int totalRating, boolean verified)
    {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNo = mobileNo;
        this.pID = pID;
        this.licenseFrontLink = licenseFrontLink;
        this.licenseBackLink = licenseBackLink;
        this.rating = rating;
        this.totalRating = totalRating;
        this.verified = verified;
    }

    /**********************************************************************************************/
    //set methods
    public void setEmail (String email)
    {
        this.email = email;
    }

    public void setFirstName (String firstName)
    {
        this.firstName = firstName;
    }

    public void setLastName (String lastName)
    {
        this.lastName = lastName;
    }

    public void setMobileNo (String mobileNo)
    {
        this.mobileNo = mobileNo;
    }

    public void setpID (String pID)
    {
        this.pID = pID;
    }

    public void setLicenseFrontLink (String licenseFrontLink)
    {
        this.licenseFrontLink = licenseFrontLink;
    }

    public void setLicenseBackLink (String licenseBackLink)
    {
        this.licenseBackLink = licenseBackLink;
    }

    public void setVerified (boolean verified)
    {
        this.verified = verified;
    }

    public void setRating(double rating)
    {
        this.rating = rating;
    }

    public void setTotalRating(int totalRating)
    {
        this.totalRating = totalRating;
    }

    //get methods
    public String getEmail ()
    {
        return email;
    }

    public String getFirstName ()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public String getMobileNo ()
    {
        return mobileNo;
    }

    public String getpID ()
    {
        return pID;
    }

    public String getLicenseFrontLink()
    {
        return licenseFrontLink;
    }

    public  String getLicenseBackLink()
    {
        return licenseBackLink;
    }

    public boolean isVerified()
    {
        return verified;
    }

    public double getRating()
    {
        return rating;
    }

    public int getTotalRating()
    {
        return totalRating;
    }

    //other methods
    public String generatePID()
    {
        String id="";

        //check if firstname, lastname, and mobileNo is empty
        if (firstName.equalsIgnoreCase("")
                &&lastName.equalsIgnoreCase("")
                &&mobileNo.equalsIgnoreCase(""))
        {
            return id;
        }
        else
        {
            id=id+"p";                                                          //start with letter 'p'
            id=id+ firstName.substring(0,2).toLowerCase();                      //first 2 letter of firstName
            id=id+ mobileNo.substring(mobileNo.length()-3,mobileNo.length());   //last 3 letter of mobileNo
            id=id+ lastName.substring(0,2).toLowerCase();                       //first 2 letter of lastName
        }

        return id;
    }

    /**********************************************************************************************/

}
