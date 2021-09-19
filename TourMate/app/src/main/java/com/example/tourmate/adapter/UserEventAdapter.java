package com.example.tourmate.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourmate.R;
import com.example.tourmate.model_class.UserEvent;
import com.example.tourmate.databinding.UserEventListModelBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.List;

public class UserEventAdapter extends RecyclerView.Adapter<UserEventAdapter.UserEventViewHolder> {

    private Context context;
    private List<UserEvent> userEventList;

    public UserEventAdapter(Context context, List<UserEvent> userEventList) {
        this.context = context;
        this.userEventList = userEventList;
    }

    @NonNull
    @Override
    public UserEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserEventListModelBinding binding= DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.user_event_list_model,parent,false);
        return new UserEventViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserEventViewHolder holder, int position) {


        final UserEvent userEvent=userEventList.get(position);
        holder.binding.rowDestinationTv.setText(userEvent.getEventDestination()+" Tour");
        holder.binding.rowStartDateTv.setText("Starts on : "+userEvent.getEventStartDate());
        holder.binding.rowEndDateTv.setText("End date : "+userEvent.getEventEndDate());
        holder.binding.rowBudgetTv.setText("Tk: "+userEvent.getEventBudget());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String userEventId=userEvent.getEventId();
                Bundle bundle=new Bundle();
                bundle.putString("id",userEventId);
                Navigation.findNavController(view).navigate(R.id.action_eventFragment_to_userEventDetailsFragment,bundle);
            }
        });


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                 AlertDialog.Builder alertdialogBuilder;

                alertdialogBuilder=new AlertDialog.Builder(context);
                alertdialogBuilder.setTitle("Are You Sure !!");
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

                        StyleableToast.makeText(context, "Delete Successful", Toast.LENGTH_LONG, R.style.mytoast2).show();


                        FirebaseAuth  firebaseAuth = FirebaseAuth.getInstance();
                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference userRef = rootRef.child("Users");
                        String currentUserId = firebaseAuth.getUid();
                        DatabaseReference eventRef = userRef.child(currentUserId).child("Event");
                        final String userEventId=userEvent.getEventId();
                        eventRef.child(userEventId).removeValue();
                        userEventList.remove(userEvent);
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
        return userEventList.size();
    }

    public class UserEventViewHolder extends RecyclerView.ViewHolder {
        UserEventListModelBinding binding;
        public UserEventViewHolder(@NonNull UserEventListModelBinding itemView) {
            super(itemView.getRoot());
            binding=itemView;
        }
    }
}
