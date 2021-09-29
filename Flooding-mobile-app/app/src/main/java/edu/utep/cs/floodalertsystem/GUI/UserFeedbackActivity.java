package edu.utep.cs.floodalertsystem.GUI;

/**
 * <h1> User Feedback Activity </h1>
 *
 * This class gets the information of the user report feedback including upload of picture or
 * use of camera.
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
import edu.utep.cs.floodalertsystem.GUI.AlertDialogs.DescriptionAlertDialog;
import edu.utep.cs.floodalertsystem.GUI.AlertDialogs.SeverityAlertDialog;
import edu.utep.cs.floodalertsystem.Model.UserInfo;
import edu.utep.cs.floodalertsystem.R;
import edu.utep.cs.floodalertsystem.Model.FeedbackManager;

public class UserFeedbackActivity extends AppCompatActivity {
    private final String TAG="Flood";
    private final String ACTIVITY="UserFeedbackActivity: ";

    private FeedbackManager feedbackManager;

    //Request codes
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int REQUEST_BROWSE = 2;
    final private int REQUEST_COURSE_ACCESS = 123;

    private ImageView mPhotoImageView;
    private Uri mSelectedImageUri=null;
    private RatingBar ratingBar;
    private RatingBar ratingBar2;

    private ImageView infoCamera;
    private ImageView infoSeverity;
    private ImageView infoDescription;
    private CameraAlertDialog cameraAlertDialog;
    private SeverityAlertDialog severityAlertDialog;
    private DescriptionAlertDialog descriptionAlertDialog;

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
        setContentView(R.layout.fragment_user_feedback); //Luis change was user_activity..

        Log.d(TAG,"in user feedback");

        //Initialize variables and objects
        feedbackManager=FeedbackManager.getInstance();

        //Back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //Image View
        mPhotoImageView = findViewById(R.id.photo_image_view);

        //Emergency type spinner if needed for future use
        //Spinner typeSpinner = findViewById(R.id.emergency_spinner);

        //Rating bar
        ratingBar = (RatingBar) findViewById(R.id.ratingBar); // initiate a rating bar
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override public void onRatingChanged(RatingBar ratingBar, float rating,
                                                  boolean fromUser) {
                if(rating<1.0f)
                    ratingBar.setRating(1.0f);
            }
        });

        //Rating bar2
        ratingBar2 = (RatingBar) findViewById(R.id.ratingBar2); // initiate a rating bar
        ratingBar2.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

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
        infoSeverity = findViewById(R.id.infoSeverity);
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
            Log.d(TAG,ACTIVITY+"-Start!-");
            System.out.println("entered the submit");
            float rating= ratingBar.getRating();
            float rating2= ratingBar.getRating();
            String severity=rating==1?"Low":rating==2?"Medium":"High";
            //Getting description from input
            String description = descriptionEditText.getText().toString();
            Map<String,String> params=new HashMap<String, String>();
            params.put("userID" , UserInfo.getInstance().getUserID());
            params.put("severity",severity);
            params.put("description",description);
            //Getting credibility of user
            String credibility = rating2==1?"-2":rating2==2?"-1":rating2==3?"0":rating2==4?"1":"2";
            if(mSelectedImageUri!=null){
                params.put("imagePath",getRealPathFromURI(mSelectedImageUri));
            }else{
                params.put("imagePath","");
            }
            params.put("imageName","");
            System.out.println(params);
            feedbackManager.createFeedback(this,params);

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
                    // display selected photo in image view
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