package com.example.tourmate.nav_fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.tourmate.R;
import com.example.tourmate.databinding.FragmentMapBinding;
import com.example.tourmate.place_search_direction.PlacesService;
import com.example.tourmate.place_search_direction.RetrofitClientMap;
import com.example.tourmate.place_search_response.PlaceSearchResponse;
import com.example.tourmate.place_search_response.Result;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.muddzdev.styleabletoast.StyleableToast;

import java.io.IOException;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class mapFragment extends Fragment implements OnMapReadyCallback {
    private FragmentMapBinding binding;
    private GoogleMap myMap;
    private FusedLocationProviderClient providerClient;
    private String placeType="";
    private double latitude;
    private  double longitude;


    public mapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentMapBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        providerClient= LocationServices.getFusedLocationProviderClient(getActivity());
        //getDeviceLastLocation();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        String [] places={"Select","atm","bank","bar","beauty_salon","bus_station","cafe","casino","department_store","doctor","funeral_home","gas_station","gym","hospital","mosque","park","parking","pharmacy","restaurant","shopping_mall","supermarket","tourist_attraction"};
        ArrayAdapter<String> adapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item,places);
        binding.spinner.setAdapter(adapter);

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0 && latitude != 0.0 && longitude != 0.0){
                    placeType = adapterView.getItemAtPosition(i).toString();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.mylocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkLocationPermission()) {
                    //myMap.setMyLocationEnabled(true);
                    getDeviceLastLocation2();
                }
            }
        });

        binding.clearBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myMap!=null){
                    myMap.clear();
                    getDeviceLastLocation();
                }

            }
        });

        binding.findBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(placeType.isEmpty()){
                    StyleableToast.makeText(getActivity(), "Please Select place type !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                }
                else {
                    getNearByPlaces();
                }



            }
        });



    }
    //................................................................................................................................

    @Override
    public void onMapReady(GoogleMap googleMap) {

        myMap = googleMap;
        myMap.getUiSettings().setZoomControlsEnabled(true);
        myMap.getUiSettings().setMyLocationButtonEnabled(false);

       if(checkLocationPermission()) {
           myMap.setMyLocationEnabled(true);
           getDeviceLastLocation();
       }

    }


    public void getNearByPlaces(){
        myMap.clear();

       String endUrl = String.format("place/nearbysearch/json?location=%f,%f&radius=1500&type=%s&key=%s",latitude,longitude,placeType,getString(R.string.place_api));

        PlacesService service = RetrofitClientMap.getClient()
                .create(PlacesService.class);
        service.getNearbyPlaces(endUrl)
                .enqueue(new Callback<PlaceSearchResponse>() {
                    @Override
                    public void onResponse(Call<PlaceSearchResponse> call, Response<PlaceSearchResponse> response) {
                        if (response.isSuccessful()){
                            PlaceSearchResponse searchResponse = response.body();
                            List<Result>placeList=searchResponse.getResults();
                            for (Result r : placeList){
                                placeMarkers(r);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<PlaceSearchResponse> call, Throwable t) {

                        Toast.makeText(getActivity(), "Not Fount..", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void placeMarkers(Result r) {
        String name = r.getName();
        double lat = r.getGeometry().getLocation().getLat();
        double lng = r.getGeometry().getLocation().getLng();
        LatLng latLng = new LatLng(lat, lng);
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f));
        myMap.addMarker(new MarkerOptions().position(latLng).title(name));
    }

    private void getDeviceLastLocation(){
        if(checkLocationPermission()){
            providerClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location!=null) {
                         latitude = location.getLatitude();
                         longitude = location.getLongitude();

                        String address=getGeoAddress(latitude,longitude);
                        LatLng myLoc=new LatLng(latitude,longitude);
                        myMap.addMarker(new MarkerOptions().position(myLoc).title(address));
                        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc,10f));

                    }
                }
            });
        }
    }


    private boolean checkLocationPermission(){
        if(getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==123 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            if(checkLocationPermission()){
                myMap.setMyLocationEnabled(true);
                getDeviceLastLocation();
            }
        }
    }
    private String getGeoAddress(double lat,double lon){
        Geocoder geocoder=new Geocoder(getActivity());

        String addressLine="";

        try {
            List<Address> addressList= geocoder.getFromLocation(lat,lon,1);
            Address address=addressList.get(0);
            addressLine=address.getAddressLine(0);

        } catch (IOException e) {
            e.printStackTrace();
        }


        return addressLine;
    }

    private void getDeviceLastLocation2(){
        if(checkLocationPermission()){
            providerClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location!=null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        String address=getGeoAddress(latitude,longitude);
                        LatLng myLoc=new LatLng(latitude,longitude);
                        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc,18f));

                    }
                }
            });
        }
    }
}
