package com.example.tourmate.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourmate.R;
import com.example.tourmate.databinding.MomentsRowModelBinding;
import com.example.tourmate.model_class.MomentsPhoto;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.muddzdev.styleabletoast.StyleableToast;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MomentsAdapter extends RecyclerView.Adapter<MomentsAdapter.MomentsViewHolder> {

    private Context context;
    private List<MomentsPhoto> momentsPhotoList;
    private DatabaseReference eventMomentsRef;

    private Dialog momentsDisplayDialog;

    private TextView commentstv;
    private EditText commentsEdt;


    public MomentsAdapter(Context context, List<MomentsPhoto> momentsPhotoList, DatabaseReference eventMomentsRef) {
        this.context = context;
        this.momentsPhotoList = momentsPhotoList;
        this.eventMomentsRef = eventMomentsRef;
    }

    @NonNull
    @Override
    public MomentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MomentsRowModelBinding binding= DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.moments_row_model,parent,false);
        momentsDisplayDialog=new Dialog(context);
        momentsDisplayDialog.setContentView(R.layout.moments_display_dialog);
        return new MomentsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MomentsViewHolder holder, int position) {

        final MomentsPhoto momentsPhoto=momentsPhotoList.get(position);

         String momentsId=momentsPhoto.getMomentsId();
         String photoLink=momentsPhoto.getMomentsImage();
         String comments=momentsPhoto.getMomentsComments();

        Picasso.get().load(photoLink)
                .fit()
                .centerInside()
                .into(holder.binding.momentsRowImage);
        holder.binding.momentsRowDescription.setText(comments);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                momentsDisplayDialog.show();
                ImageView showImage=momentsDisplayDialog.findViewById(R.id.moments_display_image);
                 commentstv=momentsDisplayDialog.findViewById(R.id.display_moments_writeCommentsTv);
                 commentsEdt=momentsDisplayDialog.findViewById(R.id.display_moments_writeCommentsEdt);
                Button deleBt=momentsDisplayDialog.findViewById(R.id.display_moment_deleteBt);
                final Button editBt=momentsDisplayDialog.findViewById(R.id.display_moment_editBt);
                final Button ookBt=momentsDisplayDialog.findViewById(R.id.display_moment_ookBt);

                String photoLink=momentsPhoto.getMomentsImage();
                final String comments1=momentsPhoto.getMomentsComments();

                Picasso.get().load(photoLink)
                        .fit()
                        .centerInside()
                        .into(showImage);


                commentstv.setText(comments1);

                commentstv.setVisibility(View.VISIBLE);
                commentsEdt.setVisibility(View.GONE);
                editBt.setVisibility(View.VISIBLE);
                ookBt.setVisibility(View.GONE);


                editBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        commentsEdt.setText(comments1);
                        commentstv.setVisibility(View.GONE);
                        commentsEdt.setVisibility(View.VISIBLE);
                        editBt.setVisibility(View.GONE);
                        ookBt.setVisibility(View.VISIBLE);


                        ookBt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String newComments=commentsEdt.getText().toString().trim();

                                if(newComments.isEmpty()){
                                    commentsEdt.requestFocus();
                                    StyleableToast.makeText(context, "Please write some comments", Toast.LENGTH_LONG, R.style.mytoast).show();
                                }
                                else {
                                    String momentsId3=momentsPhoto.getMomentsId();
                                    String photoLink3=momentsPhoto.getMomentsImage();

                                    MomentsPhoto momentsPhoto1=new MomentsPhoto(momentsId3,photoLink3,newComments);
                                    eventMomentsRef.child(momentsId3).setValue(momentsPhoto1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            commentsEdt.setText(" ");
                                            commentstv.setText(" ");
                                           momentsDisplayDialog.dismiss();

                                        }
                                    });

                                }
                            }
                        });
                    }
                });

                deleBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder alertdialogBuilder;

                        alertdialogBuilder=new AlertDialog.Builder(context);
                        alertdialogBuilder.setTitle("Are You Sure !!");
                        alertdialogBuilder.setMessage("Do you Want to Delete this Memory ?");
                        alertdialogBuilder.setIcon(R.drawable.alert3);


                        alertdialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.cancel();
                                momentsDisplayDialog.dismiss();

                            }
                        });

                        alertdialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                StyleableToast.makeText(context, "Delete Successful", Toast.LENGTH_LONG, R.style.mytoast2).show();

                                String momentsId=momentsPhoto.getMomentsId();
                                eventMomentsRef.child(momentsId).removeValue();
                                momentsPhotoList.remove(momentsPhoto);
                                notifyDataSetChanged();
                                momentsDisplayDialog.dismiss();

                            }
                        });

                        AlertDialog alertDialog=alertdialogBuilder.create();
                        alertDialog.show();



                    }
                });


            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                AlertDialog.Builder alertdialogBuilder;

                alertdialogBuilder=new AlertDialog.Builder(context);
                alertdialogBuilder.setTitle("Are You Sure ??");
                alertdialogBuilder.setMessage("Do you Want to Delete this Memory ?");
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

                        StyleableToast.makeText(context, "Delete Successful", Toast.LENGTH_LONG, R.style.mytoast2).show();


                        String momentsId=momentsPhoto.getMomentsId();
                        eventMomentsRef.child(momentsId).removeValue();
                        momentsPhotoList.remove(momentsPhoto);
                        notifyDataSetChanged();

                    }
                });

                AlertDialog alertDialog=alertdialogBuilder.create();
                alertDialog.show();

                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return momentsPhotoList.size();
    }


    public class MomentsViewHolder extends RecyclerView.ViewHolder {

        MomentsRowModelBinding binding;
        public MomentsViewHolder(@NonNull MomentsRowModelBinding itemView) {
            super(itemView.getRoot());

            binding=itemView;
        }
    }

}
