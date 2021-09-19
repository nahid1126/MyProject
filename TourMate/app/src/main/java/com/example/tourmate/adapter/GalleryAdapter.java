package com.example.tourmate.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourmate.R;
import com.example.tourmate.databinding.GalleryModelDesignBinding;
import com.example.tourmate.model_class.GalleryPhoto;

import com.google.firebase.database.DatabaseReference;
import com.muddzdev.styleabletoast.StyleableToast;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {
    private Context context;
    private List<GalleryPhoto> galleryPhotoList;
    private DatabaseReference eventImageGalleryRef;
    private Dialog myDialog;

    private AlertDialog.Builder alertdialogBuilder;

    public GalleryAdapter(Context context, List<GalleryPhoto> galleryPhotoList, DatabaseReference eventImageGalleryRef) {
        this.context = context;
        this.galleryPhotoList = galleryPhotoList;
        this.eventImageGalleryRef = eventImageGalleryRef;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GalleryModelDesignBinding binding= DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.gallery_model_design,parent,false);
        myDialog=new Dialog(context);
        myDialog.setContentView(R.layout.image_show_dialog);
        return new GalleryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {

        final GalleryPhoto galleryPhoto=galleryPhotoList.get(position);
        String photoId=galleryPhoto.getGalleryPhotoId();
        final String photoLink=galleryPhoto.getGalleryPhotoLink();

        Picasso.get().load(photoLink)
                .fit()
                .centerInside()
                .into(holder.binding.galleryImage);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               myDialog.show();
                ImageView showImage=myDialog.findViewById(R.id.showImageDisplayId);
                TextView deleteTv=myDialog.findViewById(R.id.deleteImageDisplayBt);

                Picasso.get().load(photoLink)
                        .fit()
                        .centerInside()
                        .into(showImage);

                deleteTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder alertdialogBuilder;

                        alertdialogBuilder=new AlertDialog.Builder(context);
                        alertdialogBuilder.setTitle("Are You Sure ??");
                        alertdialogBuilder.setMessage("Do you Want to Delete this Photo ?");
                        alertdialogBuilder.setIcon(R.drawable.alert3);


                        alertdialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.cancel();
                                myDialog.dismiss();

                            }
                        });

                        alertdialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                StyleableToast.makeText(context, "Delete Successful", Toast.LENGTH_LONG, R.style.mytoast2).show();


                                final String imageId=galleryPhoto.getGalleryPhotoId();
                                eventImageGalleryRef.child(imageId).removeValue();
                                galleryPhotoList.remove(galleryPhoto);
                                myDialog.dismiss();
                                notifyDataSetChanged();

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
                alertdialogBuilder.setTitle("Are You Sure !!");
                alertdialogBuilder.setMessage("Do you Want to Delete this Photo ?");
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


                        final String imageId=galleryPhoto.getGalleryPhotoId();
                        eventImageGalleryRef.child(imageId).removeValue();
                        galleryPhotoList.remove(galleryPhoto);
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
        return galleryPhotoList.size();
    }


    public class GalleryViewHolder extends RecyclerView.ViewHolder {
        GalleryModelDesignBinding binding;

        public GalleryViewHolder(@NonNull GalleryModelDesignBinding itemView) {
            super(itemView.getRoot());

            binding=itemView;
        }
    }





}
