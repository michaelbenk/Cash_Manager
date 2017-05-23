package com.pr.se.cash_manager;

import android.text.Editable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class Category implements Serializable {
    private String name;
    private List<Category> subcategories = new ArrayList<>();
    private List<Category> allcategories = new ArrayList<>();

    Category(String name, boolean subcategories) {
        this.name = name;

        if(subcategories) {
            this.subcategories = new ArrayList<>();
        }
    }

    boolean addSubCategory(Category category) {
        //TODO überprüfung ob categorie schon existiert
        if (this.subcategories != null) {
            this.subcategories.add(category);
            return true;
        }
        return false;
    }

    List<Category> getCategories() {
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

    public List<Category> getAllCategories(List<Category> categories){
        categories.add(this);
        for (Category c: this.getCategories()) {
            c.getAllCategories(categories);
        }
        return categories;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Category)
            return this.name.equals(((Category)o).getName());
        return false;
    }

    public List<Category> addToList(Category selectedCategorie, List<Category> categories) {
        //TOdo testen und überdenken
        for (Category c: this.getCategories()) {
            if (c.equals(selectedCategorie)){
                categories.add(selectedCategorie);
                return categories;
            }
            categories.add(c);
            addToList(selectedCategorie, c.getCategories());
        }
        return categories;
    }
}
