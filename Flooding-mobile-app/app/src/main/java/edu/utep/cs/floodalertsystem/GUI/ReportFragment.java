package edu.utep.cs.floodalertsystem.GUI;

/**
 * <h1> Report Fragment </h1>
 *
 * This class inflates and loads the images for the display of the reports
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import edu.utep.cs.floodalertsystem.R;
import edu.utep.cs.floodalertsystem.Model.ReportsManager;

public class ReportFragment extends Fragment {
    private final String TAG="Flood";
    private final String ACTIVITY="ReportFragment: ";
    private ReportsManager reportsManager;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter reportAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Handler handler;
    private SharedPreferences prefs;
    private boolean loadImages;
    private String limit;
    private SwipeRefreshLayout swipeContainer;

    public ReportFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView= inflater.inflate(R.layout.fragment_report, container, false);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        loadImages=prefs.getBoolean("prefImages",true);

        //Modify Main thread
        this.handler = new Handler(getActivity().getMainLooper());

        FloatingActionButton fab = fragmentView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(),UserInputActivity.class);
                startActivity(i);
            }
        });

        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.reports_list);
        //Use the settings below to improve performance if you know that changes
        //in content do not change the layout size of the RecyclerView
        //recyclerView.setHasFixedSize(true);
        //recyclerView.setOnClickListener(view -> Toast.makeText(getContext(),"clicked",Toast.LENGTH_LONG).show());

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        reportsManager=ReportsManager.getInstance();
        reportsManager.setReportsManagerListener(new ReportsManager.ReportsManagerListener() {
            @Override
            public void notifyDataSetChanged() {
                handler.post(()->{
                    reportAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void setRefreshingFalse() {
                swipeContainer.setRefreshing(false);
            }
        });

        reportAdapter = new ReportAdapter2(getActivity(), reportsManager.reportsList());
        recyclerView.setAdapter(reportAdapter);



        //Swipe container
        swipeContainer = (SwipeRefreshLayout) fragmentView.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        reportsManager.removeAll();
                        reportAdapter.notifyDataSetChanged();
                        retrieveReports();
                    }
                }
        );

        if(reportsManager.size()==0){
            retrieveReports();
        }

        return fragmentView;
    }

    private void retrieveReports(){
        limit=prefs.getString("prefLimit","10");;
        Map<String,String> params=new HashMap<String, String>();
        params.put("limit",limit);
        reportsManager.retrieveReports(loadImages,params);
        reportsManager.reportSensors(loadImages,params);
    }
}
