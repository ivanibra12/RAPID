package ii954.csci314au19.fake_uber;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfessionalDocSubmitActivity extends AppCompatActivity {

    //variable declaration
    private ImageView frontView;
    private ImageView backView;
    private Button submitSignupButton;
    private Uri frontImage;
    private Uri backImage;
    private String frontImageName;
    private String backImageName;
    private ProgressDialog progressDialog;
    private static String frontImageURL;
    private static String backImageURL;

    //extras variable declaration
    private String email;
    private String firstname;
    private String lastname;
    private String hpnumber;
    private String password;

    //static variable declaration
    static final int REQUEST_IMAGE_CAPTURE1 = 1;
    static final int REQUEST_IMAGE_CAPTURE2 = 2;

    //firebase storage reference variable declaration
    private StorageReference storageReference;

    //firebase authentication variable declaration
    private FirebaseAuth firebaseAuth;

    //firebase realtime database reference variable declaration
    private DatabaseReference professionalDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professional_doc_submit);

        //enable action bar (display back button on action bar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //firebase authentication variable initialization
        firebaseAuth = FirebaseAuth.getInstance();

        //firebase realtime database reference variable initialization
        professionalDatabase = FirebaseDatabase.getInstance().getReference(getText(R.string.professionalDatabaseName).toString());

        //firebase storage reference variable initialization
        storageReference = FirebaseStorage.getInstance().getReference();


        //variable initialization and set on click
        frontView = (ImageView) findViewById(R.id.imageView1);
        frontView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dispatchTakePictureFront();
            }
        });

        backView = (ImageView) findViewById(R.id.imageView2);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dispatchTakePictureBack();
            }
        });

        submitSignupButton = (Button) findViewById(R.id.submitSignupButton);
        submitSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submitSignup();
            }
        });

        frontImageName = "";
        backImageName = "";
        progressDialog = new ProgressDialog(this);


        //retrieve extras
        if (getIntent().hasExtra("email")) {
            email = getIntent().getExtras().getString("email");
        }
        if (getIntent().hasExtra("firstname")) {
            firstname = getIntent().getExtras().getString("firstname");
        }
        if (getIntent().hasExtra("lastname")) {
            lastname = getIntent().getExtras().getString("lastname");
        }
        if (getIntent().hasExtra("hpnumber")) {
            hpnumber = getIntent().getExtras().getString("hpnumber");
        }
        if (getIntent().hasExtra("password")) {
            password = getIntent().getExtras().getString("password");
        }


    }

    /**********************************************************************************************/
    //submit and sign up button
    private void submitSignup() {
        //images validation (front + back)
        if (frontImageName.equalsIgnoreCase(""))        //frontImageName is empty
        {
            Toast.makeText(getApplicationContext(), "Please take a picture of the front side of certificate"
                    , Toast.LENGTH_LONG).show();
            return;
        } else if (backImageName.equalsIgnoreCase(""))    //backImageName is empty
        {
            Toast.makeText(getApplicationContext(), "Please take a picture of the back side of certificate"
                    , Toast.LENGTH_LONG).show();
            return;
        }

        //display progress dialog
        progressDialog.setMessage("Registering professional...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //save both images into firebase storage
        //and get downloadURL for both images
        storageReference = FirebaseStorage.getInstance().getReference(
                getText(R.string.storageImageDirectoryName).toString() + "/" + frontImageName);

        UploadTask uploadTask = storageReference.putFile(frontImage);       //saving front image

        Task<Uri> urlTask = uploadTask.continueWithTask(
                new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
                {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        // Continue with the task to get the download URL
                        return storageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(
                        new OnCompleteListener<Uri>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    frontImageURL = downloadUri.toString(); //get front url

                                    storageReference = FirebaseStorage.getInstance().getReference(
                                            getText(R.string.storageImageDirectoryName).toString() + "/" + backImageName);

                                    UploadTask uploadTask2 = storageReference.putFile(backImage);           //saving back image
                                    Task<Uri> urlTask2 = uploadTask2.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
                                    {
                                        @Override
                                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task2) throws Exception {
                                            if (!task2.isSuccessful()) {
                                                throw task2.getException();
                                            }
                                            // Continue with the task to get the download URL
                                            return storageReference.getDownloadUrl();
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<Uri>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task2) {
                                            if (task2.isSuccessful()) {
                                                Uri downloadUri2 = task2.getResult();
                                                backImageURL = downloadUri2.toString(); //get back url

                                                //firebase authentication connection to register professional
                                                firebaseAuth.createUserWithEmailAndPassword(email, password)
                                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<AuthResult> task) {

                                                                //save professional details in database
                                                                if (task.isSuccessful()) {
                                                                    //create new professional
                                                                    Professional newProfessional = new Professional(email, firstname, lastname
                                                                            , hpnumber, frontImageURL, backImageURL, 5,1);

                                                                    //get professional new database key
                                                                    String pid = newProfessional.getpID();

                                                                    //enter professional data into database
                                                                    professionalDatabase.child(pid).setValue(newProfessional);

                                                                    //show Toast that user has been successfully registered
                                                                    Toast.makeText(getApplicationContext()
                                                                            , "Register Successful", Toast.LENGTH_SHORT).show();
                                                                    progressDialog.dismiss();

                                                                    //go back to login page
                                                                    finish();
                                                                    startActivity(new Intent(ProfessionalDocSubmitActivity.this, LoginActivity.class));
                                                                } else {
                                                                    Toast.makeText(getApplicationContext()
                                                                            , "Register Unsuccessful. Please try again.", Toast.LENGTH_SHORT).show();
                                                                    progressDialog.dismiss();
                                                                }
                                                            }
                                                        });
                                            } else {
                                                // Handle failures
                                                // ...
                                            }
                                        }
                                    });
                                } else {
                                    // Handle failures
                                    // ...
                                }
                            }
                        });

        Toast.makeText(this, "Images successfully uploaded", Toast.LENGTH_LONG).show();
    }

    /**********************************************************************************************/
    private String currentPhotoPath;
    private String currentImageName;

    private File createImageFile(String name) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = name + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,     /* prefix */
                ".jpg",    /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        currentImageName = imageFileName + ".jpg";
        return image;
    }

    /*
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
    */

    /**********************************************************************************************/
    private void dispatchTakePictureFront() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photofile = null;

            try {
                photofile = createImageFile("front");
            } catch (IOException ex) {
            }

            if (photofile != null) {
                frontImage = FileProvider.getUriForFile(this
                        , "ii954.csci314au19.fake_uber.fileprovider", photofile);
            }

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, frontImage);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE1);
        }
    }

    private void dispatchTakePictureBack() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photofile = null;

            try {
                photofile = createImageFile("back");
            } catch (IOException ex) {
            }

            if (photofile != null) {
                backImage = FileProvider.getUriForFile(this
                        , "ii954.csci314au19.fake_uber.fileprovider", photofile);
            }

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, backImage);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE2);
        }
    }

    /**********************************************************************************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //if front image
        if (requestCode == REQUEST_IMAGE_CAPTURE1 && resultCode == RESULT_OK) {

            //save image to gallery???

            //save image name
            frontImageName = currentImageName;

            //show image into imageView
            Glide.with(this).load(frontImage).into(frontView);
        }

        //if back image
        if (requestCode == REQUEST_IMAGE_CAPTURE2 && resultCode == RESULT_OK) {

            //save image to gallery???

            //save image path
            backImageName = currentImageName;

            //show image into imageView
            Glide.with(this).load(backImage).into(backView);
        }
    }

    /**********************************************************************************************/
    //override listener method for back button on action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //if id = back button
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**********************************************************************************************/
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent newIntent = new Intent(ProfessionalDocSubmitActivity.this, UserRegisterActivity.class);

        newIntent.putExtra("firstname", firstname);
        newIntent.putExtra("lastname", lastname);
        newIntent.putExtra("password", password);
        newIntent.putExtra("email", email);
        newIntent.putExtra("hpnumber", hpnumber);

        finish();
        startActivity(newIntent);
    }
}
