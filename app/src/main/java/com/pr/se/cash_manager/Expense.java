package com.pr.se.cash_manager;

import android.graphics.Bitmap;
import android.media.Image;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The List Elements are the central object in which all information about cash flow is stored.
 * <br><br>
 * In this objects all information about a certain cash flow entered by the user will be stored.
 * This information will be displayed by the {@link ListArrayAdapter} which calls content_main_list_element.xml
 * and transports the filled layout to the {@link ListView} in the activity_main_tab.xml.
 *
 * @author Team 1
 * @version 1.0
 */
 class Expense implements Serializable{

    final String id = UUID.randomUUID().toString();
    private String category;
    private String description;
    private String date;
    private double sum;

    private List<byte[]> images = new ArrayList<>();

    Expense(double sum, String date, String category, String description) {
        this.category = category;
        this.description = description;
        this.date = date;
        this.sum = sum;
    }

     Expense() {
    }

     String getCategory() {
        return this.category;
    }

     void setCategory(String category) {
        this.category = category;
    }

     String getDescription() {
        return this.description;
    }

     void setDescription(String description) {
        this.description = description;
    }

     String getDate() {
        return this.date;
    }

     void setDate(String date) {
        this.date = date;
    }

     double getSum() {
        return this.sum;
    }

     void setSum(double sum) {
        this.sum = sum;
    }

     void addImage(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        images.add(stream.toByteArray());
    }

      void deleteImage(int index){
        images.remove(index);
    }

     List<byte[]> getImages() {
        return images;
    }

     String getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Expense && this.id.equals(((Expense) o).getId());
    }
}
