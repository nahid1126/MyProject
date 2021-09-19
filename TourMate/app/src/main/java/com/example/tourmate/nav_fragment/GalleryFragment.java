package com.example.tourmate.nav_fragment;


import android.Manifest;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tourmate.R;
import com.example.tourmate.adapter.GalleryAdapter;
import com.example.tourmate.adapter.UserEventAdapter;
import com.example.tourmate.databinding.FragmentGalleryBinding;
import com.example.tourmate.model_class.GalleryPhoto;
import com.example.tourmate.model_class.UserEvent;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends Fragment {
    private static final int CAMERA_REQUEST_CODE =333;
    private FragmentGalleryBinding binding;

   private  String eventImage;
   private  String eventImageId;
   private String currentPhotoPath;


    private GalleryAdapter adapter;
    private List<GalleryPhoto>galleryPhotoList;


    private DatabaseReference eventRef;
    private DatabaseReference currentEventRef;
    private DatabaseReference eventImageGalleryRef;
    private String eventId;

    private StorageReference storageReference;
    private StorageReference imageRef;

     ProgressDialog progressDialog;

    private Uri photoURI;


    public GalleryFragment() {
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
        // Inflate the layout for this fragment
        binding=FragmentGalleryBinding.inflate(inflater,container,false);
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
        eventImageGalleryRef=currentEventRef.child("EventImageGallery");


        getAllImages();

        refreshRecyclerView();



    }

    //.....................................................................................................................

    private void getAllImages() {

        eventImageGalleryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                galleryPhotoList=new ArrayList<>();

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
                        GalleryPhoto galleryPhoto=snapshot.getValue(GalleryPhoto.class);

                        galleryPhotoList.add(galleryPhoto);
                    }

                    if(galleryPhotoList.isEmpty()){
                        binding.simpleTv.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    }
                    else {


                        Collections.reverse(galleryPhotoList);
                        adapter=new GalleryAdapter(getActivity(),galleryPhotoList,eventImageGalleryRef);
                        GridLayoutManager manager=new GridLayoutManager(getActivity(),2);
                        binding.recyclerView.setLayoutManager(manager);
                        binding.recyclerView.setAdapter(adapter);
                       // progressDialog.dismiss();
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


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.camera_menu,menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.cameraId) {

            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                File imageFile = null;
                try {
                    imageFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (imageFile != null){
                     photoURI = FileProvider.getUriForFile(getActivity(),
                            "com.example.tourmate",
                            imageFile);

                   // eventImage=photoURI.toString();   // if we can access image in local store
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, 149);

                }
            }


            return true;
        }


        return false;
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            if(requestCode==149){


                storageReference=FirebaseStorage.getInstance().getReference();

                imageRef=storageReference.child("TourMate All Images : ").child("photo/"+ UUID.randomUUID());

                if(photoURI!=null){
                  final  ProgressDialog progressDialog2=new ProgressDialog(getActivity());
                    progressDialog2.setTitle("Please wait..........");

                    imageRef.putFile(photoURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                           imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                               @Override
                               public void onSuccess(Uri uri) {
                                   eventImage=uri.toString();
                                   eventImageId=eventImageGalleryRef.push().getKey();
                                   GalleryPhoto galleryPhoto=new GalleryPhoto(eventImageId,eventImage);

                                   eventImageGalleryRef.child(eventImageId).setValue(galleryPhoto).addOnSuccessListener(new OnSuccessListener<Void>() {
                                       @Override
                                       public void onSuccess(Void aVoid) {


                                           progressDialog2.dismiss();
                                           // StyleableToast.makeText(getActivity(), "Image save Successful !!", Toast.LENGTH_LONG, R.style.mytoast2).show();
                                       }
                                   });
                               }
                           });


                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                            double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog2.setMessage("Processing.."+(int)progress+"%");
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

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }



}
