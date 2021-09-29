package edu.utep.cs.floodalertsystem.Model;

/**
 * <h1> Reports Manager </h1>
 *
 * This class has the information for the post of the report to the database and retrival
 * of the reports from the database.
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.utep.cs.floodalertsystem.Utils.HttpClient;

/**This class provides functions to manage a list of reports.
 */
public class ReportsManager {
    private final String TAG="Flood";
    private final String ACTIVITY="ReportsManager: ";

    private static ReportsManager reportsManager=null;
    private List<Report> reports;
    private HttpClient API=new HttpClient();
    private JSONParser parser=new JSONParser();
    private ReportsManagerListener listener;

    public interface ReportsManagerListener{
        void notifyDataSetChanged();
        void setRefreshingFalse();
    }

    /**Default constructor for ItemManager. Initializes the list of items.*/
    private ReportsManager() {
        reports=new ArrayList<Report>();
    }

    public static ReportsManager getInstance(){
        if(reportsManager==null){
            reportsManager=new ReportsManager();
        }
        return reportsManager;
    }

    public void setReportsManagerListener(ReportsManagerListener listener) {
        this.listener=listener;
    }

    /** Return the item list.
     * @return item list.*/
    //returning the whole list
    public List<Report> reportsList() {
        return Collections.unmodifiableList(reports);
    }
    /**Returns item list size.
     * @return item list size.
     */
    public int size() {
        return reports.size();
    }

    /**Indicates if the item is contained in the list.
     * @param item Item to be checked in the list.
     * @return	True if the item is contained in the list.
     */
    public boolean contains(Report item) {
        return reports.contains(item);
    }
    /**Return item at an specified index.
     * @param i Index of the item to be returned.
     * @return
     */
    public Report getReport(int i) {
        return reports.get(i);
    }
    /**Removes all items from items list.
     */
    public void removeAll() {
        reports.clear();
    }
    /**Removes item at specified index.
     * @param i Index of the item to be removed.
     * @return The item removed.
     */
    public Report removeReport(int i) {
        return reports.remove(i);
    }
    /**Indicates if the items list is empty.
     * @return True if the list is empty.
     */
    public boolean isEmpty() {
        return reports.isEmpty();
    }
    /**Converts the list into an array.
     * @param reportsList array to store items.
     * @return items array with items in list.
     */
    public Report[] toArray(Report[] reportsList) {
        return reports.toArray(reportsList);
    }
    /**Add an item to the item list.
     * @param report Item to be added.
     */
    public void addReport(Report report) {
        reports.add(report);
    }
    /**Add an item to the item list at specified index
     * @param i Index indicating where to place an item in the list.
     * @param report Item to be added.
     */
    public void addReport(int i,Report report) {
        reports.add(i,report);
    }

    public void retrieveReports(boolean loadImages, Map<String,String> params){
        //Retrieve data from database
        new Thread(()->{
            String jsonStr=API.connection("GET","/reports/retrieve",params);
            jsonStringToReport(jsonStr,false);
            listener.notifyDataSetChanged();
            listener.setRefreshingFalse();
            if(loadImages){ loadImages(); }
        }).start();
    }

    public void reportSensors(boolean loadImages, Map<String,String> params){
        //Retrieve data from database
        new Thread(()->{
            String jsonStr=API.connection("GET","/raspi/retrieve",params);
            jsonStringToReport(jsonStr,false);
            listener.notifyDataSetChanged();
            listener.setRefreshingFalse();
            if(loadImages){ loadImages(); }
        }).start();
    }

    public void createReport(Context context, Map<String,String> params){
        new Thread(()->{
            Log.d(TAG,ACTIVITY+params.get("imagePath"));
            Log.d(TAG,ACTIVITY+params.get("imageName"));
            if(!params.get("imagePath").equals("")){
                String imgName=API.uploadImage(context,params.get("imagePath"));
                params.put("imageName",imgName);
                Log.d(TAG,ACTIVITY+"Image uploaded!");
            }
            String jsonStr=API.connection("POST","/reports/create",params);
            jsonStringToReport(jsonStr,true);
            if(!params.get("imagePath").equals("")){
                Drawable drawable = Drawable.createFromPath(params.get("imagePath"));
                reports.get(0).setImageDrawable(drawable);
            }
            listener.notifyDataSetChanged();
        }).start();
    }

    public void loadImages(){
        ReportsManager reportsManager = ReportsManager.reportsManager;
        for(int i = 0; i< reportsManager.size(); i++){
            Report report=reports.get(i);
            String imgName=report.getImageName();
            if(!imgName.equals("")){
                new Thread(()->{
                    report.setImageDrawable(API.loadImage(imgName));
                    listener.notifyDataSetChanged();
                }).start();
            }
        }
    }

    private JSONObject jsonStringToJsonObject(String jsonStr){
        jsonStr=jsonStr.substring(1,jsonStr.length()-1);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) parser.parse(jsonStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void jsonStringToReport(String jsonStr,boolean first){
        try {
            Object obj = parser.parse(jsonStr);
            JSONArray jsonArray = (JSONArray)obj;
            Log.d(TAG,ACTIVITY+jsonArray.toString()); 
            for(int i=0;i<jsonArray.size();i++){
                JSONObject json=(JSONObject) jsonArray.get(i);
                String id=(String)json.get("_id");
                String severity=(String)json.get("severity");
                String description=(String)json.get("description");
                JSONObject location= (JSONObject) json.get("location");
                Double lat,lon;
                LatLng coord;
                if(location.get("lat").getClass().getName().equals("java.lang.Double") &&
                        location.get("lon").getClass().getName().equals("java.lang.Double")){
                    lat= (Double) location.get("lat");
                    lon=(Double) location.get("lon");
                }else {
                    lat=0.00;
                    lon=0.00;
                }
                coord=new LatLng(lat,lon);
                String date=(String)json.get("date");
                String imageName=(String)json.get("imageName");
                Log.d(TAG,"This is ID: "+id);
                Report report=new Report(id,severity,description,coord,date,imageName);
                if(first){reports.add(0,report);}else{reports.add(report);}
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
