package com.example.tourmate.nav_fragment;


import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.tourmate.R;

import com.example.tourmate.adapter.MomentsAdapter;
import com.example.tourmate.databinding.FragmentViewAllMomentsBinding;

import com.example.tourmate.model_class.MomentsPhoto;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewAllMomentsFragment extends Fragment {
    private FragmentViewAllMomentsBinding binding;

    private DatabaseReference eventRef;
    private DatabaseReference currentEventRef;
    private DatabaseReference eventMomentsRef;
    private String eventId;

    private StorageReference imageRef;
    private Uri photoURI;
    private String currentPhotoPath;

    private String momentsId;
    private String momentsImage;
    private String momentsComments;


    private ImageView showImage;
    private EditText writeEdt;
    private TextView cancelBt;
    private TextView saveBt;

    private Dialog momentsDialog;


    private List<MomentsPhoto> momentsPhotoList;
    private MomentsAdapter adapter;

    private ProgressDialog progressDialog;

    public ViewAllMomentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       binding=FragmentViewAllMomentsBinding.inflate(inflater,container,false);
       return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();

        checkStoragePermission();
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait....");
        progressDialog.show();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = rootRef.child("Users");
        String currentUserId = firebaseAuth.getUid();
        eventRef= userRef.child(currentUserId).child("Event");

        eventId=getArguments().getString("id");

        if(eventId!=null){
            currentEventRef=eventRef.child(eventId);
        }
        eventMomentsRef=currentEventRef.child("Event_All_Moments");




        binding.floatingActionBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getCameraPhoto();
            }
        });



        getAllMoments();

        refreshRecyclerView();


    }

//................................................................................................

    private void getAllMoments() {

        eventMomentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                momentsPhotoList=new ArrayList<>();

                if(dataSnapshot!=null){

                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            progressDialog.dismiss();
                            //progressDialog.setProgress(0);
                        }
                    },2000);

                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        MomentsPhoto momentsPhoto=snapshot.getValue(MomentsPhoto.class);

                        momentsPhotoList.add(momentsPhoto);
                    }

                    if(momentsPhotoList.isEmpty()){
                        binding.simpleTv.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    }
                    else {


                        Collections.reverse(momentsPhotoList);
                        adapter=new MomentsAdapter(getActivity(),momentsPhotoList,eventMomentsRef);
                        GridLayoutManager manager=new GridLayoutManager(getActivity(),1);
                        binding.recyclerView.setLayoutManager(manager);
                        binding.recyclerView.setAdapter(adapter);
                        binding.simpleTv.setVisibility(View.GONE);
                        binding.refresh.setRefreshing(false);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
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

                // eventImage=photoURI.toString();   // if we can access image in local store
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, 160);

            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            if(requestCode==160){
                momentsDialog=new Dialog(getActivity());
                momentsDialog.setContentView(R.layout.moments_dialog);

                momentsDialog.show();
                showImage=momentsDialog.findViewById(R.id.showImageCameraPhoto);
                writeEdt=momentsDialog.findViewById(R.id.writeComments);
                 cancelBt=momentsDialog.findViewById(R.id.cancelBt);
                 saveBt=momentsDialog.findViewById(R.id.saveBt);


                Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                showImage.setImageBitmap(bitmap);

                saveBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        momentsComments=writeEdt.getText().toString().trim();

                        if(momentsComments.isEmpty()){
                            writeEdt.requestFocus();
                            StyleableToast.makeText(getActivity(), "Please Write some comments !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                        }

                        else {

                            StorageReference storageReference= FirebaseStorage.getInstance().getReference();
                            imageRef=storageReference.child("TourMate All Images : ").child("photo/"+ UUID.randomUUID());

                            if(photoURI!=null){
                                final ProgressDialog progressDialog2=new ProgressDialog(getActivity());
                                progressDialog2.setTitle("Please wait..........");


                                imageRef.putFile(photoURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                momentsImage=uri.toString();

                                                momentsId=eventMomentsRef.push().getKey();
                                                MomentsPhoto momentsPhoto=new MomentsPhoto(momentsId,momentsImage,momentsComments);
                                                eventMomentsRef.child(momentsId).setValue(momentsPhoto).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        writeEdt.setText(" ");
                                                        progressDialog2.dismiss();
                                                        momentsDialog.dismiss();

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
                });


                cancelBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        momentsDialog.dismiss();

                    }
                });

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

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}

