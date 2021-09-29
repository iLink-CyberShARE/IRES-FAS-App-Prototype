package edu.utep.cs.floodalertsystem.Model;

/**
 * <h1> Feedback Manager </h1>
 *
 * This class has the information for the post of the feedback to the database and retrival
 * of the feedback from the database.
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

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

/**This class provides functions to manage a list of feedback.
 */
public class FeedbackManager {
    private final String TAG="Flood";
    private final String ACTIVITY="FeedbackManager: ";

    private static FeedbackManager feedbackManager=null;
    private List<Feedback> feedbacks;
    private HttpClient API=new HttpClient();
    private JSONParser parser=new JSONParser();
    private FeedbackManagerListener listener;

    public interface FeedbackManagerListener{
        void notifyDataSetChanged();
        void setRefreshingFalse();
    }

    /**Default constructor for ItemManager. Initializes the list of items.*/
    private FeedbackManager() {
        feedbacks=new ArrayList<Feedback>();
    }

    public static FeedbackManager getInstance(){
        if(feedbackManager==null){
            feedbackManager=new FeedbackManager();
        }
        return feedbackManager;
    }

    public void setFeedbackManagerListener(FeedbackManagerListener listener) {
        this.listener=listener;
    }

    /** Return the item list.
     * @return item list.*/
    public List<Feedback> feedbackList() {
        return Collections.unmodifiableList(feedbacks);
    }
    /**Returns item list size.
     * @return item list size.
     */
    public int size() {
        return feedbacks.size();
    }

    /**Indicates if the item is contained in the list.
     * @param item Item to be checked in the list.
     * @return	True if the item is contained in the list.
     */
    public boolean contains(Feedback item) {
        return feedbacks.contains(item);
    }
    /**Return item at an specified index.
     * @param i Index of the item to be returned.
     * @return
     */
    public Feedback getFeedback(int i) {
        return feedbacks.get(i);
    }
    /**Removes all items from items list.
     */
    public void removeAll() {
        feedbacks.clear();
    }
    /**Removes item at specified index.
     * @param i Index of the item to be removed.
     * @return The item removed.
     */
    public Feedback removeFeedback(int i) {
        return feedbacks.remove(i);
    }
    /**Indicates if the items list is empty.
     * @return True if the list is empty.
     */
    public boolean isEmpty() {
        return feedbacks.isEmpty();
    }

    public Feedback[] toArray(Feedback[] FeedbackList) { return feedbacks.toArray(FeedbackList);
    }
    /**Add an item to the item list.
     * @param feedback Item to be added.
     */
    public void addFeedback(Feedback feedback) {
        feedbacks.add(feedback);
    }

    public void addFeedback(int i,Feedback feedback) {
        feedbacks.add(i,feedback);
    }

    public void retrievefeedbacks(boolean loadImages, Map<String,String> params){
        //Retrieve data from database
        new Thread(()->{
            String jsonStr=API.connection("GET","/feedback/retrieve",params);
            jsonStringToFeedback(jsonStr,false);
            listener.notifyDataSetChanged();
            listener.setRefreshingFalse();
            if(loadImages){ loadImages(); }
        }).start();
    }

    public void createFeedback(Context context, Map<String,String> params){
        new Thread(()->{
            Log.d(TAG,ACTIVITY+params.get("imagePath")); 
            Log.d(TAG,ACTIVITY+params.get("imageName"));
            if(!params.get("imagePath").equals("")){
                String imgName=API.uploadImage(context,params.get("imagePath"));
                params.put("imageName",imgName);
                Log.d(TAG,ACTIVITY+"Image uploaded!");
            }

            String jsonStr=API.connection("POST","/feedback/create",params);
            jsonStringToFeedback(jsonStr,true);
            if(!params.get("imagePath").equals("")){
                Drawable drawable = Drawable.createFromPath(params.get("imagePath"));
                feedbacks.get(0).setImageDrawable(drawable);
            }
        }).start();
    }

    public void loadImages(){
        FeedbackManager feedbackManager = FeedbackManager.feedbackManager;
        for(int i = 0; i< feedbackManager.size(); i++){
            Feedback feedback=feedbacks.get(i);
            String imgName=feedback.getImageName();
            if(!imgName.equals("")){
                new Thread(()->{
                    feedback.setImageDrawable(API.loadImage(imgName));
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

    private void jsonStringToFeedback(String jsonStr,boolean first){
        try {
            Object obj = parser.parse(jsonStr);
            JSONArray jsonArray = (JSONArray)obj;
            Log.d(TAG,ACTIVITY+jsonArray.toString());
            for(int i=0;i<jsonArray.size();i++){
                JSONObject json=(JSONObject) jsonArray.get(i);
                String id=(String)json.get("_id");
                String severity=(String)json.get("severity");
                String description=(String)json.get("description");
                //added for credibility
                String credibility=(String)json.get("credibility");
                String date=(String)json.get("date");
                String imageName=(String)json.get("imageName");
                Feedback feedback=new Feedback(id,severity,description,date,imageName,credibility);
                if(first){feedbacks.add(0,feedback);}else{feedbacks.add(feedback);}
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}