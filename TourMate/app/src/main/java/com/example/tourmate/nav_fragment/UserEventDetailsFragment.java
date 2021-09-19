package com.example.tourmate.nav_fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tourmate.adapter.ExpandableListAdapter;
import com.example.tourmate.R;
import com.example.tourmate.dialog_fragment.AddEventDialog;
import com.example.tourmate.dialog_fragment.UpdateEventDialog;
import com.example.tourmate.model_class.EventExpense;
import com.example.tourmate.model_class.GalleryPhoto;
import com.example.tourmate.model_class.MomentsPhoto;
import com.example.tourmate.model_class.MoreBudgetExpense;
import com.example.tourmate.model_class.ProjectUtils;
import com.example.tourmate.model_class.TotalExpense;
import com.example.tourmate.model_class.UserEvent;
import com.example.tourmate.databinding.FragmentUserEventDetailsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.muddzdev.styleabletoast.StyleableToast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserEventDetailsFragment extends Fragment {
    private FragmentUserEventDetailsBinding binding;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private String currentUserId;
    DatabaseReference eventRef;
    List<UserEvent> userEventList;
    private DatabaseReference currentEventRef;
    private DatabaseReference eventImageGalleryRef;

    private String eventId;
    private String eventName;
    private String eventStartLocation;
    private String eventDestination;
    private String eventStartDate;
    private String eventEndDate;
    private String eventBudget;

    private AlertDialog.Builder alertdialogBuilder;

   // private NavController navController;
    private List<String>listGroup;
    private HashMap<String,List<String>>listItem;

    private ExpandableListAdapter adapter;
    private int lastExpendedPosition=-1;
    private Uri photoURI;

    private StorageReference imageRef;
    private String eventImage;
    private String eventImageId;

    private DatabaseReference totalExpenseRef;
    private double totalExpenseDouble=0.0;


    private Dialog addExpenseDialog;
    private String expenseType;
    private double expenseAmount;
    private EditText typeEDt;
    private EditText amountEDt;

    private DatabaseReference expenseRef;

    private List<GalleryPhoto>galleryPhotoList;
    private List<MomentsPhoto> momentsPhotoList;
    private List<MoreBudgetExpense> moreBudgetExpenseList;

    public UserEventDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      binding=FragmentUserEventDetailsBinding.inflate(inflater,container,false);
      return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        listGroup=new ArrayList<>();
        listItem=new HashMap<>();
        adapter=new ExpandableListAdapter(getActivity(),listGroup,listItem);
        binding.expendableListView.setAdapter(adapter);
        initialListData();

         firebaseAuth = FirebaseAuth.getInstance();
         rootRef = FirebaseDatabase.getInstance().getReference();
         userRef = rootRef.child("Users");
         currentUserId = firebaseAuth.getUid();
         eventRef = userRef.child(currentUserId).child("Event");

         eventRef.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  userEventList=new ArrayList<>();

                 for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                     UserEvent userEvent=snapshot.getValue(UserEvent.class);

                     if(userEvent!=null){
                         userEventList.add(userEvent);
                     }

                 }

             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });


             eventId = getArguments().getString("id");

        if(eventId !=null){
            currentEventRef=eventRef.child(eventId);
        }


        totalExpenseRef=currentEventRef.child("Total Expense Amount:");

        expenseRef=currentEventRef.child("Event_All_Expense :");
        eventImageGalleryRef=currentEventRef.child("EventImageGallery");
        DatabaseReference eventMomentsRef = currentEventRef.child("Event_All_Moments");
        DatabaseReference addMoreBudgetRef = currentEventRef.child("Event_Add_More_Budget_Expense :");

        eventImageGalleryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                galleryPhotoList=new ArrayList<>();

                   if(dataSnapshot!=null){
                       for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                           GalleryPhoto galleryPhoto=snapshot.getValue(GalleryPhoto.class);
                           galleryPhotoList.add(galleryPhoto);

                       }
                   }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        eventMomentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                momentsPhotoList=new ArrayList<>();

                if(dataSnapshot!=null){
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        MomentsPhoto momentsPhoto=snapshot.getValue(MomentsPhoto.class);

                        momentsPhotoList.add(momentsPhoto);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addMoreBudgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null){

                    moreBudgetExpenseList=new ArrayList<>();

                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        MoreBudgetExpense moreBudgetExpense=snapshot.getValue(MoreBudgetExpense.class);

                        moreBudgetExpenseList.add(moreBudgetExpense);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

                    if(dataSnapshot!=null){
                        UserEvent userEvent =dataSnapshot.getValue(UserEvent.class);

                        if(userEvent!=null){
                            eventDestination=userEvent.getEventDestination();
                            eventName=userEvent.getEventName();
                            eventStartLocation=userEvent.getEventStartLocation();
                            eventStartDate=userEvent.getEventStartDate();
                            eventEndDate=userEvent.getEventEndDate();
                            eventBudget=userEvent.getEventBudget();
                            binding.titleId.setText(eventDestination+" Tour");
                            setEventDetailsData();
                        }


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {


                }
            });




        expendableListViewChildClick();

        binding.expendableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int i) {
                if(lastExpendedPosition !=-1 && lastExpendedPosition!=i){
                    binding.expendableListView.collapseGroup(lastExpendedPosition);
                }
                lastExpendedPosition=i;
            }
        });

        binding.arroBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Navigation.findNavController(view).navigate(R.id.action_userEventDetailsFragment_to_eventFragment);

            }
        });





    }

    //...................................................................................................

    private void setEventDetailsData() {

        binding.detailsEventNameTv.setText(eventName);
        binding.detailsEventStartLoctionTv.setText(eventStartLocation);
        binding.detailsEventDestinationTv.setText(eventDestination);
        binding.detailsEventStartDateTv.setText(eventStartDate);
        binding.detailsEventEndDateTv.setText(eventEndDate);
        binding.budgetStatusTv.setText("Budget Status "+"("+totalExpenseDouble+"/"+eventBudget+")");

        if(totalExpenseDouble>0){


            double budget=Double.parseDouble(eventBudget);

            double bag12=budget/12;
            double bag10=budget/10;
            double bag9=budget/9;
            double bag8=budget/8;
            double bag7=budget/7;
            double bag6=budget/6;
            double bag5=budget/5;
            double bag4=budget/4;
            double bag3=budget/3;
            double bag2=budget/2;
            double bag1p9=budget/1.9;
            double bag1p8=budget/1.8;
            double bag1p7=budget/1.7;
            double bag1p6=budget/1.6;
            double bag1p5=budget/1.5;
            double bag1p4=budget/1.4;
            double bag1p3=budget/1.3;
            double bag1p2=budget/1.2;
            binding.normalBudgetTv.setVisibility(View.VISIBLE);
            binding.BudgetfullTv.setVisibility(View.GONE);
            binding.BudgetfullTv2.setVisibility(View.GONE);
            binding.BudgetfullFinalTv.setVisibility(View.GONE);
            if(totalExpenseDouble<=bag12){
                binding.normalBudgetTv.setText("00");
                binding.parcenZeroTv.setText("5%");
            }

            else if(totalExpenseDouble>=bag12 && totalExpenseDouble<bag10){
                binding.normalBudgetTv.setText("0000");
                binding.parcenZeroTv.setText("7%");
            }
           else if(totalExpenseDouble>=bag10 && totalExpenseDouble<bag9){
                binding.normalBudgetTv.setText("00000");
                binding.parcenZeroTv.setText("10%");
            }

            else if(totalExpenseDouble>=bag9 && totalExpenseDouble<bag8){
                binding.normalBudgetTv.setText("000000");
                binding.parcenZeroTv.setText("15%");
            }
            else if(totalExpenseDouble>=bag8 && totalExpenseDouble<bag7){
                binding.normalBudgetTv.setText("0000000");
                binding.parcenZeroTv.setText("20%");
            }
            else if(totalExpenseDouble>=bag7 && totalExpenseDouble<bag6){
                binding.normalBudgetTv.setText("00000000");
                binding.parcenZeroTv.setText("25%");
            }
            else if(totalExpenseDouble>=bag6 && totalExpenseDouble<bag5){
                binding.normalBudgetTv.setText("0000000000");
                binding.parcenZeroTv.setText("30%");
            }
            else if(totalExpenseDouble>=bag5 && totalExpenseDouble<bag4){
                binding.normalBudgetTv.setText("00000000000");
                binding.parcenZeroTv.setText("35%");
            }
            else if(totalExpenseDouble>=bag4 && totalExpenseDouble<bag3){
                binding.normalBudgetTv.setText("000000000000");
                binding.parcenZeroTv.setText("40%");
            }
            else if(totalExpenseDouble>=bag3 && totalExpenseDouble<bag2){
                binding.normalBudgetTv.setText("000000000000l");
                binding.parcenZeroTv.setText("45%");
            }
            else if(totalExpenseDouble==bag2){
                binding.normalBudgetTv.setText("0000000000000l");
                binding.parcenZeroTv.setText("50%");
            }
            else if(totalExpenseDouble>bag2 && totalExpenseDouble<=bag1p9){
                binding.normalBudgetTv.setText("0000000000000000");
                binding.parcenZeroTv.setText("55%");
            }
            else if(totalExpenseDouble>bag1p9 && totalExpenseDouble<=bag1p8){
                binding.normalBudgetTv.setText("00000000000000000");
                binding.parcenZeroTv.setText("60%");
            }
            else if(totalExpenseDouble>bag1p8 && totalExpenseDouble<=bag1p7){
                binding.normalBudgetTv.setText("000000000000000000");
                binding.parcenZeroTv.setText("65%");
            }
            else if(totalExpenseDouble>bag1p7 && totalExpenseDouble<=bag1p6){
                binding.normalBudgetTv.setText("00000000000000000000");
                binding.parcenZeroTv.setText("70%");
            }
            else if(totalExpenseDouble>bag1p6 && totalExpenseDouble<=bag1p5){
                binding.normalBudgetTv.setText("0000000000000000000000");
                binding.parcenZeroTv.setText("75%");
            }
            else if(totalExpenseDouble>bag1p5 && totalExpenseDouble<=bag1p4){
                binding.normalBudgetTv.setText("00000000000000000000000");
                binding.parcenZeroTv.setText("80%");
            }
            else if(totalExpenseDouble>bag1p4 && totalExpenseDouble<=bag1p3){
                binding.normalBudgetTv.setText("000000000000000000000000");
                binding.parcenZeroTv.setText("85%");
            }
            else if(totalExpenseDouble>bag1p3 && totalExpenseDouble<=bag1p2){
                binding.normalBudgetTv.setVisibility(View.GONE);
                binding.BudgetfullTv.setVisibility(View.VISIBLE);
                binding.parcenZeroTv.setText("90%");
            }
            else if(totalExpenseDouble>bag1p2 && totalExpenseDouble!=budget){
                binding.normalBudgetTv.setVisibility(View.GONE);
                binding.BudgetfullTv.setVisibility(View.GONE);
                binding.BudgetfullTv2.setVisibility(View.VISIBLE);
                binding.parcenZeroTv.setText("95%");
            }

            else if(totalExpenseDouble==budget){
                binding.BudgetfullFinalTv.setVisibility(View.VISIBLE);
                binding.normalBudgetTv.setVisibility(View.GONE);
                binding.BudgetfullTv.setVisibility(View.GONE);
                binding.parcenZeroTv.setText("100%");
            }


        }


    }





    private void expendableListViewChildClick() {
        binding.expendableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {

                String childString=listItem.get(listGroup.get(i)).get(i1);
               switch (childString) {
                   case "Add New Expense":
                   {
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
                                  double budget=Double.parseDouble(eventBudget);
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

                                     String expenseId=expenseRef.push().getKey();
                                      EventExpense eventExpense=new EventExpense(expenseId,expenseType,expenseAmount, ProjectUtils.getDateTime());

                                      expenseRef.child(expenseId).setValue(eventExpense).addOnSuccessListener(new OnSuccessListener<Void>() {
                                          @Override
                                          public void onSuccess(Void aVoid) {
                                              StyleableToast.makeText(getActivity(), "Your Total Expense is "+totalExpenseDouble, Toast.LENGTH_LONG, R.style.mytoast2).show();
                                              Bundle bundle4=new Bundle();
                                              bundle4.putString("id",eventId);
                                              Navigation.findNavController(getView()).navigate(R.id.action_userEventDetailsFragment_to_allExpenseFragment,bundle4);
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


                       return true;
                   }
                   case "View All Expense":
                       Bundle bundle4=new Bundle();
                       bundle4.putString("id",eventId);
                       Navigation.findNavController(view).navigate(R.id.action_userEventDetailsFragment_to_allExpenseFragment,bundle4);
                       return true;


                   case "Add More Budget":
                       Bundle bundle9=new Bundle();
                       bundle9.putString("id",eventId);
                       Navigation.findNavController(view).navigate(R.id.action_userEventDetailsFragment_to_addMoreBudgetFragment,bundle9);
                       return true;


                   case "Take a Photo":
                       getCameraPhoto();
                       return true;


                   case "View Gallery":
                       Bundle bundle=new Bundle();
                       bundle.putString("id",eventId);
                       Navigation.findNavController(view).navigate(R.id.action_userEventDetailsFragment_to_galleryFragment,bundle);
                       return true;


                   case "View All Moments":
                       Bundle bundle3=new Bundle();
                       bundle3.putString("id",eventId);
                       Navigation.findNavController(view).navigate(R.id.action_userEventDetailsFragment_to_viewAllMomentsFragment,bundle3);
                       return true;


                   case "Edit Event":
                       UpdateEventDialog updateEventDialog = new UpdateEventDialog(eventId, eventName, eventStartLocation, eventDestination, eventStartDate, eventEndDate, eventBudget,totalExpenseDouble,galleryPhotoList,momentsPhotoList, moreBudgetExpenseList);
                       updateEventDialog.setTargetFragment(UserEventDetailsFragment.this, 1234);
                       updateEventDialog.show(getFragmentManager(), "my custom2");

                       return true;


                   case "Delete Event":
                   {
                       alertdialogBuilder=new AlertDialog.Builder(getActivity());
                       alertdialogBuilder.setTitle("Are You Sure ??");
                       alertdialogBuilder.setMessage("Do you Want to Delete this Event ?");
                       alertdialogBuilder.setIcon(R.drawable.alert3);


                       alertdialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {

                               dialogInterface.cancel();

                           }
                       });

                       alertdialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {

                               Navigation.findNavController(getView()).navigate(R.id.action_userEventDetailsFragment_to_eventFragment);
                               StyleableToast.makeText(getActivity(), "Delete Successful", Toast.LENGTH_LONG, R.style.mytoast2).show();


                             currentEventRef.removeValue();



                           }
                       });

                       AlertDialog alertDialog=alertdialogBuilder.create();
                       alertDialog.show();
                       return true;

                   }

               }

                return false;
            }
        });
    }

    private void initialListData() {


        listGroup.add(getString(R.string.Expenditure));
        listGroup.add(getString(R.string.Moments));
        listGroup.add(getString(R.string.More));

        String []array;

        List<String> list1=new ArrayList<>();
        array=getResources().getStringArray(R.array.Expenditure);
        for (String item:array) {
            list1.add(item);
        }

        List<String> list2=new ArrayList<>();
        array=getResources().getStringArray(R.array.Moments);
        for (String item:array) {
            list2.add(item);
        }

        List<String> list3=new ArrayList<>();
        array=getResources().getStringArray(R.array.More);
        for (String item:array) {
            list3.add(item);
        }

        listItem.put(listGroup.get(0),list1);
        listItem.put(listGroup.get(1),list2);
        listItem.put(listGroup.get(2),list3);

        adapter.notifyDataSetChanged();
    }


   private void getCameraPhoto() {
       checkStoragePermission();
       Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
           File imageFile = null;
           try {
               imageFile = createImageFile();
           } catch (IOException e) {
               e.printStackTrace();
           }
           if (imageFile != null) {
               photoURI = FileProvider.getUriForFile(getActivity(),
                       "com.example.tourmate",
                       imageFile);

               intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
               startActivityForResult(intent, 155);

           }
       }

   }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            if(requestCode==155){

              StorageReference storageReference= FirebaseStorage.getInstance().getReference();

                imageRef=storageReference.child("TourMate All Images : ").child("photo/"+ UUID.randomUUID());

                if(photoURI!=null){
                    Bundle bundle2=new Bundle();
                    bundle2.putString("id",eventId);
                    Navigation.findNavController(getView()).navigate(R.id.action_userEventDetailsFragment_to_galleryFragment,bundle2);
                    final ProgressDialog progressDialog2=new ProgressDialog(getActivity());
                    progressDialog2.setTitle("Please wait..........");


                    imageRef.putFile(photoURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    eventImage=uri.toString();
                                    eventImageGalleryRef=currentEventRef.child("EventImageGallery");
                                    eventImageId=eventImageGalleryRef.push().getKey();
                                    GalleryPhoto galleryPhoto=new GalleryPhoto(eventImageId,eventImage);

                                    eventImageGalleryRef.child(eventImageId).setValue(galleryPhoto).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            progressDialog2.dismiss();

                                        }
                                    });
                                }
                            });


                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                            double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog2.setMessage("Processing..."+(int)progress+"%");
                            progressDialog2.show();


                        }
                    });


                }


            }

        }


    }

    private boolean checkStoragePermission(){
        if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
            getActivity().requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},111);
            return false;
        }
        return true;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "tour" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }



}
