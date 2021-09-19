package com.example.tourmate.nav_fragment;


import android.app.Dialog;
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
import com.example.tourmate.adapter.MoreBudgetExpenseAdapter;
import com.example.tourmate.databinding.FragmentAddMoreBudgetBinding;
import com.example.tourmate.model_class.MoreBudgetExpense;
import com.example.tourmate.model_class.ProjectUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddMoreBudgetFragment extends Fragment {
    private FragmentAddMoreBudgetBinding binding;

    private DatabaseReference eventRef;
    private DatabaseReference currentEventRef;
    private DatabaseReference addMoreBudgetRef;
    private String eventId;


    private String expenseId;
    private String expenseType;
    private String expenseDescription;
    private double expenseAmount;



    private EditText typeEDt;
    private EditText amountEDt;
    private EditText descriptionEDt;
    private Dialog addExpenseDialog;
    private List<MoreBudgetExpense> moreBudgetExpenseList;
    private MoreBudgetExpenseAdapter adapter;

    public AddMoreBudgetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentAddMoreBudgetBinding.inflate(inflater,container,false);
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
        addMoreBudgetRef=currentEventRef.child("Event_Add_More_Budget_Expense :");


        binding.floatingActionBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addExpenseDialog=new Dialog(getActivity());
                addExpenseDialog.setContentView(R.layout.add_more_expense_dialog);

                addExpenseDialog.show();

                typeEDt=addExpenseDialog.findViewById(R.id.add_typeEDT);
                amountEDt=addExpenseDialog.findViewById(R.id.add_amountEDT);
                descriptionEDt=addExpenseDialog.findViewById(R.id.add_descripTionEDT);

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
                        expenseDescription=descriptionEDt.getText().toString().trim();
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

                            expenseId=addMoreBudgetRef.push().getKey();
                            MoreBudgetExpense moreBudgetExpense=new MoreBudgetExpense(expenseId,expenseType,expenseDescription,ProjectUtils.getDateTime(),expenseAmount);

                            addMoreBudgetRef.child(expenseId).setValue(moreBudgetExpense).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    StyleableToast.makeText(getActivity(), "Expense add Successful. ", Toast.LENGTH_LONG, R.style.mytoast2).show();
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
                });

            }
        });

        refreshRecyclerView();
        getAllExpense();

    }

    private void getAllExpense() {
        addMoreBudgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                moreBudgetExpenseList=new ArrayList<>();

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    MoreBudgetExpense moreBudgetExpense=snapshot.getValue(MoreBudgetExpense.class);

                    moreBudgetExpenseList.add(moreBudgetExpense);
                }

                if(moreBudgetExpenseList.isEmpty()){
                    binding.simpleTv.setVisibility(View.VISIBLE);
                }
                else {
                    Collections.reverse(moreBudgetExpenseList);
                    adapter=new MoreBudgetExpenseAdapter(getActivity(),moreBudgetExpenseList,addMoreBudgetRef);
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
