package ii954.csci314au19.fake_uber;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateSecurityActivity extends AppCompatActivity {

    //variable declaration
    private TextInputLayout emailInputLayout;
    private TextInputLayout newPasswordInputLayout;
    private TextInputLayout confirmPasswordInputLayout;
    private Button updatePasswordButton;
    private String newPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_security);

        //enable action bar (display back button on action bar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //variable declaration
        emailInputLayout = (TextInputLayout) findViewById(R.id.emailInputLayout);
        newPasswordInputLayout = (TextInputLayout) findViewById(R.id.newPasswordInputLayout);
        confirmPasswordInputLayout = (TextInputLayout) findViewById(R.id.confirmPasswordInputLayout);
        updatePasswordButton = (Button) findViewById(R.id.updatePasswordButton);
        newPassword="";

        //load email inputLayout
        loadDetails();

        //listener for updatePasswordButton
        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //call update password validation method
                updatePasswordValidation();
            }
        });



    }

    /**********************************************************************************************/
    //update password method
    private void updatePasswordValidation()
    {
        //get inputs
        newPassword = newPasswordInputLayout.getEditText().getText().toString().trim();
        String confirmPassword = confirmPasswordInputLayout.getEditText().getText().toString().trim();

        //validation
        if (newPassword.equalsIgnoreCase(""))   //if new password is empty
        {
            Toast.makeText(getApplicationContext(),"Please enter new password",Toast.LENGTH_SHORT).show();
            return;
        }
        else if (confirmPassword.equalsIgnoreCase(""))  //if confirm password is empty
        {
            Toast.makeText(getApplicationContext(),"Please enter confirm password",Toast.LENGTH_SHORT).show();
            return;
        }
        else if (newPassword.length()<8)        //if new password length less than 8
        {
            Toast.makeText(getApplicationContext(),"New password must have at least 8 characters",Toast.LENGTH_SHORT).show();
            return;
        }
        else if (!newPassword.equalsIgnoreCase(confirmPassword))    //if new password is not equal to confirm password
        {
            Toast.makeText(getApplicationContext(),"New password doesn't match with confirm password",Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            AlertDialog.Builder confirmationAlert = new AlertDialog.Builder(this);
            confirmationAlert.setMessage("Confirm changes?");
            confirmationAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });
            confirmationAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //call update password function
                    updatePassword(newPassword);
                }
            });
            confirmationAlert.create().show();
        }

    }

    private void updatePassword(String newPassword)
    {
        final ProgressDialog updatingDialog = new ProgressDialog(this);
        updatingDialog.setMessage("Updating password...");
        updatingDialog.setCancelable(false);
        updatingDialog.show();

        FirebaseUser user = LoginActivity.firebaseAuth.getCurrentUser();

        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful())
                {
                    //display toast and go back
                    Toast.makeText(getApplicationContext(),"Password updated",Toast.LENGTH_LONG).show();
                    onBackPressed();
                }
                else
                {
                    //display error toast
                    Toast.makeText(getApplicationContext(),"Failed updating password, try again later",Toast.LENGTH_LONG).show();
                }
                updatingDialog.cancel();
            }
        });
    }

    /**********************************************************************************************/
    //load details
    private void loadDetails()
    {
        emailInputLayout.getEditText().setText(UserMainActivity.loggedInUser.getEmail());
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

        //override and go to settings activity
        finish();
        startActivity(new Intent(UpdateSecurityActivity.this,SettingsActivity.class));
    }
    /**********************************************************************************************/
}
