package edu.utep.cs.floodalertsystem.Model;

/**
 * <h1> Report </h1>
 *
 * Constructors, getters and setters of the report.
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */


import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Report {

    private String id;
    private String severity;
    private String description;
    private LatLng coord;
    private String date;
    private String imageName;
    private Drawable imageDrawable=null;
    private String lifespan;

    public Report() {

    }

    Report(String id,String severity, String description, LatLng coord, String date,String imageName) {
        this.id=id;
        this.severity = severity;
        this.description = description;
        this.coord=coord;
        this.date = date;
        this.imageName= imageName;
        this.lifespan = date;

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

    public LatLng getCoord(){ return coord; }

    public String getDate() {
        return date;
    }

    public String getImageName() {
        return imageName;
    }

    public Drawable getImageDrawable() {
        return imageDrawable;
    }

    //Calculation of time since creation of report
    public String getLifespan() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date newDate = sdf.parse(date);
        long millis = newDate.getTime();
        long sub = System.currentTimeMillis() - millis - 21600000 - 39600000;
        Date subDate = new Date(sub);
        String lifespan = sdf.format(subDate);
        lifespan = lifespan.substring(10);
        return lifespan;
    }

    public void setDate(String date) {
        this.date=date;
    }

    public void setImageDrawable(Drawable imageDrawable) {
        this.imageDrawable=imageDrawable;
    }

}