package com.pr.se.cash_manager;

import android.content.Context;

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

/**
 * Created by Uni on 22.03.2017.
 */

public class RW {

    public static List<Expense> readExpenses(Context context, String file) {
        FileInputStream fis;
        List<Expense> list = new ArrayList<>();
        try {
            fis = context.openFileInput(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            list = (List<Expense>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static void writeExpenses(Context context, List<Expense> list, String file) {
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
            e.printStackTrace();
        }
    }

    public static List<Category> readCategories(Context context, String file) {
        FileInputStream fis;
        List<Category> list = new ArrayList<>();
        try {
            fis = context.openFileInput(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            list = (List<Category>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static void writeCategories(Context context, List<Category> list, String file) {
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
            e.printStackTrace();
        }
    }

    public static List<Filter> readFilter(Context context, String file) {
        FileInputStream fis;
        List<Filter> list = new ArrayList<>();
        try {
            fis = context.openFileInput(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            list = (List<Filter>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static void writeFilter(Context context, List<Filter> list, String file) {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(file, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(list);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
