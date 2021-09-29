package edu.utep.cs.floodalertsystem.GUI;

/**
 * <h1> User Feedback Fragment </h1>
 *
 * This class inflates the layout for the user feedback window
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import edu.utep.cs.floodalertsystem.GUI.AlertDialogs.CameraAlertDialog;
import edu.utep.cs.floodalertsystem.GUI.AlertDialogs.CreditablityAlertDialog;
import edu.utep.cs.floodalertsystem.GUI.AlertDialogs.DescriptionAlertDialog;
import edu.utep.cs.floodalertsystem.GUI.AlertDialogs.SeverityAlertDialog;
import edu.utep.cs.floodalertsystem.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFeedBackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class UserFeedBackFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public UserFeedBackFragment() {
        // Required empty public constructor
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Kermit.
     */

    public static UserFeedBackFragment newInstance(String param1, String param2) {
        UserFeedBackFragment fragment = new UserFeedBackFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("UserFeedbackFragment","in user feedback fragment click");
        return inflater.inflate(R.layout.fragment_user_feedback, container, false);
    }
}

