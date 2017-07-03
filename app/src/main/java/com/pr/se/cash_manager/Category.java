package com.pr.se.cash_manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class Category implements Serializable {
    final String id = UUID.randomUUID().toString();
    private String name;
    private double limit;
    private double sum;
    private List<Category> subcategories = new ArrayList<>();

    public Category(String name, double limit, boolean subcategories) {
        this.name = name;
        if(limit < 0)
            this.limit = 0;
        else
            this.limit = limit;
        this.sum = 0;

        if (subcategories) {
            this.subcategories = new ArrayList<>();
        }
    }

    /**
     *
     * @param category; Adds a Category object to the subcategories list, if it isn't already in the list.
     * @return true if added successfully; false if category == null or the category is already in the list.
     */
    public boolean addSubCategory(Category category) {
        if (this.subcategories != null && !this.subcategories.contains(category) && category != null) {
            this.subcategories.add(category);
            this.sortSubCategories();
            return true;
        }
        return false;
    }

    /**
     *
     * @return List<Category>; Returns list of all categories.
     */
    public List<Category> getSubCategories() {
        return this.subcategories;
    }

    /**
     *
     * @param subcategories
     */
    public void setCategories(ArrayList<Category> subcategories) {
        this.subcategories = subcategories;
    }

    /**
     *
     * @param sum
     */
    public void addSum(double sum) { this.sum += sum; }

    public void zeroSum() { this.sum = 0; }

    public double getSum() { return this.sum; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return this.name;
    }

    private void sortSubCategories() {
        if (this.subcategories != null) {
            Collections.sort(this.subcategories, new Comparator<Category>() {
                @Override
                public int compare(Category c1, Category c2) {
                    return c1.getName().compareTo(c2.getName());
                }
            });
        }
    }
}
