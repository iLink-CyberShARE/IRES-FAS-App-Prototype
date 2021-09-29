package edu.utep.cs.floodalertsystem.GUI;

/**
 * <h1> Report Adapter </h1>
 *
 * This class sets the layout for the display of the reports saved in the database
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;
import java.util.List;

import edu.utep.cs.floodalertsystem.Utils.Helper;
import edu.utep.cs.floodalertsystem.Model.Report;
import edu.utep.cs.floodalertsystem.R;

public class ReportAdapter extends ArrayAdapter<Report> {
    private final String TAG="Flood";
    private final String ACTIVITY="ReportAdapter: ";

    private Context context;
    private List<Report> reportsList=new ArrayList<>();
    private Helper helper;
    private String addressStr;

    ReportAdapter(@NonNull Context context, List<Report> reportsList){
        super(context,0,reportsList);
        this.context=context;
        this.reportsList=reportsList;

    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listView = convertView;
        Report report = reportsList.get(position);
        if(listView == null)
            listView = LayoutInflater.from(context).inflate(R.layout.list_report_view,parent,false);

        addressStr=helper.Coord2AddressStr(context,report.getCoord());

        //Set Image
        ImageView imageView = listView.findViewById(R.id.report_image);

        SpinKitView progress= listView.findViewById(R.id.spin_kit);
        String imgName=report.getImageName();
        Drawable imgDrawable=report.getImageDrawable();

        if(!imgName.equals("")){
            if(imgDrawable==null){
                progress.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
            }else{
                progress.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageDrawable(imgDrawable);
            }
        }else{
            progress.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }

        //Set Address
        TextView addressView = listView.findViewById(R.id.address_tag);
        addressView.setText(Html.fromHtml("<b>Address: </b>"+addressStr));

        //Set severity
        TextView severityView = listView.findViewById(R.id.severity_tag);
        severityView.setText(Html.fromHtml("<b>Severity: </b>"+report.getSeverity()));

        //Set description
        TextView dateView = listView.findViewById(R.id.date_tag);
        dateView.setText(Html.fromHtml("<b>Date: </b>"+report.getDate()));

        return listView;
    }
}
