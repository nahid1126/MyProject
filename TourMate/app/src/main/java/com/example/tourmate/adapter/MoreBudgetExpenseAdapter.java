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

import com.example.tourmate.model_class.MoreBudgetExpense;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.List;

public class MoreBudgetExpenseAdapter extends RecyclerView.Adapter<MoreBudgetExpenseAdapter.MoreBudgetExpenseViewholder> {
    private Context context;
    private List<MoreBudgetExpense> moreBudgetExpenseList;
    private DatabaseReference addMoreBudgetRef;

    private Dialog expenseRowModelDialog;

    private String updateExpenseType;
    private String updateExpenseDes;
    private double updateExpenseAmount;
    private AlertDialog.Builder alertdialogBuilder;
    public MoreBudgetExpenseAdapter(Context context, List<MoreBudgetExpense> moreBudgetExpenseList, DatabaseReference addMoreBudgetRef) {
        this.context = context;
        this.moreBudgetExpenseList = moreBudgetExpenseList;
        this.addMoreBudgetRef = addMoreBudgetRef;
    }

    @NonNull
    @Override
    public MoreBudgetExpenseViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ExpenseRowModelBinding binding= DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.expense_row_model,parent,false);
        return new MoreBudgetExpenseViewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MoreBudgetExpenseViewholder holder, int position) {

        final MoreBudgetExpense moreBudgetExpense=moreBudgetExpenseList.get(position);

        holder.binding.rowExpenseTypeTv.setText(moreBudgetExpense.getExpenseType());
        holder.binding.rowExpenseAmountTv.setText("Tk: "+moreBudgetExpense.getExpenseAmount());
        holder.binding.rowDateTimeTv.setText(moreBudgetExpense.getDateTime());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expenseRowModelDialog=new Dialog(context);
                expenseRowModelDialog.setContentView(R.layout.expense_row_model_dialog2);
                expenseRowModelDialog.show();
                TextView displayType=expenseRowModelDialog.findViewById(R.id.expense_display_typeTV);
                TextView displayAmount=expenseRowModelDialog.findViewById(R.id.expense_display_amountTv);
                TextView displayDateTime=expenseRowModelDialog.findViewById(R.id.expense_display_dateTimeTv);
                TextView displayDesc=expenseRowModelDialog.findViewById(R.id.expense_display_desTV);
                displayType.setText(moreBudgetExpense.getExpenseType());
                displayAmount.setText("Tk: "+moreBudgetExpense.getExpenseAmount());
                displayDateTime.setText(moreBudgetExpense.getDateTime());
                displayDesc.setText(moreBudgetExpense.getExpenseDescription());
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
                        switch (menuItem.getItemId()) {

                            case R.id.updateId:
                                {


                                final String expenseId=moreBudgetExpense.getExpenseId();
                                String type=moreBudgetExpense.getExpenseType();
                                final String dateTime=moreBudgetExpense.getDateTime();
                                String descrip=moreBudgetExpense.getExpenseDescription();
                                double amount=moreBudgetExpense.getExpenseAmount();


                                final Dialog  expenseUpdateDialog=new Dialog(context);
                                expenseUpdateDialog.setContentView(R.layout.add_more_expense_dialog_update);

                                final EditText amountEdt=expenseUpdateDialog.findViewById(R.id.update_amountEDT);
                                final EditText typeEdt=expenseUpdateDialog.findViewById(R.id.update_typeEDT);
                                final EditText descripEdt=expenseUpdateDialog.findViewById(R.id.update_descripTionEDT);
                                TextView cancelBt=expenseUpdateDialog.findViewById(R.id.update_cancelBt);
                                TextView updateBt=expenseUpdateDialog.findViewById(R.id.update_updateBt);

                                amountEdt.setText(amount+"");
                                typeEdt.setText(type);
                                if(descrip!=null){
                                    descripEdt.setText(descrip);
                                }

                                expenseUpdateDialog.show();

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
                                        updateExpenseDes=descripEdt.getText().toString().trim();
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
                                            MoreBudgetExpense moreBudgetExpense=new MoreBudgetExpense(expenseId,updateExpenseType,updateExpenseDes,dateTime,updateExpenseAmount);

                                            addMoreBudgetRef.child(expenseId).setValue(moreBudgetExpense).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                        final String expenseId=moreBudgetExpense.getExpenseId();


                                        addMoreBudgetRef.child(expenseId).removeValue();

                                        moreBudgetExpenseList.remove(moreBudgetExpense);
                                        notifyDataSetChanged();

                                        StyleableToast.makeText(context, "Delete Successful ", Toast.LENGTH_LONG, R.style.mytoast2).show();

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
        });

    }

    @Override
    public int getItemCount() {
        return moreBudgetExpenseList.size();
    }


    public class MoreBudgetExpenseViewholder extends RecyclerView.ViewHolder {
        ExpenseRowModelBinding binding;
        public MoreBudgetExpenseViewholder(@NonNull ExpenseRowModelBinding itemView) {
            super(itemView.getRoot());
            binding=itemView;
        }
    }
}
