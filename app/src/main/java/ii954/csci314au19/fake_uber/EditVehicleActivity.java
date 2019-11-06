package ii954.csci314au19.fake_uber;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.Calendar;

public class EditVehicleActivity extends AppCompatActivity{ //implements DialogInterface.OnClickListener{

    //variable declaration
    private TextInputLayout brandInput;
    private TextInputLayout modelInput;
    private Spinner yearSpinner;
    private Spinner typeSpinner;
    private TextInputLayout regNumInput;
    private TextInputLayout insuranceNumInput;
    private TextInputLayout nicknameInput;
    private Button editVehicleButton;
    private Button deleteVehicleButton;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder editConfirmation;
    private AlertDialog.Builder deleteConfirmation;
    private Calendar calendar;
    private String yearArray[];
    private String typeArray[];
    private String brand,model,year,type,regnum,insurancenum,nickname;
    private String newBrand,newModel,newYear,newType,newRegnum,newInsurancenum,newNickname;


    //firebase database reference variable declaration
    private DatabaseReference vehicleDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_vehicle);

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
        editVehicleButton = (Button) findViewById(R.id.buttonEditVehicle);
        deleteVehicleButton = (Button) findViewById(R.id.butttonDeleteVehicle);
        progressDialog = new ProgressDialog(this);
        editConfirmation = new AlertDialog.Builder(this);
        deleteConfirmation = new AlertDialog.Builder(this);


        //load type array
        typeArray = getResources().getStringArray(R.array.vehicleTypeArray);

        //get current year and create an array of 20 year until current year
        yearArray = new String[100];
        calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        //Toast.makeText(getApplicationContext(),currentYear+"",Toast.LENGTH_SHORT).show(); //to debug to show current year

        //load year array
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

        //get values from intent extras
        if (getIntent().hasExtra("brand"))
        {
            brand = getIntent().getExtras().getString("brand");
        }
        if (getIntent().hasExtra("model"))
        {
            model = getIntent().getExtras().getString("model");
        }
        if (getIntent().hasExtra("regnum"))
        {
            regnum = getIntent().getExtras().getString("regnum");
        }
        if (getIntent().hasExtra("insurancenum"))
        {
            insurancenum = getIntent().getExtras().getString("insurancenum");
        }
        if (getIntent().hasExtra("nickname"))
        {
            nickname = getIntent().getExtras().getString("nickname");
        }
        if (getIntent().hasExtra("year"))
        {
            year = getIntent().getExtras().getString("year");
        }
        if (getIntent().hasExtra("type"))
        {
            type = getIntent().getExtras().getString("type");
        }

        //load car details
        loadCarDetails();

        //set on click listener to edit button
        editVehicleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //call edit function
                editVehicleDetails();
            }
        });

        //set on click listener to delete button
        deleteVehicleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //call delete function
                deleteVehicle();
            }
        });
    }

    /**********************************************************************************************/
    //delete vehicle function
    private void deleteVehicle()
    {
        //display alert dialog to get confirmation
        deleteConfirmation = new AlertDialog.Builder(this);
        deleteConfirmation.setMessage("Are you sure you want to delete this vehicle ?");
        deleteConfirmation.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        deleteConfirmation.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleting();
            }
        });
        deleteConfirmation.create().show();
    }

    //deleting method after receiving confirmation
    private void deleting()
    {
        //display progress dialog
        progressDialog.setMessage("Deleting...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //delete vehicle details in database
        vehicleDatabase.child(UserMainActivity.loggedInUser.getUID()).child(regnum).removeValue();

        //get current totalVehicle
        int currentTotalVehicle = UserMainActivity.userCars.size();
        //minus totalVehicle by 1
        currentTotalVehicle--;
        //save updated totalVehicle in database
        vehicleDatabase.child(UserMainActivity.loggedInUser.getUID()).child("totalVehicle").setValue(currentTotalVehicle);

        //cancel progress dialog
        progressDialog.cancel();
        Toast.makeText(this,"Vehicle Successfully Deleted !",Toast.LENGTH_LONG).show();

        //go back to my vehicle page
        finish();
        startActivity(new Intent(EditVehicleActivity.this,MyVehicleActivity.class));
    }

    /**********************************************************************************************/
    //edit vehicle details function
    private void editVehicleDetails()
    {
        //get inputs
        newBrand = brandInput.getEditText().getText().toString().trim();
        newModel = modelInput.getEditText().getText().toString().trim();
        newRegnum = regNumInput.getEditText().getText().toString().trim();
        newInsurancenum = insuranceNumInput.getEditText().getText().toString().trim();
        newNickname = nicknameInput.getEditText().getText().toString().trim();
        newYear = yearSpinner.getSelectedItem().toString();
        newType = typeSpinner.getSelectedItem().toString();

        //no changes validation
        if (brand.equals(newBrand) && model.equals(newModel) && regnum.equals(newRegnum)
                && insurancenum.equals(newInsurancenum) && nickname.equals(newNickname)
                && year.equals(newYear) && type.equals(newType))
        {
            Toast.makeText(getApplicationContext(),"There's no changes to be made",Toast.LENGTH_LONG).show();
        }
        else
        {
            //empty validation
            if (TextUtils.isEmpty(newBrand))               //if brand is empty
            {
                Toast.makeText(getApplicationContext(),"Please enter vehicle brand",Toast.LENGTH_SHORT).show();
                return;
            }
            else if (TextUtils.isEmpty(newModel))          //if model is empty
            {
                Toast.makeText(getApplicationContext(),"Please enter vehicle model",Toast.LENGTH_SHORT).show();
                return;
            }
            else if (TextUtils.isEmpty(newRegnum))         //if registration number is empty
            {
                Toast.makeText(getApplicationContext(),"Please enter vehicle registration number",Toast.LENGTH_SHORT).show();
                return;
            }
            else if (TextUtils.isEmpty(newInsurancenum))   //if insurance number is empty
            {
                Toast.makeText(getApplicationContext(),"Please enter vehicle insurance number",Toast.LENGTH_SHORT).show();
                return;
            }
            else if (TextUtils.isEmpty(newNickname))       //if nickname is empty
            {
                Toast.makeText(getApplicationContext(),"Please enter vehicle nickname",Toast.LENGTH_SHORT).show();
                return;
            }
            else
            {
                //display alert dialog to get confirmation
                editConfirmation = new AlertDialog.Builder(this);
                editConfirmation.setMessage("Confirm changes?");
                editConfirmation.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                editConfirmation.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saving();
                    }
                });
                editConfirmation.create().show();
            }
        }
    }

    //saving method after receiving confirmation
    private void saving()
    {
        //display progress dialog
        progressDialog.setMessage("Saving...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //delete old details in database
        vehicleDatabase.child(UserMainActivity.loggedInUser.getUID()).child(regnum).removeValue();

        //save new details in database
        Car newCar = new Car(newNickname,newBrand,newModel,newYear,newType,newRegnum,newInsurancenum);
        vehicleDatabase.child(UserMainActivity.loggedInUser.getUID()).child(newRegnum).setValue(newCar);

        //cancel progress dialog
        progressDialog.cancel();
        Toast.makeText(this,"Changes Successfully Saved !",Toast.LENGTH_LONG).show();

        //go back to my vehicle page
        finish();
        startActivity(new Intent(EditVehicleActivity.this,MyVehicleActivity.class));
    }


    /**********************************************************************************************/
    /*
    //listener for alert dialog
    @Override
    public void onClick(DialogInterface dialog, int which) {

        switch (which)
        {
            case -2: //NegativeButton (Cancel)
            {
                dialog.dismiss();
            }
            break;

            case -3: //NeutralButton (Dismiss)
            {
                dialog.dismiss();
            }
            break;

            case -1: //PositiveButton (Yes)
            {
                //save changes
                saving();
            }
            break;
        }
    }*/


    /**********************************************************************************************/
    //load car details
    private void loadCarDetails()
    {
        brandInput.getEditText().setText(brand);
        modelInput.getEditText().setText(model);
        regNumInput.getEditText().setText(regnum);
        insuranceNumInput.getEditText().setText(insurancenum);
        nicknameInput.getEditText().setText(nickname);

        //get year position in spinner
        int yearPos = 0;
        for (int i=0;i<yearArray.length;i++)
        {
            if (yearArray[i].equalsIgnoreCase(year))
            {
                yearPos = i;
                break;
            }
        }
        //set year spinner position
        yearSpinner.setSelection(yearPos);

        //get vehicle type position in spinner
        int typePos = 0;
        for (int i=0;i<typeArray.length;i++)
        {
            if (typeArray[i].equalsIgnoreCase(type))
            {
                typePos = i;
                break;
            }
        }
        //set type spinner position
        typeSpinner.setSelection(typePos);
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
        startActivity(new Intent(EditVehicleActivity.this,MyVehicleActivity.class));
    }

    /**********************************************************************************************/
}
