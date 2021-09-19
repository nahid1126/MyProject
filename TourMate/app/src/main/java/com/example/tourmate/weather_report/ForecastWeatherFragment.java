package com.example.tourmate.weather_report;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Toast;

import com.example.tourmate.adapter.UserEventAdapter;
import com.example.tourmate.databinding.FragmentForecastWeatherBinding;
import com.example.tourmate.forecast_weather_response.DhakaResponse;
import com.example.tourmate.forecast_weather_response.ForeCast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastWeatherFragment extends Fragment {
    private FragmentForecastWeatherBinding binding;

    private ForecastAdapter adapter;
    private List<ForeCast> foreCastList;

    private String units="metric";

    private Context context;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }


    public ForecastWeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentForecastWeatherBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String endUrl="forecast?q=dhaka,bangladesh&cnt=40&units=metric&appid=dfcd19c76c02e3f667646b5ce3f3dd19";
        WeatherServiceApi serviceApi=RetrofitClient.getRetrofitClient().create(WeatherServiceApi.class);
        serviceApi.getForecasttWeatherData(endUrl).enqueue(new Callback<DhakaResponse>() {
            @Override
            public void onResponse(Call<DhakaResponse> call, Response<DhakaResponse> response) {
                if(response.isSuccessful()){
                    DhakaResponse dhakaResponse=response.body();

                    foreCastList=new ArrayList<>();
                    foreCastList=dhakaResponse.getList();
                    adapter=new ForecastAdapter(context,foreCastList);
                    GridLayoutManager manager=new GridLayoutManager(context,1);
                    binding.recyclerView.setLayoutManager(manager);
                    binding.recyclerView.setAdapter(adapter);

                }

            }

            @Override
            public void onFailure(Call<DhakaResponse> call, Throwable t) {

            }
        });


    }

}
