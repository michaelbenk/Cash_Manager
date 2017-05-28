package com.pr.se.cash_manager;

public class Recurring_Expense extends Expense {
    private String date_to;
    private String intervall;


    public Recurring_Expense(double sum, String date, String category, String description, String date_to, Intervall intervall) {
        super(sum, date, category, description);
        this.date_to = date_to;
        this.intervall = intervall.name();
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
}
