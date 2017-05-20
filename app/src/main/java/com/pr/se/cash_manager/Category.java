package com.pr.se.cash_manager;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Uni on 31.03.2017.
 */

public class Category implements Serializable {
    private String name;
    private ArrayList<Category> subcategories = null;

    public Category(String name, boolean subcategories) {
        this.name = name;

        if(subcategories) {
            this.subcategories = new ArrayList<>();
        }
    }

    public boolean addSubCategory (Category category) {
        if (this.subcategories != null) {
            this.subcategories.add(category);
            return true;
        }
        return false;
    }

    public ArrayList<Category> getCategories () {
        return this.subcategories;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
