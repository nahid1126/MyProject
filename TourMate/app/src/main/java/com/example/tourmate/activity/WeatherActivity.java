package com.example.tourmate.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItem;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.tourmate.R;
import com.example.tourmate.databinding.ActivityWeatherBinding;
import com.example.tourmate.weather_report.CurrentWeatherFragment;
import com.example.tourmate.weather_report.ForecastWeatherFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class WeatherActivity extends AppCompatActivity {
    private ActivityWeatherBinding binding;

    private CurrentWeatherFragment currentWeatherFragment;
    private ForecastWeatherFragment forecastWeatherFragment;
    private WeatherPagerAdapter weatherPagerAdapter;

    private static final int LOCATION_REQUIRED_CODE = 111;
    private FusedLocationProviderClient providerClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_weather);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Weather Report");

        currentWeatherFragment=new CurrentWeatherFragment();
        forecastWeatherFragment=new ForecastWeatherFragment();
        weatherPagerAdapter=new WeatherPagerAdapter(getSupportFragmentManager());
        providerClient= LocationServices.getFusedLocationProviderClient(this);
        binding.viewPager.setAdapter(weatherPagerAdapter);
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));


        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Current"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("5 days Forecast"));
        binding.tabLayout.setTabTextColors(Color.GRAY,Color.WHITE);
        binding.tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });




    }

    @Override
    protected void onResume() {
        super.onResume();

        if(checkLocationPermission()){
            getDeviceLastLocation();
        }
    }



    private boolean checkLocationPermission(){
        String [] permissionn={Manifest.permission.ACCESS_FINE_LOCATION};
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,permissionn,LOCATION_REQUIRED_CODE);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==LOCATION_REQUIRED_CODE){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){

                getDeviceLastLocation();


            }
            else {
                Toast.makeText(this, "Pleae Allow permission", Toast.LENGTH_SHORT).show();
                //explain for request permission ...
            }
        }
    }

    private void getDeviceLastLocation() {
        if(checkLocationPermission()){
            providerClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location==null) {
                        Toast.makeText(WeatherActivity.this, "Not found Location", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double latitude=location.getLatitude();
                    double longitude=location.getLongitude();
                    currentWeatherFragment.updateLoction(latitude,longitude);




                }
            });
        }

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==android.R.id.home){

            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class WeatherPagerAdapter extends FragmentPagerAdapter {
        public WeatherPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return currentWeatherFragment;

                case 1:
                    return forecastWeatherFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }



}
