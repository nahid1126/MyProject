package com.example.tourmate.place_search_direction;

import com.example.tourmate.place_search_response.PlaceSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface PlacesService {
    @GET
    Call<PlaceSearchResponse> getNearbyPlaces(@Url String endUrl);
}
