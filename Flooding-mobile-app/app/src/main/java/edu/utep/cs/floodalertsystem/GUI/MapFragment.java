package edu.utep.cs.floodalertsystem.GUI;

/**
 * <h1> Map Fragment </h1>
 *
 * This class gets the arguments for the display of the map around the user location
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import edu.utep.cs.floodalertsystem.Utils.HttpClient;
import edu.utep.cs.floodalertsystem.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {
    private final String TAG="Flood";
    private final String ACTIVITY="MapFragment: ";

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView=inflater.inflate(R.layout.fragment_map, container, false);

        //Initialize variables and objects
        String urlStr=new HttpClient().getUrlStr();
        String lat=getArguments().getString("lat");
        String lon=getArguments().getString("lon");
        String loadUrl=urlStr+"/maps/display?lat="+lat+"&lon="+lon;
        Log.d(TAG,ACTIVITY+loadUrl);    

        //Initialize WebView
        WebView webView = (WebView) fragmentView.findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(loadUrl);

        return fragmentView;
    }

}
