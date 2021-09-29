package edu.utep.cs.floodalertsystem.Model;

/**
 * <h1> Feedback </h1>
 *
 * Constructors, getters and setters for the feedback.
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */


import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Feedback {

    private String id;
    private String severity;
    private String description;
    private String date;
    private String imageName;
    private Drawable imageDrawable=null;
    private String credibility;

    public Feedback() {

    }

    Feedback(String id,String severity, String description, String date,String imageName, String credibility) {
        this.id=id;
        this.severity = severity;
        this.description = description;
        this.date = date;
        this.imageName= imageName;
        //added for credibility
        this.credibility = credibility;

    }

    public Feedback(String id,String severity, String description, String date,String imageName){
        this.id=id;
        this.severity = severity;
        this.description = description;
        this.date = date;
        this.imageName= imageName;
    }

    public String getId() {
        return id;
    }

    public String getSeverity() {
        return severity;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date=date;
    }

    public String getImageName() {
        return imageName;
    }

    public Drawable getImageDrawable() {
        return imageDrawable;
    }

    //added for credibility
    public String  getCredibility() {
        return credibility;
    }

    //added for credibility
    public void setCredibility(String credibility){
        this.credibility = credibility;
    }

    public void setImageDrawable(Drawable imageDrawable) {
        this.imageDrawable=imageDrawable;
    }
}
