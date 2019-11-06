package ii954.csci314au19.fake_uber;

public class User {

    //variable declaration
    private String email;
    private String firstName;
    private String lastName;
    private String mobileNo;
    private String uID;
    private double rating;
    private int totalRating;

    /**********************************************************************************************/
    //default constructor
    public User()
    {
        email="";
        firstName="";
        lastName="";
        mobileNo="";
        uID="";
        rating = 5;
        totalRating = 1;
    }

    //non_default constructor
    public User(String email, String firstName, String lastName, String mobileNo, double rating, int totalRating)
    {
        this.email=email;
        this.firstName=firstName;
        this.lastName=lastName;
        this.mobileNo=mobileNo;
        this.rating = rating;
        this.totalRating = totalRating;
        this.uID = generateUID();
    }

    public User (String email, String firstName, String lastName, String mobileNo, String uID, double rating, int totalRating)
    {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNo = mobileNo;
        this.uID = uID;
        this.rating = rating;
        this.totalRating = totalRating;
    }


    /**********************************************************************************************/
    //set methods
    public void setEmail(String email)
    {
        this.email = email;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public void setMobileNo(String mobileNo)
    {
        this.mobileNo = mobileNo;
    }

    public void setUID(String uID)
    {
        this.uID = uID;
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
    public String getEmail()
    {
        return email;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public String getMobileNo()
    {
        return mobileNo;
    }

    public String getUID() { return uID; }

    public double getRating()
    {
        return rating;
    }

    public int getTotalRating()
    {
        return totalRating;
    }

    //other methods
    public String generateUID()
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
            id=id+"u";                                                          //start with letter 'u'
            id=id+ firstName.toLowerCase().substring(0,2).toLowerCase();        //first 2 letter of firstName
            id=id+ mobileNo.substring(mobileNo.length()-3,mobileNo.length());   //last 3 letter of mobileNo
            id=id+ lastName.toLowerCase().substring(0,2).toLowerCase();         //first 2 letter of lastName
        }

        return id;
    }
}
