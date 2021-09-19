package com.example.tourmate.nav_fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.tourmate.R;
import com.example.tourmate.databinding.FragmentSignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.muddzdev.styleabletoast.StyleableToast;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {
    private FragmentSignInBinding binding;

    private String loginEmail,loginPassword;
    private FirebaseAuth firebaseAuth;


    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       binding=FragmentSignInBinding.inflate(inflater,container,false);
       return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        binding.signInEmailEdt.requestFocus();

        firebaseAuth=FirebaseAuth.getInstance();
        binding.loginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginEmail=binding.signInEmailEdt.getText().toString().trim();
                loginPassword=binding.signInPasswordEdt.getText().toString().trim();

                if(loginEmail.isEmpty()){
                    binding.signInEmailEdt.requestFocus();
                    StyleableToast.makeText(getActivity(), "Please Enter Email !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                }

                else if(loginPassword.isEmpty()){
                    binding.signInPasswordEdt.requestFocus();
                    StyleableToast.makeText(getActivity(), "Please Enter Password !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                }

                else {


                    binding.signInProgressbar.setVisibility(View.VISIBLE);
                    firebaseAuth.signInWithEmailAndPassword(loginEmail,loginPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                binding.signInProgressbar.setVisibility(View.GONE);
                                Navigation.findNavController(getView()).navigate(R.id.action_signInFragment_to_eventFragment);
                                mytoast();
                                clear();

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            binding.signInProgressbar.setVisibility(View.GONE);
                            StyleableToast.makeText(getActivity(), " "+e.getLocalizedMessage(), Toast.LENGTH_LONG, R.style.mytoast).show();
                        }
                    });
                }
            }
        });


        binding.dontBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_signInFragment_to_signUpFragment);
            }
        });

    }
    private void clear() {

        binding.signInEmailEdt.setText("");
        binding.signInPasswordEdt.setText("");
       binding.signInEmailEdt.requestFocus();


    }

    private void mytoast() {
        LinearLayout linearLayout;
        LayoutInflater inflater=getLayoutInflater();
        View view = inflater.inflate(R.layout.my_toast4, null, false);
        view.findViewById(R.id.mytoastId4);

        Toast toast=new Toast(getActivity());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setView(view);
        toast.show();

    }
}
