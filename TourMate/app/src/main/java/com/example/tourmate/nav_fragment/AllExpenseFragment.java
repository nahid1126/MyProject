package com.example.tourmate.nav_fragment;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tourmate.R;
import com.example.tourmate.adapter.ExpenseAdapter;
import com.example.tourmate.adapter.MomentsAdapter;
import com.example.tourmate.adapter.UserEventAdapter;
import com.example.tourmate.databinding.FragmentAllExpenseBinding;
import com.example.tourmate.model_class.EventExpense;
import com.example.tourmate.model_class.MomentsPhoto;
import com.example.tourmate.model_class.ProjectUtils;
import com.example.tourmate.model_class.TotalExpense;
import com.example.tourmate.model_class.UserEvent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoast.StyleableToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllExpenseFragment extends Fragment {
    private FragmentAllExpenseBinding binding;

    private DatabaseReference eventRef;
    private DatabaseReference currentEventRef;
    private DatabaseReference totalExpenseRef;
    private DatabaseReference expenseRef;
    private String eventId;

    private double totalExpenseDouble=0;
    private double budget=0;


    private Dialog addExpenseDialog;

    private String expenseId;
    private String expenseType;
    private double expenseAmount;

    private List<EventExpense> eventExpenseList;
    private ExpenseAdapter adapter;




    private EditText typeEDt;
    private EditText amountEDt;
    public AllExpenseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       binding=FragmentAllExpenseBinding.inflate(inflater,container,false);
       return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = rootRef.child("Users");
        String currentUserId = firebaseAuth.getUid();
        eventRef= userRef.child(currentUserId).child("Event");

        eventId=getArguments().getString("id");

        if(eventId!=null){
            currentEventRef=eventRef.child(eventId);
        }
        expenseRef=currentEventRef.child("Event_All_Expense :");
        totalExpenseRef=currentEventRef.child("Total Expense Amount:");


        totalExpenseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                TotalExpense totalExpense=dataSnapshot.getValue(TotalExpense.class);

                if(totalExpense!=null){
                    totalExpenseDouble=totalExpense.getTotal_expense();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        currentEventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    UserEvent userEvent =dataSnapshot.getValue(UserEvent.class);

                    if(userEvent!=null){

                       String eventBudget=userEvent.getEventBudget();
                       budget=Double.parseDouble(eventBudget);

                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        binding.floatingActionBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addExpenseDialog=new Dialog(getActivity());
                addExpenseDialog.setContentView(R.layout.add_expense_dialog);

                addExpenseDialog.show();

                 typeEDt=addExpenseDialog.findViewById(R.id.add_typeEDT);
                 amountEDt=addExpenseDialog.findViewById(R.id.add_amountEDT);

                TextView cancelBt=addExpenseDialog.findViewById(R.id.add_cancelBt);
                TextView addBt=addExpenseDialog.findViewById(R.id.add_addBt);

                cancelBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addExpenseDialog.dismiss();
                    }
                });


                addBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        expenseType=typeEDt.getText().toString().trim();
                        String expenseAmountSt=amountEDt.getText().toString().trim();

                        if(expenseAmountSt.isEmpty()){
                            amountEDt.requestFocus();
                            StyleableToast.makeText(getActivity(), "Please Enter Expense amount !", Toast.LENGTH_LONG, R.style.mytoast).show();
                        }

                      else if(expenseAmountSt.startsWith(".")){
                            amountEDt.requestFocus();
                            StyleableToast.makeText(getActivity(), "Please Enter valid amount !", Toast.LENGTH_LONG, R.style.mytoast).show();
                        }

                        else if(expenseType.isEmpty()){
                            typeEDt.requestFocus();
                            StyleableToast.makeText(getActivity(), "Please Enter Expense type !", Toast.LENGTH_LONG, R.style.mytoast).show();
                        }

                        else {



                            expenseAmount=Double.parseDouble(expenseAmountSt);
                            double simpleAmount=totalExpenseDouble+expenseAmount;
                            if(simpleAmount>budget){
                                amountEDt.requestFocus();
                                StyleableToast.makeText(getActivity(), "Sorry Your expense is over Budget ! So you can use Add More Budget.", Toast.LENGTH_LONG, R.style.mytoast).show();
                            }

                            else {

                                totalExpenseDouble=totalExpenseDouble+expenseAmount;
                                TotalExpense totalExpense=new TotalExpense(totalExpenseDouble);

                                totalExpenseRef.setValue(totalExpense).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });

                                expenseId=expenseRef.push().getKey();
                                EventExpense eventExpense=new EventExpense(expenseId,expenseType,expenseAmount,ProjectUtils.getDateTime());

                                expenseRef.child(expenseId).setValue(eventExpense).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                      StyleableToast.makeText(getActivity(), "Your Total Expense is = "+totalExpenseDouble, Toast.LENGTH_LONG, R.style.mytoast2).show();
                                        addExpenseDialog.dismiss();


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        addExpenseDialog.dismiss();
                                    }
                                });


                            }
                        }
                    }
                });





            }
        });


        refreshRecyclerView();
        getAllExpense();

    }

    //.........................................................................................................................................

    private void getAllExpense() {

        expenseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                eventExpenseList=new ArrayList<>();

                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        EventExpense eventExpense=snapshot.getValue(EventExpense.class);

                        eventExpenseList.add(eventExpense);
                    }

                    if(eventExpenseList.isEmpty()){
                        binding.simpleTv.setVisibility(View.VISIBLE);
                    }
                    else {
                        Collections.reverse(eventExpenseList);
                       // Toast.makeText(getActivity(), ""+totalExpenseDouble, Toast.LENGTH_SHORT).show();
                        adapter=new ExpenseAdapter(getActivity(),eventExpenseList,expenseRef,totalExpenseRef,budget,totalExpenseDouble);
                        GridLayoutManager manager=new GridLayoutManager(getActivity(),1);
                        binding.recyclerView.setLayoutManager(manager);
                        binding.recyclerView.setAdapter(adapter);
                        binding.simpleTv.setVisibility(View.GONE);
                        binding.refresh.setRefreshing(false);

                    }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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




}
