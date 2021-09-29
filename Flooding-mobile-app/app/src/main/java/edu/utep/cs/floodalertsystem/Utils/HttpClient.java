package edu.utep.cs.floodalertsystem.Utils;

/**
 * <h1> Http Client </h1>
 *
 * Connection to the flooding server for the display of reports in the map and the use of
 * Open Weather Map for the display of the current temperature.
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import edu.utep.cs.floodalertsystem.R;
import id.zelory.compressor.*;

public class HttpClient {
    private final String TAG="Flood";
    private final String ACTIVITY="HttpClient: ";

    private String urlStr;
    private String APPID;
    public HttpClient(){

        this.urlStr= "http://localhost:8500";
        this.APPID="addYourAppID";
    }

    //Connection to the openweather API for the app weather forecast
    public String connection(String method, String API, Map<String,String> params) {
        String line=null;
        StringBuffer buffer=new StringBuffer();
        HttpURLConnection con = null;
        try {
            URL url=null;
            switch(API){
                case "/weather":
                case "/forecast":
                    url= new URL("http://api.openweathermap.org/data/2.5"+API+"?"+params2Str(params)+"&APPID="+APPID);
                    break;
                case "/reports/retrieve":
                    url= new URL(urlStr+API+"?"+params2Str(params));
                    break;
                case "feedback/retrieve":
                    url = new URL(urlStr+API+"?"+params2Str(params));
                default:
                    url= new URL(urlStr+API);
                    break;
            }

            Log.d(TAG,ACTIVITY+url.toString());
            con = (HttpURLConnection) url.openConnection();
            // optional default is GET
            con.setRequestMethod(method);
            con.setRequestProperty("User-Agent", "...");
            if(method.equals("POST")){
                // Send post request
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(params2Str(params));
                wr.flush();
                wr.close();
            }
            String encoding = con.getContentEncoding();
            if (encoding == null) { encoding = "utf-8"; }
            InputStreamReader reader = null;
            if ("gzip".equals(encoding)) { // gzipped document?
                reader = new InputStreamReader(new GZIPInputStream(con.getInputStream()));
            } else {
                reader = new InputStreamReader(con.getInputStream(), encoding);
            }
            BufferedReader in = new BufferedReader(reader);
            while ((line = in.readLine()) != null ) {
                buffer.append(line + "\r\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
            Log.d(TAG,ACTIVITY+buffer.toString());
            return buffer.toString();
        }
    }

    public String uploadImage(Context context, String imgPath){
        String urlString =urlStr+"/images/upload";
        File file=new File(imgPath);
        File compressedImgFile = new File(imgPath);
        try {
            compressedImgFile = new Compressor(context).compressToFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Ion.with(context)
                .load(urlString)
                //.progressDialog(pd)
                //.setMultipartParameter("file", "file")
                .setMultipartFile("file", "image/*", compressedImgFile)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Log.d(TAG,ACTIVITY+"Successful"+result.toString());
                    }
                });
        return file.getName();
    }

    public Drawable loadImage(String imgName){
        String urlString =urlStr+"/images/download/"+imgName;
        Drawable image = null;
        Log.d(TAG,ACTIVITY+urlString);
        try {
            URL url = new URL(urlString);
            InputStream is = (InputStream)url.getContent();
            image = Drawable.createFromStream(is, "src");
        } catch (MalformedURLException e) {
            // handle URL exception
            image = null;
        } catch (IOException e) {
            // handle InputStream exception
            image = null;
        }
        return image;
    }

    public void DisplayMap(Context context, LatLng coord){
        String lat=Double.toString(coord.latitude);
        String lon=Double.toString(coord.longitude);

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // Begin customizing
        // set toolbar colors
        builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary));
        builder.setSecondaryToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary));

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(context, Uri.parse(urlStr+"/maps/display?lat="+lat+"&lon="+lon));
    }

    public String getUrlStr() {
        return urlStr;
    }

    private String params2Str(Map<String,String>params){
        // using for-each loop for iteration over Map.entrySet()
        String paramsStr="";
        for (Map.Entry<String,String> entry : params.entrySet()){
            paramsStr+=(entry.getKey()+"="+entry.getValue()+"&");
        }
        paramsStr=paramsStr.substring(0,paramsStr.length()-1);
        return paramsStr;
    }
}
