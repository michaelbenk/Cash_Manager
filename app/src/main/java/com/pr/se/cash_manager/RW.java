package com.pr.se.cash_manager;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Uni on 22.03.2017.
 */

public class RW {

    public static ArrayList<Expense> readExpenses(Context context, String file) {
        FileInputStream fis;
        ArrayList<Expense> list = new ArrayList<>();
        try {
            fis = context.openFileInput(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            list = (ArrayList<Expense>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static void writeExpenses(Context context, ArrayList<Expense> list, String file) {
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

    public static ArrayList<Category> readCategories(Context context, String file) {
        FileInputStream fis;
        ArrayList<Category> list = new ArrayList<>();
        try {
            fis = context.openFileInput(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            list = (ArrayList<Category>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static void writeCategories(Context context, ArrayList<Category> list, String file) {
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
