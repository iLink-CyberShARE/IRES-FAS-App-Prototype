package edu.utep.cs.floodalertsystem.GUI;

/**
 * <h1> Report Adapter 2 </h1>
 *
 * This class provides reference to the detailed view of the reports and getters and setters
 * for its needed information
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import edu.utep.cs.floodalertsystem.Utils.Helper;
import edu.utep.cs.floodalertsystem.Model.Report;
import edu.utep.cs.floodalertsystem.R;

public class ReportAdapter2 extends RecyclerView.Adapter<ReportAdapter2.MyViewHolder> {
    private final String TAG="Flood";
    private final String ACTIVITY="ReportAdapter2: ";
    private Context context;
    private List<Report> reportsList=new ArrayList<>();
    private Helper helper;
    private String []severities;
    private String severityStr;
    private SharedPreferences prefs;
    private boolean loadImages;



    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final String CLICKTAG="Recycler view clicked";

        private ImageView imageView;
        private SpinKitView progress;
        private TextView addressView;
        private TextView severityView;
        private TextView dateView;
        private TextView lifespanView; //life span

        //Each data item is just a string in this case
        public MyViewHolder(View itemView) {
            super(itemView);
            //Set Image
            imageView = itemView.findViewById(R.id.report_image);
            progress= itemView.findViewById(R.id.spin_kit);
            addressView = itemView.findViewById(R.id.address_tag);
            severityView = itemView.findViewById(R.id.severity_tag);
            dateView = itemView.findViewById(R.id.date_tag);
            lifespanView = itemView.findViewById(R.id.lifespan_tag);

            itemView.setOnClickListener(this);
        }



        @Override
        public void onClick(View view) {

            Log.d(CLICKTAG,"Recycler View Clicked vato");

            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            Fragment myFragment = new ReportDetailsFragment();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myFragment).addToBackStack(null).commit();

        }
    }

    //Provide a suitable constructor (depends on the kind of dataset)
    public ReportAdapter2(@NonNull Context context, List<Report> reportsList) {
        this.context=context;
        this.reportsList=reportsList;
        this.helper=new Helper();
    }

    //Create new views (invoked by the layout manager)
    @Override
    public ReportAdapter2.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_report_view, parent, false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Report report = reportsList.get(position);
        String addressStr = helper.Coord2AddressStr(context, report.getCoord());

        String imgName=report.getImageName();
        Drawable imgDrawable=report.getImageDrawable();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        loadImages=prefs.getBoolean("prefImages",true);

        if(imgDrawable==null){
            if(loadImages){
                if(!imgName.equals("")){
                    showProgressImage(holder);
                }else{
                    showImagePlaceholder(holder);
                }
            }else{
                showImagePlaceholder(holder);
            }
        }else{
            showReportImage(holder,imgDrawable);
        }

        severities=context.getResources().getStringArray(R.array.Severities);
        switch(report.getSeverity()){
            case "Low":
                severityStr=severities[0];
                break;
            case "Medium":
                severityStr=severities[1];
                break;
            case "High":
                severityStr=severities[2];
                break;
        }

        //Set Address
        holder.addressView.setText(Html.fromHtml("<b>"+context.getResources().getString(R.string.Address)+" </b>"+ addressStr));

        //Set severity
        holder.severityView.setText(Html.fromHtml("<b>"+context.getResources().getString(R.string.Severity)+" </b>"+severityStr));

        //Set description
        holder.dateView.setText(Html.fromHtml("<b>"+context.getResources().getString(R.string.Date)+" </b>"+report.getDate()));

        //Set lifespan
        try {
            holder.lifespanView.setText(Html.fromHtml("<b>"+context.getResources().getString(R.string.lifespan)+" </b>"+report.getLifespan()));
        } catch (ParseException e) {
            holder.lifespanView.setText(Html.fromHtml("<b>"+context.getResources().getString(R.string.lifespan)+" </b>"+report.getDate().substring(10)));
        }
    }




    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return reportsList.size();
    }

    private void showImagePlaceholder(MyViewHolder holder){
        holder.progress.setVisibility(View.GONE);
        holder.imageView.setVisibility(View.VISIBLE);
    }
    private void showProgressImage(MyViewHolder holder){
        holder.progress.setVisibility(View.VISIBLE);
        holder.imageView.setVisibility(View.GONE);
    }
    private void showReportImage(MyViewHolder holder,Drawable imgDrawable){
        holder.progress.setVisibility(View.GONE);
        holder.imageView.setVisibility(View.VISIBLE);
        holder.imageView.setImageDrawable(imgDrawable);
    }
}