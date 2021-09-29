package edu.utep.cs.floodalertsystem.GUI;

/**
 * <h1> Weather Fragment </h1>
 *
 * This class inflates the layout for the display of the current temperature window.
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */


import android.content.SharedPreferences;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.koushikdutta.ion.Ion;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import edu.utep.cs.floodalertsystem.Utils.Helper;
import edu.utep.cs.floodalertsystem.Location.GPSTracker;
import edu.utep.cs.floodalertsystem.R;
import edu.utep.cs.floodalertsystem.Model.WeatherManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment {
    private final String TAG="Flood";
    private final String ACTIVITY="WeatherFragment: ";

    private WeatherManager weatherManager;
    private GPSTracker gps;
    private Helper helper;
    private ImageView weatherIcon;
    private TextView city;
    private TextView temp;
    private TextView tempMin;
    private TextView tempMax;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter weatherAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Handler handler;
    private SharedPreferences prefs;
    private String langTag;
    private String langValue;
    private String units="metric";
    private String unitsSymbol="°C";
    private Double latitude = 0.00;
    private Double longitude = 0.00;
    private Map<String,String> params;
    private SwipeRefreshLayout swipeContainer;


    public WeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View fragmentView =inflater.inflate(R.layout.fragment_weather, container, false);

        //Initialize variables and objects
        weatherManager = WeatherManager.getInstance();
        weatherAdapter = new WeatherAdapter(getActivity(), weatherManager.getForecastList());
        gps = new GPSTracker(getActivity());
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        this.handler = new Handler(getActivity().getMainLooper());
        helper=new Helper();
        updateParamValues();

        weatherIcon=fragmentView.findViewById(R.id.weather_icon);
        city=fragmentView.findViewById(R.id.city_value);
        temp=fragmentView.findViewById(R.id.temp_value);
        tempMin=fragmentView.findViewById(R.id.temp_min_value);
        tempMax=fragmentView.findViewById(R.id.temp_max_value);

        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.forecast_list);
        //Use this setting to improve performance if you know that changes
        //in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        //Use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        weatherManager.setWeatherManagerListener(new WeatherManager.WeatherManagerListener() {
            @Override
            public void notifyCurrentWeatherDataSetChanged() {
                handler.post(()->{
                    paintCurrentWeather();
                });
            }

            @Override
            public void notifyForecastDataSetChanged() {
                handler.post(()->{
                    weatherAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void setRefreshingFalse() {
                handler.post(()->{
                    swipeContainer.setRefreshing(false);
                });
            }
        });

        recyclerView.setAdapter(weatherAdapter);

        //Set city
        Address address=helper.Coord2Address(getActivity(),new LatLng(latitude,longitude));
        if(address!=null){
            String addressCity=address.getLocality();
            city.setText(addressCity);
        }

        //Swipe container
        swipeContainer = (SwipeRefreshLayout) fragmentView.findViewById(R.id.swipeContainer2);
        swipeContainer.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        weatherManager.removeAll();
                        weatherAdapter.notifyDataSetChanged();
                        updateParamValues();
                        weatherManager.requestForecast(params);
                    }
                }
        );

        if(weatherManager.size()==0){
            weatherManager.requestForecast(params);
        }else{
            paintCurrentWeather();
        }

        return fragmentView;
    }

    private void updateParamValues(){
        langTag=Locale.getDefault().getDisplayLanguage();
        units=prefs.getString("prefUnits","metric");
        unitsSymbol=units.equals("imperial")?"°F":units.equals("metric")?"°C":"°C";
        langValue=langTag.equals("English")?"en":langTag.equals("español")?"es":"es";

        if(gps.canGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }else{
            gps.showSettingsAlert();
        }

        Log.d(TAG,ACTIVITY+units);  

        params=new HashMap<String, String>();
        params.put("units",units);
        params.put("lang",langValue);
        params.put("lat",Double.toString(latitude));
        params.put("lon",Double.toString(longitude));

        weatherManager.setUnitsSymbol(unitsSymbol);
    }

    private void paintCurrentWeather(){
        //Current Weather
        //Set Icon
        Log.d(TAG,ACTIVITY+weatherManager.getCurrentWeather().getWeatherData().getIcon());   //------------------------------------------
        Ion.with(weatherIcon)
                .placeholder(R.drawable.ic_sun)
                .error(R.drawable.error_image)
                .load("http://openweathermap.org/img/wn/"+weatherManager.getCurrentWeather().getWeatherData().getIcon()+"@2x.png");
        //Set temperature
        temp.setText(Html.fromHtml("<b>"+weatherManager.getCurrentWeather().getMain().getTemp()+unitsSymbol+"</b>"));
        //Set date
        tempMin.setText(Html.fromHtml(weatherManager.getCurrentWeather().getMain().getTemp_min()+unitsSymbol));
        //Set description
        tempMax.setText(Html.fromHtml(weatherManager.getCurrentWeather().getMain().getTemp_max()+unitsSymbol));
    }

}
