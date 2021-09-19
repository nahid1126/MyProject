package com.example.tourmate.weather_report;

import android.content.Context;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourmate.R;
import com.example.tourmate.databinding.ForecastRowModelBinding;
import com.example.tourmate.forecast_weather_response.ForeCast;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {
    private Context context;
    private List<ForeCast> foreCastList;

    public ForecastAdapter(Context context, List<ForeCast> foreCastList) {
        this.context = context;
        this.foreCastList = foreCastList;
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       ForecastRowModelBinding binding= DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.forecast_row_model,parent,false);
       return new ForecastViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {

        final ForeCast foreCast=foreCastList.get(position);

       Long date= foreCast.getDt();
       double tem=foreCast.getMain().getTemp();
       double minTem=foreCast.getMain().getTempMin();
       double maxTem=foreCast.getMain().getTempMax();

        String icon=foreCast.getWeather().get(0).getIcon();
        String main=foreCast.getWeather().get(0).getMain();
        String des=foreCast.getWeather().get(0).getDescription();

        int humidity=foreCast.getMain().getHumidity();
        int pressure=foreCast.getMain().getPressure();

        Uri uri=Uri.parse(WeatherUtils.Icon.ICON_PREFIX+icon+WeatherUtils.Icon.ICON_SUFFIX);
        Picasso.get().load(uri).into(holder.binding.forecastImageView);
        holder.binding.forecastImageDesc.setText(des);

        String dateTime=WeatherUtils.getForcastDate(date);
        holder.binding.forecastDateTv.setText(dateTime);

        holder.binding.forecastTempTv.setText("Temp : "+tem+" °C");
        holder.binding.forecastMinTem.setText("Min Tem: "+minTem+" °C");
        holder.binding.forecastMaxTem.setText("Max : "+maxTem+" °C");


        holder.binding.forecastHumidityTv.setText("Humidity : "+humidity+" %");
        holder.binding.forecastPressureTv.setText("Pressure : "+pressure+" hpa");





    }

    @Override
    public int getItemCount() {
        return foreCastList.size();
    }


    public class ForecastViewHolder extends RecyclerView.ViewHolder {
        ForecastRowModelBinding binding;
        public ForecastViewHolder(@NonNull ForecastRowModelBinding itemView) {
            super(itemView.getRoot());

            binding=itemView;
        }
    }
}
