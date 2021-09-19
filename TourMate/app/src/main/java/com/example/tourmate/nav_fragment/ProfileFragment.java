package com.example.tourmate.nav_fragment;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tourmate.model_class.User;
import com.example.tourmate.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    private DatabaseReference userProfileRef;


    ProgressDialog progressDialog;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentProfileBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait....");
        progressDialog.show();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = rootRef.child("Users");
        String currentUserId = firebaseAuth.getUid();
        userProfileRef=userRef.child(currentUserId).child("UserProfile");



        userProfileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        progressDialog.dismiss();
                        //progressDialog.setProgress(0);
                    }
                },3000);

                User user=dataSnapshot.getValue(User.class);

                String  userImage=user.getUserImage();
                String userName=user.getUserFullName();
                String userEmail=user.getUserEmail();
                String userGender=user.getUserGender();

               // Picasso.get().load(userImage).into(binding.profileImage);

                Picasso.get().load(userImage)
                        .fit()
                        .centerInside()
                        .into(binding.profileImage);

                binding.profileNameTv.setText(userName);
                binding.profileEmailTv.setText(userEmail);
                binding.profileGenderTv.setText(userGender);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                binding.progressbar.setVisibility(View.GONE);
            }
        });

    }


}
