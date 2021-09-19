package com.example.tourmate.nav_fragment;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.MediaStore;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import android.widget.Toast;

import com.example.tourmate.R;
import com.example.tourmate.model_class.User;
import com.example.tourmate.databinding.FragmentSignUpBinding;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.muddzdev.styleabletoast.StyleableToast;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;



/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {
    private FragmentSignUpBinding binding;


    private String name,email,password;
    private String confirmPassword;
    private String gender;
    private String userimage;
    private String currentPhotoPath;
    private android.app.AlertDialog.Builder alertdialogBuilder;

    // Regestration

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private String userId;

    private Uri photoURI;
    private ProgressDialog progressDialog2;
    private ProgressDialog progressDialog3;


    private StorageReference storageReference;
    private StorageReference imageRef2;


    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       binding=FragmentSignUpBinding.inflate(inflater,container,false);
       return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();

        checkStoragePermission();


        firebaseAuth=FirebaseAuth.getInstance();
        rootRef= FirebaseDatabase.getInstance().getReference();
        userRef=rootRef.child("Users");


        binding.addBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhoto();

            }
        });
        binding.crossBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhoto();

            }
        });

        binding.signUpBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name=binding.signUpNameEdt.getText().toString().trim();
                email=binding.signUpEmailEdt.getText().toString().trim();
                password=binding.signUpPasswordEdt.getText().toString().trim();
                confirmPassword=binding.signUpConfirmPassword.getText().toString().trim();

                if(binding.maleRBT.isChecked()){
                    gender="Male";
                }
                else
                    gender="FeMale";



                if(name.isEmpty()){
                    binding.signUpNameEdt.requestFocus();
                    StyleableToast.makeText(getActivity(), "Please Enter name !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                }

                else if(name.startsWith("0") || name.startsWith("1") || name.startsWith("2") || name.startsWith("3") || name.startsWith("4")
                        || name.startsWith("5") || name.startsWith("6")|| name.startsWith("7") || name.startsWith("8") || name.startsWith("9") ){
                    binding.signUpNameEdt.requestFocus();
                    StyleableToast.makeText(getActivity(), "Invalid Name !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                }

                else if(name.startsWith(".")){
                    binding.signUpNameEdt.requestFocus();
                    StyleableToast.makeText(getActivity(), "Please Enter valid name !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                }
                else if(name.length()<4){
                    binding.signUpNameEdt.requestFocus();
                    StyleableToast.makeText(getActivity(), "Please Enter full name !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                }

                else if(email.isEmpty()){
                    binding.signUpEmailEdt.requestFocus();
                    StyleableToast.makeText(getActivity(), "Please Enter Email !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                }

                else if(email.contains(" ")){
                    binding.signUpEmailEdt.requestFocus();
                    StyleableToast.makeText(getActivity(), "Not Allow for space !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                }

                else if(!email.contains("@")){
                    binding.signUpEmailEdt.requestFocus();
                    StyleableToast.makeText(getActivity(), "Invalid Email address !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                }

                else if(!email.contains("mail")){
                    binding.signUpEmailEdt.requestFocus();
                    StyleableToast.makeText(getActivity(), "Invalid Email address !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                }

                else if(!email.endsWith(".com")){
                    binding.signUpEmailEdt.requestFocus();
                    StyleableToast.makeText(getActivity(), "Invalid Email address !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                }

                else if(password.isEmpty()){
                    binding.signUpPasswordEdt.requestFocus();
                    StyleableToast.makeText(getActivity(), "Please Enter Password !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                }
                else if(password.length()>6){
                    binding.signUpPasswordEdt.requestFocus();
                    StyleableToast.makeText(getActivity(), "Maximum 6 digit allow !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                }
                else if(password.length()<6){
                    binding.signUpPasswordEdt.requestFocus();
                    StyleableToast.makeText(getActivity(), "Minimum 6 digit  !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                }

                else if(confirmPassword.isEmpty()){
                    binding.signUpConfirmPassword.requestFocus();
                    StyleableToast.makeText(getActivity(), "Please again password for Confirm !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                }

                else if(!password.equals(confirmPassword)){
                    binding.signUpConfirmPassword.requestFocus();
                    StyleableToast.makeText(getActivity(), "Don't Match ...Please Try again !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                }

                else if(userimage==null){
                    binding.signUpConfirmPassword.requestFocus();
                    StyleableToast.makeText(getActivity(), "Please Select image !!", Toast.LENGTH_LONG, R.style.mytoast).show();
                }


                else {

                    progressDialog3=new ProgressDialog(getActivity());
                    progressDialog3.setMessage("Please wait.....");
                    progressDialog3.show();


                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                currentUser=firebaseAuth.getCurrentUser();
                                userId=currentUser.getUid();

                                userRef= userRef.child(userId);
                                DatabaseReference profileRef=userRef.child("UserProfile");
                                DatabaseReference eventRef=userRef.child("Event");
                                eventRef.setValue("all events");
                                User user=new User(userId,name,email,gender,userimage);
                                profileRef.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {


                                        progressDialog3.dismiss();
                                        Navigation.findNavController(getView()).navigate(R.id.action_signUpFragment_to_signInFragment);
                                        clear();
                                        mytoast();
                                    }
                                });



                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog3.dismiss();
                            StyleableToast.makeText(getActivity(), " "+e.getLocalizedMessage(), Toast.LENGTH_LONG, R.style.mytoast).show();
                        }
                    });

                }



            }
        });


    }

    private void clear() {

        binding.signUpNameEdt.setText("");
        binding.signUpEmailEdt.setText("");
        binding.signUpPasswordEdt.setText("");
        binding.signUpConfirmPassword.setText("");
        binding.maleRBT.isChecked();
        binding.profileImage.setImageResource(R.drawable.profile);
        binding.crossBt.setVisibility(View.GONE);
        binding.addBt.setVisibility(View.VISIBLE);
        binding.signUpNameEdt.requestFocus();

    }


    private void selectPhoto() {



        alertdialogBuilder=new android.app.AlertDialog.Builder(getActivity());
        alertdialogBuilder.setTitle("Select Photo");
        alertdialogBuilder.setMessage("Choice any Option !! ");
        alertdialogBuilder.setIcon(R.drawable.camera);


        alertdialogBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        });

        alertdialogBuilder.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               // startActivityForResult(intent,0);
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
                       // userimage=photoURI.toString();

                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                        startActivityForResult(intent, 0);

                    }
                }


                dialogInterface.dismiss();
            }
        });

        alertdialogBuilder.setPositiveButton("Gallary", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");

                startActivityForResult(intent,9);
                dialogInterface.dismiss();
            }
        });

        android.app.AlertDialog alertDialog=alertdialogBuilder.create();
        alertDialog.show();



    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {


                storageReference = FirebaseStorage.getInstance().getReference();

                imageRef2 = storageReference.child("TourMate All Images : ").child("photo/" + UUID.randomUUID());

                if (photoURI != null) {

                    progressDialog2 = new ProgressDialog(getActivity());
                    progressDialog2.setTitle("Please wait..........");

                    imageRef2.putFile(photoURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageRef2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    userimage = uri.toString();
                                    progressDialog2.dismiss();
                                    Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                                    binding.profileImage.setImageBitmap(bitmap);
                                    binding.addBt.setVisibility(View.GONE);
                                    binding.crossBt.setVisibility(View.VISIBLE);

                                }
                            });
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog2.setMessage("Processing.." + (int) progress + "%");
                            progressDialog2.show();
                        }
                    });

                }

            }
                else if (requestCode == 9) {
                    Uri uri = data.getData();
                    userimage = uri.toString();
                    binding.profileImage.setImageURI(uri);
                    binding.addBt.setVisibility(View.GONE);
                    binding.crossBt.setVisibility(View.VISIBLE);


                }



        }
    }


    private void mytoast() {
        LinearLayout linearLayout;
        LayoutInflater inflater=getLayoutInflater();

       // View view=inflater.inflate(R.layout.my_toast3,(getView()findViewById(R.id.mytoast));
        View view = inflater.inflate(R.layout.my_toast3, null, false);
        view.findViewById(R.id.mytoastId);

        Toast toast=new Toast(getActivity());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setView(view);
        toast.show();

    }

    private boolean checkStoragePermission(){
        if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},111);
            return false;
        }
        return true;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
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
