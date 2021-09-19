package com.example.tourmate.nav_fragment;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.tourmate.R;
import com.example.tourmate.databinding.FragmentHotelBookingBinding;


/**
 * A simple {@link Fragment} subclass.
 */
public class HotelBookingFragment extends Fragment {
    private FragmentHotelBookingBinding binding;

    ProgressDialog progressDialog;

    public HotelBookingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentHotelBookingBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();

        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait....");
        progressDialog.show();
        WebSettings webSettings=binding.web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        binding.web.setWebViewClient(new WebViewClient());

        binding.web.loadUrl("https://www.agoda.com/en-in/country/bangladesh/popular-hotels.html?site_id=1830004&tag=539416b4-2dde-437c-88d5-d960f96121ed&device=c&network=g&adid=361332375060&rand=299633588247005363&expid=&adpos=1t2&url=https://www.agoda.com/en-in/country/bangladesh/popular-hotels.html?site_id=1830004&tag=539416b4-2dde-437c-88d5-d960f96121ed&gclid=CjwKCAiAob3vBRAUEiwAIbs5Tm8h_d4PH1ORK_jbjcgg2hfszopSSGwPO46aUNmyW1OYl5DPApCLDhoCSNgQAvD_BwE");


        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                progressDialog.dismiss();
                //progressDialog.setProgress(0);
            }
        },3000);

    }
}
