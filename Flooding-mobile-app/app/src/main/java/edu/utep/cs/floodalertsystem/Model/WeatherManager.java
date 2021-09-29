package edu.utep.cs.floodalertsystem.Model;

/**
 * <h1> Weather Manager </h1>
 *
 * This class requests the weather information from Open Street Map and extracts the information
 * from a JSON file.
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.utep.cs.floodalertsystem.Utils.HttpClient;

public class WeatherManager {
    private final String TAG="Flood";
    private final String ACTIVITY="WeatherManager: ";

    private static WeatherManager weatherManager =null;
    private Weather currentWeather;
    private List<Weather> forecastList;
    private HttpClient API=new HttpClient();
    private WeatherManager.WeatherManagerListener listener;
    private String unitsSymbol="°C";

    public interface WeatherManagerListener{
        void notifyCurrentWeatherDataSetChanged();
        void notifyForecastDataSetChanged();
        void setRefreshingFalse();
    }

    private WeatherManager(){
        currentWeather=new Weather();
        forecastList=new ArrayList<Weather>();
    }

    public static WeatherManager getInstance(){
        if(weatherManager ==null){
            weatherManager =new WeatherManager();
        }
        return weatherManager;
    }

    public void setWeatherManagerListener(WeatherManager.WeatherManagerListener listener) {
        this.listener=listener;
    }

    /** Return the item list.
     * @return item list.*/
    public List<Weather> getForecastList() {
        return Collections.unmodifiableList(forecastList);
    }
    /**Returns item list size.
     * @return item list size.
     */
    public int size() {
        return forecastList.size();
    }

    /**Indicates if the item is contained in the list.
     * @param item Item to be checked in the list.
     * @return	True if the item is contained in the list.
     */
    public boolean contains(Weather item) {
        return forecastList.contains(item);
    }
    /**Return item at an specified index.
     * @param i Index of the item to be returned.
     * @return
     */
    public Weather getWeather(int i) {
        return forecastList.get(i);
    }
    /**Removes all items from items list.
     */
    public void removeAll() {
        forecastList.clear();
    }
    /**Removes item at specified index.
     * @param i Index of the item to be removed.
     * @return The item removed.
     */
    public Weather removeWeather(int i) {
        return forecastList.remove(i);
    }
    /**Indicates if the items list is empty.
     * @return True if the list is empty.
     */
    public boolean isEmpty() {
        return forecastList.isEmpty();
    }
    /**Converts the list into an array.
     * @param forecastList2 array to store items.
     * @return items array with items in list.
     */
    public Weather[] toArray(Weather[] forecastList2) {
        return forecastList.toArray(forecastList2);
    }
    /**Add an item to the item list.
     * @param weather Item to be added.
     */
    public void addWeather(Weather weather) {
        forecastList.add(weather);
    }
    /**Add an item to the item list at specified index
     * @param i Index indicating where to place an item in the list.
     * @param weather Item to be added.
     */
    public void addWeather(int i,Weather weather) {
        forecastList.add(i,weather);
    }

    public String getUnitsSymbol(){ return unitsSymbol;}

    public void setUnitsSymbol(String unitsSymbol){ this.unitsSymbol=unitsSymbol;}

    public Weather getCurrentWeather() {
        return currentWeather;
    }

    public void setCurrentWeather(Weather currentWeather){
        this.currentWeather=currentWeather;
    }

    public void requestForecast(Map<String,String> params){
        //Request weatherManager to API
        new Thread(()->{
            currentWeather=null;
            listener.notifyForecastDataSetChanged();
            String jsonStr=API.connection("GET","/weather",params);
            JSONObject jsonObj= null;
            try {
                jsonObj = new JSONObject(jsonStr);
                currentWeather=extractWeatherData(jsonObj);
                listener.notifyCurrentWeatherDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonStr=API.connection("GET","/forecast",params);
            jsonStringToForecast(jsonStr);
            listener.notifyForecastDataSetChanged();
            listener.setRefreshingFalse();
        }).start();
    }

    private void jsonStringToForecast(String jsonStr){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonStr);
            Log.d(TAG,ACTIVITY+jsonObject.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            Log.d(TAG,ACTIVITY+jsonArray.toString());
            for(int i=0;i<jsonArray.length();i++){
                JSONObject json=(JSONObject) jsonArray.get(i);
                forecastList.add(extractWeatherData(json));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Getting the information for the weather forecast
    private Weather extractWeatherData(JSONObject json){
        Weather weather=new Weather();
        JSONObject jsonWeather= null;
        JSONObject jsonMain=null;
        JSONObject jsonRain=null;
        try {
            jsonWeather = (JSONObject) json.getJSONArray("weather").get(0);
            weather.getWeatherData().setId(jsonWeather.getInt("id"));
            weather.getWeatherData().setMain(jsonWeather.getString("main"));
            weather.getWeatherData().setDescription(jsonWeather.getString("description"));
            weather.getWeatherData().setIcon(jsonWeather.getString("icon"));
            //Filling Main
            jsonMain=json.getJSONObject("main");
            weather.getMain().setTemp(jsonMain.getDouble("temp"));
            weather.getMain().setTemp_min(jsonMain.getDouble("temp_min"));
            weather.getMain().setTemp_max(jsonMain.getDouble("temp_max"));
            weather.getMain().setHumidity(jsonMain.getDouble("humidity"));
            weather.getMain().setPressure(jsonMain.getDouble("pressure"));
            //Filling dt_txt
            weather.setDate(json.getString("dt_txt"));
            //Filling Rain
            if(json.has("rain")){
                jsonRain=json.getJSONObject("rain");
                weather.getRain().setVolume(jsonRain.getDouble("3h"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return weather;
    }
}
