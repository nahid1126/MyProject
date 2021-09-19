package com.example.tourmate.weather_report;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tourmate.R;
import com.example.tourmate.current_weather_response.WeatherResponse;
import com.example.tourmate.databinding.FragmentCurrentWeatherBinding;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentWeatherFragment extends Fragment {
    private FragmentCurrentWeatherBinding binding;
    private String units="metric"; // imperial for furenhait

    private static String TAG="current";

   private Context context;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }


    public CurrentWeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentCurrentWeatherBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void updateLoction(final double latitude, final double longitude){
        // binding.latLongTv.setText(latitude+" , "+longitude);

        String endUrl=String.format("weather?lat=%f&lon=%f&units=%s&appid=%s",latitude,longitude,units,WeatherUtils.API_KEY);   //akhane place holder bosano ase..

        WeatherServiceApi serviceApi=RetrofitClient.getRetrofitClient().create(WeatherServiceApi.class);

        serviceApi.getCurrentWeatherData(endUrl).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if(response.isSuccessful()){
                    WeatherResponse weatherResponse=response.body();

                    double temp=weatherResponse.getMain().getTemp();
                    binding.currentTV.setText(temp+" °C");


                    String icon=weatherResponse.getWeather().get(0).getIcon();
                    Uri uri=Uri.parse(WeatherUtils.Icon.ICON_PREFIX+icon+WeatherUtils.Icon.ICON_SUFFIX);
                    Picasso.get().load(uri).into(binding.iconImageView);

                    double minTem=weatherResponse.getMain().getTempMin();
                    double maxTem=weatherResponse.getMain().getTempMax();
                    int huminity=weatherResponse.getMain().getHumidity();
                    int preasure=weatherResponse.getMain().getPressure();

                    binding.huminityTv.setText("Humidity : "+huminity+" %");
                    binding.presureTv.setText("Pressure : "+preasure+" hpa");
                    binding.minTv.setText(" Min Tem : "+minTem+" °C");
                    binding.maxTv.setText(" Max Tem : "+maxTem+" °C");

                    String descrip= weatherResponse.getWeather().get(0).getDescription();
                    binding.descriptionTv.setText(descrip);


                    Long time1=  weatherResponse.getSys().getSunrise();
                    Long time2=weatherResponse.getSys().getSunset();

                    String sunrise= WeatherUtils.convertTimeToTimeString(time1);
                    String sunset=WeatherUtils.convertTimeToTimeString(time2);

                    binding.sunriseTv.setText("Sunrise  "+sunrise);
                    binding.sunsetTv.setText("Sunset  "+sunset);

                    binding.dateTimeTv.setText(WeatherUtils.getDateTime()+" ");

                }

            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {

            }
        });



    }


}
