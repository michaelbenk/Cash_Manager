package com.pr.se.cash_manager;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class RW {

    private static String TAG = "RW";

    static List<Expense> readExpenses(Context context, String file) {
        FileInputStream fis;
        List<Expense> list = new ArrayList<>();
        try {
            fis = context.openFileInput(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            list = (List<Expense>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            Log.e(TAG, "Couldn't read expenses list", e);
        }

        return list;
    }

    static void writeExpenses(Context context, List<Expense> list, String file) {
        Collections.sort(list, new Comparator<Expense>() {
            @Override
            public int compare(Expense e1, Expense e2) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

                Calendar calendar1 = Calendar.getInstance();
                Calendar calendar2 = Calendar.getInstance();

                try {
                    calendar1.setTime(sdf.parse(e1.getDate()));
                    calendar2.setTime(sdf.parse(e2.getDate()));
                } catch (Exception e) {
                    //TODO Empty catch block
                }

                return calendar2.compareTo(calendar1);
            }
        });

        FileOutputStream fos;
        try {
            fos = context.openFileOutput(file, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(list);
            out.close();
        } catch (Exception e) {
            Log.e(TAG, "Couldn't write expenses list", e);
        }
    }

     static List<Category> readCategories(Context context, String file) {
        FileInputStream fis;
        List<Category> list = new ArrayList<>();
        try {
            fis = context.openFileInput(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            list = (List<Category>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            Log.e(TAG, "Couldn't read categories", e);
        }

        return list;
    }

     static void writeCategories(Context context, List<Category> list, String file) {
        Collections.sort(list, new Comparator<Category>() {
            @Override
            public int compare(Category c1, Category c2) {
                return c1.getName().compareTo(c2.getName());
            }
        });

        FileOutputStream fos;
        try {
            fos = context.openFileOutput(file, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(list);
            out.close();
        } catch (Exception e) {
            Log.e(TAG, "Couldn't write categories list", e);
        }
    }

     static List<Filter> readFilter(Context context, String file) {
        FileInputStream fis;
        List<Filter> list = new ArrayList<>();
        try {
            fis = context.openFileInput(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            list = (List<Filter>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            Log.e(TAG, "Couldn't filtered list", e);;
        }

        return list;
    }

     static void writeFilter(Context context, List<Filter> list, String file) {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(file, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(list);
            out.close();
        } catch (Exception e) {
            Log.e(TAG, "Couldn't write filtered list", e);
        }
    }
}
