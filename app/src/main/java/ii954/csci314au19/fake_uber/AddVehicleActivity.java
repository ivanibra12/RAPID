package ii954.csci314au19.fake_uber;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddVehicleActivity extends AppCompatActivity {

    //variable declaration
    private TextInputLayout brandInput;
    private TextInputLayout modelInput;
    private Spinner yearSpinner;
    private Spinner typeSpinner;
    private TextInputLayout regNumInput;
    private TextInputLayout insuranceNumInput;
    private TextInputLayout nicknameInput;
    private Button addVehicleButton;
    private ProgressDialog progressDialog;
    private Calendar calendar;

    //firebase database reference variable declaration
    private DatabaseReference vehicleDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        //enable action bar (display back button on action bar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        //firebase database reference variable initialization
        vehicleDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.vehicleDatabaseName).toString());

        //variable initialization
        brandInput = (TextInputLayout) findViewById(R.id.text_input_vBrand);
        modelInput = (TextInputLayout) findViewById(R.id.text_input_vModel);
        yearSpinner = (Spinner) findViewById(R.id.spinner_vModelYear);
        typeSpinner = (Spinner) findViewById(R.id.spinner_vType);
        regNumInput = (TextInputLayout) findViewById(R.id.text_vRegistrationNumber);
        insuranceNumInput = (TextInputLayout) findViewById(R.id.text_vInsuranceNumber);
        nicknameInput = (TextInputLayout) findViewById(R.id.text_vNickname);
        addVehicleButton = (Button) findViewById(R.id.buttonAddVehicle);
        progressDialog = new ProgressDialog(this);

        //get current year and create an array of 20 year until current year
        String yearArray[] = new String[100];
        calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        //Toast.makeText(getApplicationContext(),currentYear+"",Toast.LENGTH_SHORT).show(); //to debug to show current year

        for (int i=0;i<yearArray.length;i++)
        {
            yearArray[i] = (currentYear-i)+"";
        }

        //set array adapter for spinners
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,yearArray);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,R.array.vehicleTypeArray, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);

        //set listener for button
        addVehicleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //call addVehicle function
                addVehicle();
            }
        });


    }

    /**********************************************************************************************/
    //add vehicle function
    private void addVehicle()
    {
        //get inputs
        String brand = brandInput.getEditText().getText().toString().trim();
        String model = modelInput.getEditText().getText().toString().trim();
        String regNum = regNumInput.getEditText().getText().toString().trim();
        String insuranceNum = insuranceNumInput.getEditText().getText().toString().trim();
        String nickname = nicknameInput.getEditText().getText().toString().trim();
        String year = yearSpinner.getSelectedItem().toString();
        String type = typeSpinner.getSelectedItem().toString();

        //empty validation
        if (TextUtils.isEmpty(brand))               //if brand is empty
        {
            Toast.makeText(getApplicationContext(),"Please enter vehicle brand",Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(model))          //if model is empty
        {
            Toast.makeText(getApplicationContext(),"Please enter vehicle model",Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(regNum))         //if registration number is empty
        {
            Toast.makeText(getApplicationContext(),"Please enter vehicle registration number",Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(insuranceNum))   //if insurance number is empty
        {
            Toast.makeText(getApplicationContext(),"Please enter vehicle insurance number",Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(nickname))       //if nickname is empty
        {
            Toast.makeText(getApplicationContext(),"Please enter vehicle nickname",Toast.LENGTH_SHORT).show();
            return;
        }

        //save vehicle details in vehicle database
        progressDialog.setMessage("Adding Vehicle...");         //display progress dialog
        progressDialog.setCancelable(false);
        progressDialog.show();

        Car newCar = new Car(nickname,brand,model,year,type,regNum,insuranceNum);                                   //create new vehicle

        //exist validation
        boolean exist = false;
        for (int i=0;i<UserMainActivity.userCars.size();i++)
        {
            //if registration number exist
            if (newCar.getRegNum().equalsIgnoreCase(UserMainActivity.userCars.get(i).getRegNum()))
            {
                exist = true;
                break;
            }
            //if insurance number exist
            else if (newCar.getInsuranceNum().equalsIgnoreCase(UserMainActivity.userCars.get(i).getInsuranceNum()))
            {
                exist = true;
                break;
            }
        }

        if (exist)      //if car registration number exist
        {
            Toast.makeText(getApplicationContext(),"Similar vehicle has been added",Toast.LENGTH_LONG).show();
        }
        else
        {
            vehicleDatabase.child(UserMainActivity.loggedInUser.getUID()).child(newCar.getRegNum()).setValue(newCar);   //save vehicle details

            vehicleDatabase = vehicleDatabase.child(UserMainActivity.loggedInUser.getUID()).child("totalVehicle");
            int currentTotalVehicle = UserMainActivity.userCars.size();     //retrieve current total vehicle
            currentTotalVehicle++;                                          //add totalVehicle by 1
            vehicleDatabase.setValue(currentTotalVehicle);                  //save totalVehicle in database

            Toast.makeText(getApplicationContext(),"Vehicle successfully added",Toast.LENGTH_LONG).show();

            //go back to my vehicle page
            finish();
            startActivity(new Intent(AddVehicleActivity.this,MyVehicleActivity.class));
        }

        progressDialog.cancel();                                //cancel progress dialog
    }



    /**********************************************************************************************/
    //override listener method for back button on action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //if id = back button
        if (item.getItemId()==android.R.id.home)
        {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**********************************************************************************************/
    @Override
    public void onBackPressed() {

        //override and go back to my vehicle page
        finish();
        startActivity(new Intent(AddVehicleActivity.this,MyVehicleActivity.class));
    }

    /**********************************************************************************************/
}
