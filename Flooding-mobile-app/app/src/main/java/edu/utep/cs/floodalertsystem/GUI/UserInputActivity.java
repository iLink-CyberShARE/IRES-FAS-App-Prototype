package edu.utep.cs.floodalertsystem.GUI;

/**
 * <h1> User Input Activity </h1>
 *
 * This class displays the window and takes the information of the user to create a report.
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */


import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import java.util.HashMap;
import java.util.Map;

import edu.utep.cs.floodalertsystem.GUI.AlertDialogs.CameraAlertDialog;
import edu.utep.cs.floodalertsystem.GUI.AlertDialogs.CreditablityAlertDialog;
import edu.utep.cs.floodalertsystem.GUI.AlertDialogs.DescriptionAlertDialog;
import edu.utep.cs.floodalertsystem.GUI.AlertDialogs.SeverityAlertDialog;
import edu.utep.cs.floodalertsystem.Location.GPSTracker;
import edu.utep.cs.floodalertsystem.Model.UserInfo;
import edu.utep.cs.floodalertsystem.R;
import edu.utep.cs.floodalertsystem.Model.ReportsManager;

public class UserInputActivity extends AppCompatActivity {
    private final String TAG="Flood";
    private final String ACTIVITY="UserInputActivity: ";

    private ReportsManager reportsManager;

    //Request codes
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int REQUEST_BROWSE = 2;
    final private int REQUEST_COURSE_ACCESS = 123;

    private ImageView mPhotoImageView;
    private Uri mSelectedImageUri=null;
    private RatingBar ratingBar;
    private GPSTracker gps;

    private ImageView infoCamera;
    private ImageView infoSeverity;
    private ImageView infoDescription;
    private CameraAlertDialog cameraAlertDialog;
    private SeverityAlertDialog severityAlertDialog;
    private DescriptionAlertDialog descriptionAlertDialog;
    private CreditablityAlertDialog creditablilityAlertDialog;

    private final String[] requiredPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //Dialog fragments
    private void showAlertDialog(int title) {
        if(title==R.string.camera_dialog_title ){
            cameraAlertDialog =new CameraAlertDialog().newInstance(title);
            cameraAlertDialog.show(((FragmentActivity)this).getSupportFragmentManager(), "CameraDialogFragment");
        } else if(title==R.string.severity_dialog_title){
            severityAlertDialog =new SeverityAlertDialog().newInstance(title);
            severityAlertDialog.show(((FragmentActivity)this).getSupportFragmentManager(), "SeverityDialogFragment");
        }  else if(title==R.string.description_dialog_title ){
            descriptionAlertDialog =new DescriptionAlertDialog().newInstance(title);
            descriptionAlertDialog.show(((FragmentActivity)this).getSupportFragmentManager(), "DescriptionDialogFragment");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_input);

        Log.d(TAG,"in user activity");

        //Initialize variables and objects
        reportsManager=ReportsManager.getInstance();
        gps = new GPSTracker(UserInputActivity.this);

        //Back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //Image View
        mPhotoImageView = findViewById(R.id.photo_image_view);

        //Uncomment line below for emergency type spinner if needed for future use
        //Spinner typeSpinner = findViewById(R.id.emergency_spinner);

        //Rating bar
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override public void onRatingChanged(RatingBar ratingBar, float rating,
                                                  boolean fromUser) {
                if(rating<1.0f)
                    ratingBar.setRating(1.0f);
            }
        });

        //Description
        EditText descriptionEditText = findViewById(R.id.description_edit_text);

        //Information of activities
        infoCamera= findViewById(R.id.infoCamera);
        infoSeverity = findViewById(R.id.infoRating);
        infoDescription= findViewById(R.id.infoDescription);

        infoCamera.setOnClickListener(v -> {
            showAlertDialog(R.string.camera_dialog_title);
        });
        infoSeverity.setOnClickListener(v -> {
            showAlertDialog(R.string.severity_dialog_title);
        });
        infoDescription.setOnClickListener(v -> {
            showAlertDialog(R.string.description_dialog_title);
        });

        //Submit button
        Button submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(v -> {
            System.out.println("im here test");
            Log.d(TAG,ACTIVITY+"-Start!-");   
            Double latitude = 0.00;
            Double longitude = 0.00;
            float rating= ratingBar.getRating();
            String severity=rating==1?"Low":rating==2?"Medium":"High";
            //Getting description from input
            String description = descriptionEditText.getText().toString();
            //Getting Location
            //Checking if GPS enabled
            if(gps.canGetLocation()){
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
            }else{
                //If application can't get the user location. GPS or Network is not enabled.
                //Ask user to enable GPS/network in settings. If user does not enable GPS.
                //Use default address
                gps.showSettingsAlert();
            }

            Map<String,String> params=new HashMap<String, String>();
            params.put("userID" , UserInfo.getInstance().getUserID());
            params.put("severity",severity);
            params.put("description",description);
            params.put("lat",Double.toString(latitude));
            params.put("lon",Double.toString(longitude));
            if(mSelectedImageUri!=null){
                params.put("imagePath",getRealPathFromURI(mSelectedImageUri));
            }else{
                params.put("imagePath","");
            }
            params.put("imageName","");
            reportsManager.createReport(this,params);

           //Finish activity
            finish();
        });

        //Browse for picture (open gallery) button
        Button browseButton = findViewById(R.id.browse_button);
        browseButton.setOnClickListener(view -> {
            if (areMediaPermissionGranted()) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //Uncomment line below to filter jpeg, png, gif, etc.
                //pickIntent.setType("image/*");
                startActivityForResult(pickIntent, REQUEST_BROWSE);
            } else {
                requestMediaPermissions();
            }

        });

        //Take picture (open camera) button
        Button pictureButton = findViewById(R.id.picture_button);
        pictureButton.setOnClickListener(v -> {
            if (areMediaPermissionGranted()) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mSelectedImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mSelectedImageUri);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                requestMediaPermissions();
            }
        });
    }


    private boolean areMediaPermissionGranted() {
        for (String permission : requiredPermissions) {
            if (!(ActivityCompat.checkSelfPermission(this, permission) ==
                    PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    private void requestMediaPermissions() {
        ActivityCompat.requestPermissions(
                this,
                requiredPermissions,
                REQUEST_COURSE_ACCESS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_BROWSE:
                    mSelectedImageUri = data.getData();
                    //Display selected photo in image view
                    mPhotoImageView.setImageURI(mSelectedImageUri);
                    break;
                case REQUEST_IMAGE_CAPTURE:
                    mPhotoImageView.setImageURI(mSelectedImageUri);
                    break;
            }
        }
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}
