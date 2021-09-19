package com.example.tourmate.dialog_fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.tourmate.R;
import com.example.tourmate.databinding.UpdateEventDialogCustomBinding;
import com.example.tourmate.model_class.GalleryPhoto;
import com.example.tourmate.model_class.MomentsPhoto;
import com.example.tourmate.model_class.MoreBudgetExpense;
import com.example.tourmate.model_class.UserEvent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.muddzdev.styleabletoast.StyleableToast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UpdateEventDialog extends DialogFragment {
    private UpdateEventDialogCustomBinding binding;



    private DatePicker datePicker;
    private DatePickerDialog datePickerDialog;

    private String eventId;
    private String eventName;
    private String eventStartLocation;
    private String eventDestination;
    private String eventStartDate;
    private String eventEndDate;
    private String eventBudget;
    private double totalExpenseDouble;
    private List<GalleryPhoto> galleryPhotoList;
    private List<MomentsPhoto> momentsPhotoList;
    private List<MoreBudgetExpense> moreBudgetExpenseList;
    private DatabaseReference eventRef;


    public UpdateEventDialog(String eventId, String eventName, String eventStartLocation, String eventDestination, String eventStartDate, String eventEndDate, String eventBudget, double totalExpenseDouble, List<GalleryPhoto> galleryPhotoList, List<MomentsPhoto> momentsPhotoList, List<MoreBudgetExpense> moreBudgetExpenseList) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventStartLocation = eventStartLocation;
        this.eventDestination = eventDestination;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.eventBudget = eventBudget;
        this.totalExpenseDouble = totalExpenseDouble;
        this.galleryPhotoList = galleryPhotoList;
        this.momentsPhotoList = momentsPhotoList;
        this.moreBudgetExpenseList = moreBudgetExpenseList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=UpdateEventDialogCustomBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dataSet();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = rootRef.child("Users");
        String currentUserId = firebaseAuth.getCurrentUser().getUid();
        eventRef= userRef.child(currentUserId).child("Event");


        binding.startDateEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar=Calendar.getInstance();

                final int year=calendar.get(Calendar.YEAR);
                final int month=calendar.get(Calendar.MONTH);
                final int day=calendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                        String currentDate=( day+"/"+ (month+1)+"/"+year);
                        DateFormat dformat=new SimpleDateFormat("dd/MM/yyyy");

                        Date date=null;


                        try {
                            date=dformat.parse(currentDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                        binding.startDateEdt.setText(dformat.format(date));



                    }
                }, year, month, day);

                calendar.add(Calendar.DATE, 0);
                Date newDate=calendar.getTime();
                datePickerDialog.getDatePicker().setMinDate(newDate.getTime()-(newDate.getTime()%(24*60*60*1000)));
                datePickerDialog.show();
            }
        });

        binding.endDateEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar=Calendar.getInstance();

                final int year=calendar.get(Calendar.YEAR);
                final int month=calendar.get(Calendar.MONTH);
                final int day=calendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                        String currentDate=( day+"/"+ (month+1)+"/"+year);
                        DateFormat dformat=new SimpleDateFormat("dd/MM/yyyy");

                        Date date=null;


                        try {
                            date=dformat.parse(currentDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                        binding.endDateEdt.setText(dformat.format(date));



                    }
                }, year, month, day);

                calendar.add(Calendar.DATE, 0);
                Date newDate=calendar.getTime();
                datePickerDialog.getDatePicker().setMinDate(newDate.getTime()-(newDate.getTime()%(24*60*60*1000)));
                datePickerDialog.show();

            }
        });


        binding.updateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                eventName = binding.eventNameEdt.getText().toString().trim();
                eventStartLocation = binding.startLocationEdt.getText().toString().trim();
                eventDestination = binding.destinationEdt.getText().toString().trim();
                eventStartDate = binding.startDateEdt.getText().toString().trim();
                eventEndDate = binding.endDateEdt.getText().toString().trim();
                eventBudget = binding.budgetEdt.getText().toString().trim();


                if(totalExpenseDouble>0){
                    StyleableToast.makeText(getActivity(), "Sorry Not Updated...You Already attend "+eventDestination+" Tour and You Already save some expense !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                }
                else if(galleryPhotoList.size()>0){
                    StyleableToast.makeText(getActivity(), "Sorry Not Updated...You Already attend "+eventDestination+" Tour and You Already save some photo !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                }
                else if(momentsPhotoList.size()>0){
                    StyleableToast.makeText(getActivity(), "Sorry Not Updated...You Already attend "+eventDestination+" Tour and You Already save some Memory !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                }
                else if(moreBudgetExpenseList.size()>0){
                    StyleableToast.makeText(getActivity(), "Sorry Not Updated...You Already attend "+eventDestination+" Tour and You Already save some expense !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                }
                else {
                    if (eventName.isEmpty()) {
                        binding.eventNameEdt.requestFocus();
                        StyleableToast.makeText(getActivity(), "Please Enter event name !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                    } else if (eventName.length() < 2) {
                        binding.eventNameEdt.requestFocus();
                        StyleableToast.makeText(getActivity(), "Please valid event name !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                    } else if (eventName.startsWith(".")) {
                        binding.eventNameEdt.requestFocus();
                        StyleableToast.makeText(getActivity(), "Please valid event name !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                    } else if (eventStartLocation.isEmpty()) {
                        binding.startLocationEdt.requestFocus();
                        StyleableToast.makeText(getActivity(), "Please Enter Starting Location !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                    } else if (eventDestination.isEmpty()) {
                        binding.destinationEdt.requestFocus();
                        StyleableToast.makeText(getActivity(), "Please Enter Your Destination !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                    } else if (eventStartDate.isEmpty()) {
                        binding.startDateEdt.requestFocus();
                        StyleableToast.makeText(getActivity(), "Please Enter Event start Date !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                    } else if (eventEndDate.isEmpty()) {
                        binding.endDateEdt.requestFocus();
                        StyleableToast.makeText(getActivity(), "Please Enter Event End Date !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                    } else if (eventBudget.isEmpty()) {
                        binding.budgetEdt.requestFocus();
                        StyleableToast.makeText(getActivity(), "Please Enter your Budget !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                    } else if (eventBudget.startsWith(".")) {
                        binding.budgetEdt.requestFocus();
                        StyleableToast.makeText(getActivity(), "Please Enter valid Budget !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                    } else if (eventBudget.contains(" ")) {
                        binding.budgetEdt.requestFocus();
                        StyleableToast.makeText(getActivity(), "Please Enter valid Budget !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                    }

                    else {
                        binding.progressbar.setVisibility(View.VISIBLE);
                        UserEvent userEvent = new UserEvent(eventId, eventName, eventStartLocation, eventDestination, eventStartDate, eventEndDate, eventBudget);
                        eventRef.child(eventId).setValue(userEvent).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                StyleableToast.makeText(getActivity(), "Event Update Successful", Toast.LENGTH_LONG, R.style.mytoast2).show();
                                getDialog().dismiss();
                                binding.progressbar.setVisibility(View.GONE);


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                getDialog().dismiss();
                                binding.progressbar.setVisibility(View.GONE);
                            }
                        });


                    }


                }







            }
        });


        binding.cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

    }

    private void dataSet() {

        binding.eventNameEdt.setText(eventName);
        binding.startLocationEdt.setText(eventStartLocation);
        binding.destinationEdt.setText(eventDestination);
        binding.startDateEdt.setText(eventStartDate);
        binding.endDateEdt.setText(eventEndDate);
        binding.budgetEdt.setText(eventBudget);
    }
}
