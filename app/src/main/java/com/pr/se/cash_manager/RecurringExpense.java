package com.pr.se.cash_manager;

public class RecurringExpense extends Expense {
    private String date_to;
    private String intervall;
    private String date_next;

    public RecurringExpense(double sum, String date, String category, String description, String date_to, String intervall) {
        super(sum, date, category, description);
        this.date_to = date_to;
        this.intervall = intervall;
    }

    public RecurringExpense() {
        super();
    }

    public String getDate_to() {
        return date_to;
    }

    public void setDate_to(String date_to) {
        this.date_to = date_to;
    }

    public String getIntervall() {
        return intervall;
    }

    public void setIntervall(String intervall) {
        this.intervall = intervall;
    }

    public String getDate_next() {
        return date_next;
    }

    public void setDate_next(String date_next) {
        this.date_next = date_next;
    }
}
