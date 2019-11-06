package ii954.csci314au19.fake_uber;

public class Car {

    private String nickname;
    private String brand;
    private String model;
    private String year;
    private String type;//SUV, SEDAN, EV, VAN, MINIVAN, 4X4, SPORT CAR, COUPE, HATCHBACK
    private String regNum;
    private String insuranceNum;

    /**********************************************************************************************/
    //default constructor
    public Car()
    {
        nickname = "";
        brand = "";
        model = "";
        year = "";
        type = "";
        regNum = "";
        insuranceNum = "";
    }

    //constructor
    public Car(String nickname, String brand, String model, String year, String type, String regNum
            , String insuranceNumber)
    {
        this.nickname = nickname;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.type = type;
        this.regNum = regNum;
        this.insuranceNum = insuranceNumber;
    }

    /**********************************************************************************************/
    //set methods
    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    public void setBrand(String brand)
    {
        this.brand = brand;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public void setYear(String year)
    {
        this.year = year;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public void setRegNum(String regNum)
    {
        this.regNum = regNum;
    }

    public void setInsuranceNum(String insuranceNumber)
    {
        this.insuranceNum = insuranceNumber;
    }

    //get methods
    public String getNickname()
    {
        return nickname;
    }

    public String getBrand()
    {
        return brand;
    }

    public String getModel()
    {
        return model;
    }

    public String getYear()
    {
        return year;
    }

    public String getType()
    {
        return type;
    }

    public String getRegNum()
    {
        return regNum;
    }

    public String getInsuranceNum()
    {
        return insuranceNum;
    }
}
