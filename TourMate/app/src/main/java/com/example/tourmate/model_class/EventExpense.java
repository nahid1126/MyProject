package com.example.tourmate.model_class;

public class EventExpense {
    private String expenseId;
    private String expenseType;
    private double expenseAmount;
    private String expenseDateTime;

    public EventExpense() {
    }

    public EventExpense(String expenseId, String expenseType, double expenseAmount, String expenseDateTime) {
        this.expenseId = expenseId;
        this.expenseType = expenseType;
        this.expenseAmount = expenseAmount;
        this.expenseDateTime = expenseDateTime;
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

    public double getExpenseAmount() {
        return expenseAmount;
    }

    public void setExpenseAmount(double expenseAmount) {
        this.expenseAmount = expenseAmount;
    }

    public String getExpenseDateTime() {
        return expenseDateTime;
    }

    public void setExpenseDateTime(String expenseDateTime) {
        this.expenseDateTime = expenseDateTime;
    }
}
