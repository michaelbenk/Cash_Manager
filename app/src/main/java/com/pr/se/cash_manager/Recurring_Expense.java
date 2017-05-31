package com.pr.se.cash_manager;

class Recurring_Expense extends Expense {
    private String date_to;
    private String intervall;
    private String date_next;

    Recurring_Expense(double sum, String date, String category, String description, String date_to, String intervall) {
        super(sum, date, category, description);
        this.date_to = date_to;
        this.intervall = intervall;
    }

    Recurring_Expense() {
        super();
    }

    String getDate_to() {
        return date_to;
    }

     void setDate_to(String date_to) {
        this.date_to = date_to;
    }

    String getIntervall() {
        return intervall;
    }

     void setIntervall(String intervall) {
        this.intervall = intervall;
    }

    public String getDate_next() {
        return date_next;
    }

    public void setDate_next(String date_next) {
        this.date_next = date_next;
    }
}
