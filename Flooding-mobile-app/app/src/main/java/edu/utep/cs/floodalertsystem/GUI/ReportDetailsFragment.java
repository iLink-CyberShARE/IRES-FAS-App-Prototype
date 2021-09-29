package edu.utep.cs.floodalertsystem.GUI;

/**
 * <h1> Report Detailed Fragment </h1>
 *
 * This class inflates the layout for the detailed display of a report after a user click.
 * This layout shows buttons to created feedback and more.
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import edu.utep.cs.floodalertsystem.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportDetailsFragment extends Fragment implements View.OnClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Button createFeedback;
    private String mParam1;
    private String mParam2;

    public ReportDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */

    public static ReportDetailsFragment newInstance(String param1, String param2) {
        ReportDetailsFragment fragment = new ReportDetailsFragment();
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
        View v = inflater.inflate(R.layout.fragment_report_details, container, false);

        createFeedback = (Button) v.findViewById(R.id.create_feedback_button);

        createFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(),UserFeedbackActivity.class);
                startActivity(i);
            }
        });
        return v;
    }

    @Override
    public void onClick(View view) {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
    }
}
