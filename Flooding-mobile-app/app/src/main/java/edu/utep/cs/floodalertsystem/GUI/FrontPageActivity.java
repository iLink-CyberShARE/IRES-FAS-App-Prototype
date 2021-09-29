package edu.utep.cs.floodalertsystem.GUI;

/**
 * <h1> Main Menu </h1>
 *
 * This class is the Main menu of the FAS application, showing options for the different
 * application's services
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;

import edu.utep.cs.floodalertsystem.Location.GPSTracker;
import edu.utep.cs.floodalertsystem.Model.UserInfo;
import edu.utep.cs.floodalertsystem.R;

public class FrontPageActivity extends AppCompatActivity {
    private final String TAG="Flood";
    private final String ACTIVITY="FrontPageActivity: ";

    private BottomNavigationView bottomMenu;
    private GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);

        //Loading the default fragment
        loadFragment(new ReportFragment());

        //Initialize variables and objects
        gps = new GPSTracker(this);
        bottomMenu=findViewById(R.id.navigationView);
        bottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = null;

                switch(menuItem.getItemId()){
                    case R.id.reports_item:
                        fragment=new ReportFragment();
                        break;
                    /**case R.id.create_item:
                     fragment=new UserInputFragment();
                     break;**/
                    case R.id.maps_item:
                        LatLng coord=getCoordinates();
                        Bundle bundle= new Bundle();
                        bundle.putString("lat",Double.toString(coord.latitude));
                        bundle.putString("lon",Double.toString(coord.longitude));
                        fragment=new MapFragment();
                        fragment.setArguments(bundle);
                        break;
                    case R.id.weather_item:
                        fragment=new WeatherFragment();
                        break;
                }
                return loadFragment(fragment);
            }
        });
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_settings:
                Intent i=new Intent(this,SettingsActivity.class);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public LatLng getCoordinates(){
        Double lat=null;
        Double lon=null;
        //Get Location
        //check if GPS enabled
        if(gps.canGetLocation()){
            lat = gps.getLatitude();
            lon = gps.getLongitude();
        }else{
            //If the application cannot access the location of the user
            //the GPS or Network is not enabled. Ask user to enable GPS/network in settings
            //If not use UTEP address as default.
            gps.showSettingsAlert();
            lat=31.7677946;
            lon=-106.5022808;
        }
        Log.d(TAG,ACTIVITY+"LAT= "+lat+"LON= "+lon); 
        return new LatLng(lat,lon);
    }

}
