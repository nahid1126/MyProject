package com.example.tourmate.weather_report;

import com.example.tourmate.current_weather_response.WeatherResponse;
import com.example.tourmate.forecast_weather_response.DhakaResponse;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface WeatherServiceApi {
    @GET
    Call<WeatherResponse> getCurrentWeatherData(@Url String endUrl);

    @GET
    Call<DhakaResponse> getForecasttWeatherData(@Url String endUrl);
}
