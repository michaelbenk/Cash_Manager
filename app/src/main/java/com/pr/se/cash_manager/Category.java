package com.pr.se.cash_manager;

import android.text.Editable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class Category implements Serializable {
    private String name;
    private List<Category> subcategories = new ArrayList<>();

    public Category(String name, boolean subcategories) {
        this.name = name;

        if(subcategories) {
            this.subcategories = new ArrayList<>();
        }
    }

    public boolean addSubCategory(Category category) {
        if (this.subcategories.contains(category)) {
            return false;
        } else if (this.subcategories != null) {
            this.subcategories.add(category);
            return true;
        }
        return false;
    }

    public List<Category> getSubCategories() {
        return this.subcategories;
    }

    public void setCategories(ArrayList<Category> subcategories) {
        this.subcategories = subcategories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Category)
            return this.name.equals(((Category) o).getName());
        return false;
    }
}
