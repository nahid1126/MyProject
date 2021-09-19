package com.example.tourmate.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourmate.R;
import com.example.tourmate.databinding.ExpenseRowModelBinding;
import com.example.tourmate.model_class.EventExpense;
import com.example.tourmate.model_class.TotalExpense;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private Context context;
    private List<EventExpense> eventExpenseList;
    private DatabaseReference expenseRef;
    private DatabaseReference totalExpenseRef;
    private double budget;
    private double totalExpenseDouble;
    private double totalExpenseDoubleTemp;

    private Dialog expenseRowModelDialog;

    private AlertDialog.Builder alertdialogBuilder;


    private String updateExpenseType;
    private double updateExpenseAmount;


    public ExpenseAdapter(Context context, List<EventExpense> eventExpenseList, DatabaseReference expenseRef, DatabaseReference totalExpenseRef, double budget, double totalExpenseDouble) {
        this.context = context;
        this.eventExpenseList = eventExpenseList;
        this.expenseRef = expenseRef;
        this.totalExpenseRef = totalExpenseRef;
        this.budget = budget;
        this.totalExpenseDouble = totalExpenseDouble;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ExpenseRowModelBinding binding= DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.expense_row_model,parent,false);
        return new ExpenseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {

      final EventExpense eventExpense=eventExpenseList.get(position);

        holder.binding.rowExpenseTypeTv.setText(eventExpense.getExpenseType());
        holder.binding.rowDateTimeTv.setText(eventExpense.getExpenseDateTime());
        holder.binding.rowExpenseAmountTv.setText("Tk: "+eventExpense.getExpenseAmount());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expenseRowModelDialog=new Dialog(context);
                expenseRowModelDialog.setContentView(R.layout.expense_row_model_dialog);
                expenseRowModelDialog.show();
                TextView displayType=expenseRowModelDialog.findViewById(R.id.expense_display_typeTV);
                TextView displayAmount=expenseRowModelDialog.findViewById(R.id.expense_display_amountTv);
                TextView displayDateTime=expenseRowModelDialog.findViewById(R.id.expense_display_dateTimeTv);
                displayType.setText(eventExpense.getExpenseType());
                displayAmount.setText("Tk: "+eventExpense.getExpenseAmount());
                displayDateTime.setText(eventExpense.getExpenseDateTime());


            }
        });

        holder.binding.menuBtTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu=new PopupMenu(context,view);
                popupMenu.inflate(R.menu.expense_pop_up_menu);
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch (menuItem.getItemId()){

                            case R.id.updateId:
                            {

                                final String expenseId=eventExpense.getExpenseId();
                                String type=eventExpense.getExpenseType();
                                final String dateTime=eventExpense.getExpenseDateTime();
                                double amount=eventExpense.getExpenseAmount();

                                final Dialog  expenseUpdateDialog=new Dialog(context);
                                expenseUpdateDialog.setContentView(R.layout.add_expense_dialog_update);

                                final EditText amountEdt=expenseUpdateDialog.findViewById(R.id.update_amountEDT);
                                final EditText typeEdt=expenseUpdateDialog.findViewById(R.id.update_typeEDT);
                                TextView cancelBt=expenseUpdateDialog.findViewById(R.id.update_cancelBt);
                                TextView updateBt=expenseUpdateDialog.findViewById(R.id.update_updateBt);

                                amountEdt.setText(amount+"");
                                typeEdt.setText(type);
                                expenseUpdateDialog.show();

                                totalExpenseDoubleTemp=(totalExpenseDouble-amount);

                                cancelBt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        expenseUpdateDialog.dismiss();
                                    }
                                });


                                updateBt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        updateExpenseType=typeEdt.getText().toString().trim();
                                        String updateExpenseAmountSt=amountEdt.getText().toString().trim();

                                        if(updateExpenseAmountSt.isEmpty()){
                                            amountEdt.requestFocus();
                                            StyleableToast.makeText(context, "Please Enter Expense amount !", Toast.LENGTH_LONG, R.style.mytoast).show();
                                        }
                                        else if(updateExpenseAmountSt.startsWith(".")){
                                            amountEdt.requestFocus();
                                            StyleableToast.makeText(context, "Please Enter valid amount !", Toast.LENGTH_LONG, R.style.mytoast).show();
                                        }
                                        else if(updateExpenseType.isEmpty()){
                                            typeEdt.requestFocus();
                                            StyleableToast.makeText(context, "Please Enter Expense type !", Toast.LENGTH_LONG, R.style.mytoast).show();
                                        }

                                        else {
                                            updateExpenseAmount=Double.parseDouble(updateExpenseAmountSt);
                                            double simpleAmount=(totalExpenseDoubleTemp+updateExpenseAmount);
                                            if(simpleAmount>budget){
                                                amountEdt.requestFocus();
                                                StyleableToast.makeText(context, "Sorry Not Updated..Your expense is over Budget !! ", Toast.LENGTH_LONG, R.style.mytoast).show();
                                            }
                                            else {

                                                totalExpenseDouble=totalExpenseDoubleTemp+updateExpenseAmount;
                                                TotalExpense totalExpense=new TotalExpense(totalExpenseDouble);

                                                totalExpenseRef.setValue(totalExpense).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                    }
                                                });


                                                EventExpense eventExpense=new EventExpense(expenseId,updateExpenseType,updateExpenseAmount,dateTime);

                                                expenseRef.child(expenseId).setValue(eventExpense).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        StyleableToast.makeText(context, "Update Successful ", Toast.LENGTH_LONG, R.style.mytoast2).show();
                                                        expenseUpdateDialog.dismiss();


                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        expenseUpdateDialog.dismiss();

                                                    }
                                                });

                                            }




                                            }


                                        }

                                });


                                return true;
                            }

                            case R.id.deleteId:
                            {


                                alertdialogBuilder=new AlertDialog.Builder(context);
                                alertdialogBuilder.setTitle("Are You Sure !!");
                                alertdialogBuilder.setMessage("Do you Want to Delete this Expense ?");
                                alertdialogBuilder.setIcon(R.drawable.alert4);

                                alertdialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        dialogInterface.cancel();

                                    }
                                });

                                alertdialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        final String expenseId=eventExpense.getExpenseId();
                                        double amount=eventExpense.getExpenseAmount();
                                        totalExpenseDouble=totalExpenseDouble-amount;

                                        TotalExpense totalExpense=new TotalExpense(totalExpenseDouble);

                                        totalExpenseRef.setValue(totalExpense).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        });

                                        expenseRef.child(expenseId).removeValue();

                                        eventExpenseList.remove(eventExpense);
                                        notifyDataSetChanged();

                                        StyleableToast.makeText(context, "Delete Successful ", Toast.LENGTH_LONG, R.style.mytoast2).show();

                                    }
                                });

                                AlertDialog alertDialog=alertdialogBuilder.create();
                                alertDialog.show();
                                return true;
                            }

                        }



                        return true;
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return eventExpenseList.size();
    }

    public class ExpenseViewHolder extends RecyclerView.ViewHolder {

        ExpenseRowModelBinding binding;
        public ExpenseViewHolder(@NonNull ExpenseRowModelBinding itemView) {
            super(itemView.getRoot());

            binding=itemView;
        }
    }
}
