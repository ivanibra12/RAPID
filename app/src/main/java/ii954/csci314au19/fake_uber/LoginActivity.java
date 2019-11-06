package ii954.csci314au19.fake_uber;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    //layout variable declaration
    private EditText emailText;
    private EditText passwordText;
    private Button loginButton;
    private TextView newAccount;

    //static variable declaration
    public static String email;


    //firebase authentication variable declaration
    public static FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    //firebase database reference variable declaration
    private DatabaseReference professionalDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //remove title bar on top of the screen only for login page
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //display activity
        setContentView(R.layout.activity_login);

        //firebase database reference variable initialization
        professionalDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.professionalDatabaseName).toString());

        //layout variable initialization
        emailText = (EditText) findViewById(R.id.emailText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        loginButton = (Button) findViewById(R.id.loginButton);
        newAccount = (TextView) findViewById(R.id.signUpTextView);
        newAccount.setPaintFlags(newAccount.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);//underline textView

        //set listener for newAccount TextView (go to user register page)
        newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to user register activity
                finish();
                startActivity(new Intent(LoginActivity.this, UserRegisterActivity.class));
                //startActivity(new Intent(LoginActivity.this,SelectMembershipActivity.class)); //to debug go to select membership activity
            }
        });

        //firebase variable initialization
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);



    }




    /**********************************************************************************************/
    //LOGIN AUTHENTICATION
    public void loginButtonClicked(View v)
    {
        //get email and password
        email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        //empty validation
        if (email.equalsIgnoreCase(""))
        {
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
        }
        else if (password.equalsIgnoreCase(""))
        {
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
        }
        else
        {
            progressDialog.setMessage("Signing In...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            //call login authentication
            loginAuth(email,password);
        }

    }

    private void loginAuth(String email, String password)
    {
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful())
                        {
                            determineAccountType();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Email or Password is incorrect"
                                    ,Toast.LENGTH_LONG).show();
                            progressDialog.cancel();
                        }
                    }
                });
    }

    /**********************************************************************************************/
    //determine account type method
    private void determineAccountType()
    {
        //check whether account is professional
        professionalDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                boolean isProfessional = false;
                //check for each professional
                for (DataSnapshot ds:dataSnapshot.getChildren())
                {
                    //if email is a match
                    if (ds.getValue(Professional.class).getEmail().equalsIgnoreCase(email))
                    {
                        isProfessional = true;
                        break;
                    }
                }

                if (isProfessional)
                {
                    finish();
                    startActivity(new Intent(LoginActivity.this, ProfessionalMainActivity.class));
                    Toast.makeText(getApplicationContext(),"Welcome !!!",Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
                else
                {
                    finish();
                    startActivity(new Intent(LoginActivity.this, UserMainActivity.class));
                    Toast.makeText(getApplicationContext(),"Welcome !!!",Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**********************************************************************************************/

    @Override
    public void onBackPressed() {
        //override back button to exit application
        finish();
        System.exit(0);
    }
}
