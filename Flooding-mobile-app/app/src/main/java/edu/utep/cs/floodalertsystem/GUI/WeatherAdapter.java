package edu.utep.cs.floodalertsystem.GUI;

/**
 * <h1> Weather Adapter </h1>
 *
 * This class takes the information of Open Weather Map for the diplay of the current temperature
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

import edu.utep.cs.floodalertsystem.Model.Weather;
import edu.utep.cs.floodalertsystem.Model.WeatherManager;
import edu.utep.cs.floodalertsystem.R;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.MyViewHolder> {
    private final String TAG="Flood";
    private final String ACTIVITY="WeatherAdapter: ";

    private Context context;
    private List<Weather> weatherList=new ArrayList<>();

    //Provide a reference to the views for each data item
    //Complex data items may need more than one view per item, and
    //you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView forecastCardView;
        private ImageView weatherIcon;
        private TextView tempView;
        private TextView dateView;
        private TextView descView;

        //Each data item is just a string in this case
        public MyViewHolder(View itemView) {
            super(itemView);
            this.forecastCardView=itemView.findViewById(R.id.forecast_cardview);
            this.weatherIcon=itemView.findViewById(R.id.forecast_weather_icon);
            this.tempView = itemView.findViewById(R.id.forecast_temp);
            this.dateView = itemView.findViewById(R.id.forecast_date);
            this.descView = itemView.findViewById(R.id.forecast_description);
        }
    }

    //Provide a suitable constructor (depends on the kind of dataset)
    public WeatherAdapter(@NonNull Context context,List<Weather> weatherList) {
        this.context=context;
        this.weatherList=weatherList;
    }

    //Create new views (invoked by the layout manager)
    @Override
    public WeatherAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_weather_view, parent, false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    //Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Weather weather = weatherList.get(position);
        if(weather.getRain().getVolume()!=null){
            holder.forecastCardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        }else{
            holder.forecastCardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        }
        //Forecast
        Ion.with(holder.weatherIcon)
                .placeholder(R.drawable.ic_sun)
                .error(R.drawable.error_image)
                .load("http://openweathermap.org/img/wn/"+weather.getWeatherData().getIcon()+"@2x.png");
        //Set temperature
        holder.tempView.setText(Html.fromHtml("<b>"+weather.getMain().getTemp()+ WeatherManager.getInstance().getUnitsSymbol()+"</b>"));
        //Set date
        holder.dateView.setText(Html.fromHtml(weather.getDate()));
        //Set description
        holder.descView.setText(Html.fromHtml(weather.getWeatherData().getDescription()));
    }

    //Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return weatherList.size();
    }
}