package com.example.tourmate.place_search_direction;


import com.example.tourmate.direction.DirectionResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface DirectionService {
    @GET
    Call<DirectionResponse> getDirectionResponse(@Url String endUrl);
}
