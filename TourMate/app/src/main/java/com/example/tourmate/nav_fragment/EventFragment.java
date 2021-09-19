package com.example.tourmate.nav_fragment;


import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.example.tourmate.dialog_fragment.AddEventDialog;
import com.example.tourmate.R;
import com.example.tourmate.model_class.UserEvent;
import com.example.tourmate.adapter.UserEventAdapter;
import com.example.tourmate.databinding.FragmentEventBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment  {
    private FragmentEventBinding binding;
    private DatabaseReference eventRef;
    private UserEventAdapter adapter;
    private List<UserEvent>userEventList;

    private DatePicker datePicker;
    private DatePickerDialog datePickerDialog;
    public EventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
     binding=FragmentEventBinding.inflate(inflater,container,false);
     return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();

        binding.progressbar.setVisibility(View.VISIBLE);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = rootRef.child("Users");
        String currentUserId = firebaseAuth.getUid();

        eventRef= userRef.child(currentUserId).child("Event");



        binding.floatingActionBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


               // Navigation.findNavController(view).navigate(R.id.action_eventFragment_to_addEventDialogFragment);

                AddEventDialog addEventDialog=new AddEventDialog();
                addEventDialog.setTargetFragment(EventFragment.this,1234);
                addEventDialog.show(getFragmentManager(),"my custom");

            }
        });


             refreshRecyclerView();

             getEventData();






    }

    //..........................................................................................................


    private void getEventData(){
        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userEventList=new ArrayList<>();

                if(dataSnapshot!=null){

                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        UserEvent userEvent=snapshot.getValue(UserEvent.class);

                        userEventList.add(userEvent);
                    }

                    if(userEventList.isEmpty()){
                        binding.simpleTv.setVisibility(View.VISIBLE);
                        binding.progressbar.setVisibility(View.GONE);
                    }
                    else {
                        adapter=new UserEventAdapter(getActivity(),userEventList);
                        GridLayoutManager manager=new GridLayoutManager(getActivity(),1);
                        binding.recyclerView.setLayoutManager(manager);
                        binding.recyclerView.setAdapter(adapter);
                        binding.progressbar.setVisibility(View.GONE);
                        binding.simpleTv.setVisibility(View.GONE);
                        binding.refresh.setRefreshing(false);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                binding.progressbar.setVisibility(View.GONE);
                binding.simpleTv.setVisibility(View.VISIBLE);
            }
        });


    }

    private void refreshRecyclerView() {

        binding.refresh.setColorSchemeResources(R.color.colorAccent);
        binding.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(adapter!=null){
                    adapter.notifyDataSetChanged();
                    binding.refresh.setRefreshing(false);
                }

                binding.refresh.setRefreshing(false);


            }
        });
    }




    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.logout_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.signInFragment) {


            FirebaseAuth.getInstance().signOut();

            Navigation.findNavController(getView()).navigate(R.id.action_eventFragment_to_signInFragment);
            return true;
        }

        NavController controller= Navigation.findNavController(getActivity(),R.id.nav_host_fragment);
        return NavigationUI.onNavDestinationSelected(item,controller) || super.onOptionsItemSelected(item);
    }



}
