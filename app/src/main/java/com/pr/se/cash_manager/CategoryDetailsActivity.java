package com.pr.se.cash_manager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CategoryDetailsActivity extends AppCompatActivity {
    private List<Category> categories;
    private Category category = null;
    private Category subCategory = null;
    private String selectedCategory;

    private EditText nameView;
    private EditText limitView;
    private Spinner categoriesView;
    private TextView saveView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_categories_det_toolbar);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.categories = RW.readCategories(this, "categories");
        this.nameView = (EditText) findViewById(R.id.activity_categories_det_input_bez);
        this.limitView = (EditText) findViewById(R.id.activity_categories_det_input_limit);
        this.categoriesView = (Spinner) findViewById(R.id.activity_categories_det_input_cat);
        this.saveView = (TextView) findViewById(R.id.activity_categories_det_save);

        final String cat = getIntent().getStringExtra("cat");
        if (cat != null) {
            for (Category c : this.categories) {
                if (c.getId().equals(cat)) {
                    this.category = c;
                    this.nameView.setText(c.getName());
                    this.limitView.setText(Double.toString(c.getLimit()));
                }
            }
        }

        final String subCat = getIntent().getStringExtra("subCat");
        if (subCat != null) {
            for (Category c : this.category.getSubCategories()) {
                if (c.getId().equals(subCat)) {
                    this.subCategory = c;
                    this.nameView.setText(c.getName());
                    this.limitView.setText(Double.toString(c.getLimit()));
                }
            }
        }

        this.setupSpinner();

        categoriesView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCategory = adapterView.getItemAtPosition(i).toString(); //selektierter Kategorienname
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub
            }
        });

        //Save Button
        saveView.setOnClickListener(new View.OnClickListener() {

            public SharedPreferences prefs;

            @Override
            public void onClick(View view) {
                
                if (checkInput()) {  //Wenn alle Eingaben korrekt sind
                    this.prefs = getSharedPreferences("com.pr.se.cash_manager", MODE_PRIVATE);
                    this.prefs.edit().putBoolean("CategorieChange", true).apply();

                    Category remove = null;
                    if (category == null && subCategory == null) {// neue Kategorie
                        if (selectedCategory == null || selectedCategory.equals(" ")) {
                            if(limitView.getText().toString().equals(""))
                                categories.add(new Category(nameView.getText().toString(), 0, true));
                            else
                                categories.add(new Category(nameView.getText().toString(), Double.parseDouble(limitView.getText().toString()), true));
                        } else {
                            Category c;
                            for (int i = 0; i < categories.size(); i++) { //Fügt die neue Kategorie unter der selektierten Kategorie ein
                                c = categories.get(i);
                                if (c.getName().equals(selectedCategory)) {
                                    if(limitView.getText().toString().equals(""))
                                        categories.get(i).addSubCategory(new Category(nameView.getText().toString(), 0, true));
                                    else
                                        categories.get(i).addSubCategory(new Category(nameView.getText().toString(), Double.parseDouble(limitView.getText().toString()), true));
                                }
                            }
                        }
                    } else if (category != null) {//Kategorie updaten
                        Category c;
                        Category s;
                        for (int i = 0; i < categories.size(); i++) {
                            c = categories.get(i);
                            if (subCategory == null && c.getId().equals(category.getId())) {
                                if (selectedCategory.equals(" ")) {
                                    categories.get(i).setName(nameView.getText().toString());
                                    categories.get(i).setLimit(Double.parseDouble(limitView.getText().toString()));
                                } else {
                                    if (c.getId().equals(category.getId())) {
                                        remove = categories.get(i);
                                    }
                                    if (c.getName().equals(selectedCategory)) {
                                        category.setName(nameView.getText().toString());
                                        category.setLimit(Double.parseDouble(limitView.getText().toString()));
                                        category.setCategories(null);
                                        categories.get(i).addSubCategory(category);
                                        // was passiert mit expenses
                                    }
                                }
                            } else if (subCategory != null){
                                if (selectedCategory.equals(" ")) {
                                    for (int j = 0; j < categories.get(i).getSubCategories().size(); j++) {
                                        s = categories.get(i).getSubCategories().get(j);
                                        if (s.getId().equals(subCategory.getId())) {
                                            categories.get(i).getSubCategories().remove(j);
                                        }
                                    }
                                } else {
                                    for (int j = 0; j < categories.get(i).getSubCategories().size(); j++) {
                                        s = categories.get(i).getSubCategories().get(j);
                                        if (s.getId().equals(subCategory.getId())) {
                                            categories.get(i).getSubCategories().remove(j);
                                        }
                                    }
                                    if (c.getName().equals(selectedCategory)) {
                                        subCategory.setName(nameView.getText().toString());
                                        subCategory.setLimit(Double.parseDouble(limitView.getText().toString()));
                                        subCategory.setCategories(null);
                                        categories.get(i).addSubCategory(subCategory);
                                    }
                                }
                            }
                        }
                    }

                    if (remove != null) {
                        Category c;
                        for (int i = 0; i < categories.size(); i++) {
                            c = categories.get(i);
                            if (c.getId().equals(remove.getId())) {
                                categories.remove(i);
                            }
                        }
                    }

                    if (subCategory != null && selectedCategory.equals(" ")) {
                        subCategory.setName(nameView.getText().toString());
                        subCategory.setLimit(Double.parseDouble(limitView.getText().toString()));
                        subCategory.setCategories(new ArrayList<Category>());
                        categories.add(subCategory);
                    }


                    RW.writeCategories(CategoryDetailsActivity.this, categories, "categories");

                    Intent intent = new Intent(CategoryDetailsActivity.this, CategoriesActivity.class);
                    startActivity(intent);

                } else {
                    nameView.setBackgroundColor(CategoryDetailsActivity.this.getResources().getColor(android.R.color.holo_green_light));
                    Toast.makeText(CategoryDetailsActivity.this, "This Category already exists!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkInput() {
        return true;
        /*String input = this.nameView.getText().toString();

        if (input == null || input.equals("") || input.equals(" ")) {
            return false;
        }

        List<String> categoryNames = new ArrayList<>();
        for (Category c : RW.readCategories(this, "categories")) {
            categoryNames.add(c.getName());
            for (Category s : c.getSubCategories()) {
                categoryNames.add(s.getName());
            }
        }

        for (String s : categoryNames) {
            if (input.equals(s)) {
                return false;
            }
        }

        return true;*/
    }

    private void setupSpinner() {
        List<String> categoryNames = new ArrayList<>();
        categoryNames.add(" "); //lassen!
        int i = 1;
        int sub = -1;
        for (Category c : this.categories) {
            if (subCategory != null) {
                categoryNames.add(c.getName());
                if (c.getName().equals(category.getName())) {
                    sub = i;
                }
                i++;
            } else if (category != null){
                if (!c.getName().equals(category.getName())) {
                    categoryNames.add(c.getName());
                }
            } else {
                categoryNames.add(c.getName());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriesView.setAdapter(adapter);
        if (sub > -1) {
            categoriesView.setSelection(sub);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
