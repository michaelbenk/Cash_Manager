package com.pr.se.cash_manager;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
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
