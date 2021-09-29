package edu.utep.cs.floodalertsystem.GUI;

/**
 * <h1> User Input Fragment </h1>
 *
 * This class inflates the layout of the creation of report window.
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */


/**
 * THIS FRAGMENT IS NOT BEING USED ANYMORE
 * THIS FRAGMENT IS NOT BEING USED ANYMORE
 * THIS FRAGMENT IS NOT BEING USED ANYMORE
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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import java.util.HashMap;
import java.util.Map;

import edu.utep.cs.floodalertsystem.Location.GPSTracker;
import edu.utep.cs.floodalertsystem.Model.UserInfo;
import edu.utep.cs.floodalertsystem.R;
import edu.utep.cs.floodalertsystem.Model.ReportsManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserInputFragment extends Fragment {
    private final String TAG="Flood";
    private final String ACTIVITY="UserInputFragment: ";

    private BottomNavigationView bottomMenu;
    private ReportsManager reportsManager;

    //Request codes
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int REQUEST_BROWSE = 2;
    final private int REQUEST_COURSE_ACCESS = 123;


    private ImageView mPhotoImageView;
    private Uri mSelectedImageUri=null;
    private RatingBar ratingBar;
    private GPSTracker gps;
    private ImageView infoRating;
    private ImageView infoDescription;

    private final String[] requiredPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public UserInputFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView= inflater.inflate(R.layout.fragment_user_input, container, false);

        Log.d(TAG,ACTIVITY+"Started create report view");

        //Initialize variables and objects
        bottomMenu=getActivity().findViewById(R.id.navigationView);
        reportsManager=ReportsManager.getInstance();
        gps = new GPSTracker(getActivity());

        //Image View
        mPhotoImageView = fragmentView.findViewById(R.id.photo_image_view);

        //Uncomment line below for emergency type spinner if needed for future use
        //Spinner typeSpinner = fragmentView.findViewById(R.id.emergency_spinner);

        //Rating bar
        ratingBar = (RatingBar) fragmentView.findViewById(R.id.ratingBar); // initiate a rating bar
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override public void onRatingChanged(RatingBar ratingBar, float rating,
                                                  boolean fromUser) {
                Log.d(TAG,ACTIVITY+"Sad---------------------------------");   //---------------------------------
                if(rating<1.0f)
                    ratingBar.setRating(1.0f);
            }
        });

        //Description
        EditText descriptionEditText = fragmentView.findViewById(R.id.description_edit_text);

        //Submit button
        Button submitButton = fragmentView.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(v -> {
            Log.d(TAG,ACTIVITY+"-Start!-");
            double latitude = 0.00;
            double longitude = 0.00;
            float rating= ratingBar.getRating();
            String severity=rating==1?"Low":rating==2?"Medium":"High";
            //Getting description from input
            String description = descriptionEditText.getText().toString();
            //Getting Location
            //Check if GPS enabled
            if(gps.canGetLocation()){
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
            }else{
                //If the application can't get the location. GPS or Network is not enabled
                //Ask user to enable GPS/network in settings
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
            reportsManager.createReport(getActivity(),params);
            //Load next fragment
            loadNextFragment(new ReportFragment());
        });

        //Browse for picture (open gallery) button
        Button browseButton = fragmentView.findViewById(R.id.browse_button);
        browseButton.setOnClickListener(view -> {
            System.out.println("Hello");
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
        Button pictureButton = fragmentView.findViewById(R.id.picture_button);
        pictureButton.setOnClickListener(v -> {
            Log.d(TAG,ACTIVITY+"Sad---------------------------------");   //---------------------------------
            if (areMediaPermissionGranted()) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mSelectedImageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mSelectedImageUri);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                requestMediaPermissions();
            }
        });

        return fragmentView;
    }

    private boolean areMediaPermissionGranted() {
        for (String permission : requiredPermissions) {
            if (!(ActivityCompat.checkSelfPermission(getActivity(), permission) ==
                    PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    private void requestMediaPermissions() {
        ActivityCompat.requestPermissions(
                getActivity(),
                requiredPermissions,
                REQUEST_COURSE_ACCESS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private boolean loadNextFragment(Fragment fragment) {
        //Switching to next fragment
        if (fragment != null) {
            Menu menu = bottomMenu.getMenu();
            menu.findItem(R.id.reports_item).setChecked(true);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment, "findThisFragment")
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return false;
    }

}
