package com.example.tourmate.model_class;

public class MoreBudgetExpense {
    private String expenseId;
    private String expenseType;
    private String expenseDescription;
    private String dateTime;
    private double expenseAmount;

    public MoreBudgetExpense() {
    }

    public MoreBudgetExpense(String expenseId, String expenseType, String expenseDescription, String dateTime, double expenseAmount) {
        this.expenseId = expenseId;
        this.expenseType = expenseType;
        this.expenseDescription = expenseDescription;
        this.dateTime = dateTime;
        this.expenseAmount = expenseAmount;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
    }

    public String getExpenseDescription() {
        return expenseDescription;
    }

    public void setExpenseDescription(String expenseDescription) {
        this.expenseDescription = expenseDescription;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public double getExpenseAmount() {
        return expenseAmount;
    }

    public void setExpenseAmount(double expenseAmount) {
        this.expenseAmount = expenseAmount;
    }
}
