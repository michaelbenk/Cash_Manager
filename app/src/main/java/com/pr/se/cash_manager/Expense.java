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
public class Expense implements Serializable{

    final String id = UUID.randomUUID().toString();
    private String category;
    private String description;
    private String date;
    private double sum;

    private List<Image> pics;
    private List<byte[]> images = new ArrayList<>();

    public Expense(double sum, String date, String category, String description) {
        this.category = category;
        this.description = description;
        this.date = date;
        this.sum = sum;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getSum() {
        return this.sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public void addPic(Image image) {
        this.pics.add(image);
    }

    public void addImage(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        images.add(stream.toByteArray());
    }

    public  void deleteImage(int index){
        images.remove(index);
    }

    public List<byte[]> getImages() {
        return images;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Expense)) {
            return false;
        }

        return this.id.equals(((Expense) o).getId());
    }
}
