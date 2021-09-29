package edu.utep.cs.floodalertsystem.GUI;

/**
 * <h1> Settings Fragment </h1>
 *
 * Loading of images for switch of preference
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */


import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;

import edu.utep.cs.floodalertsystem.R;
import edu.utep.cs.floodalertsystem.Model.ReportsManager;

public class SettingsFragment extends PreferenceFragment {
    private final String TAG="Flood";
    private final String ACTIVITY="SettingsFragment: ";

    private SwitchPreference prefSwitch;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_main);

        //Load images switch button
        prefSwitch=(SwitchPreference) findPreference("prefImages");
        prefSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean switched = (boolean)newValue;
                Log.d(TAG,ACTIVITY+" pref Images: "+switched); //------------------------------

                if(switched){
                    ReportsManager.getInstance().loadImages();
                }
                return true;
            }
        });

    }

}