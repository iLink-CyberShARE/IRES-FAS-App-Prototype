package edu.utep.cs.floodalertsystem.Utils;

/**
 * <h1> Helper </h1>
 *
 * This is a helper class for the addresses in the database.
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Helper {
    private final String TAG="Flood";
    private final String ACTIVITY="Helper: ";

    public String Coord2AddressStr(Context context, LatLng coord){
        Geocoder geoCoder=new Geocoder(context, Locale.getDefault());
        String addressStr="";
        Address address;
        try{
            List<Address> addresses=geoCoder.getFromLocation(coord.latitude,coord.longitude,1);
            if(addresses != null && addresses.size()>0){
                address = addresses.get(0);
                for(int i=0;i<=address.getMaxAddressLineIndex();i++){
                    addressStr+=address.getAddressLine(i)+" ";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addressStr;
    }

    public Address Coord2Address(Context context, LatLng coord){
        Geocoder geoCoder=new Geocoder(context, Locale.getDefault());
        String addressStr="";
        Address address=null;
        try{
            List<Address> addresses=geoCoder.getFromLocation(coord.latitude,coord.longitude,1);
            if(addresses != null && addresses.size()>0){
                address = addresses.get(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }
}
