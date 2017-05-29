package com.pr.se.cash_manager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
    private Spinner categoriesView;
    private CardView cardView;
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
        this.cardView = (CardView) findViewById(R.id.activity_categories_det_cat);
        this.categoriesView = (Spinner) findViewById(R.id.activity_categories_det_input_cat);
        this.saveView = (TextView) findViewById(R.id.activity_categories_det_save);

        final String cat = getIntent().getStringExtra("cat");
        if (cat != null) {
            for (Category c : this.categories) {
                if (c.getName().equals(cat))
                    this.category = c;
            }
        }

        final String subCat = getIntent().getStringExtra("subCat");
        if (subCat != null) {
            for (Category c : this.category.getSubCategories()) {
                if (c.getName().equals(subCat))
                    this.category = c;
            }
        }

        if (this.category != null) { //vorhandene Kategorie bearbeiten
            cardView.setVisibility(View.INVISIBLE); //bei Kategorien können die Subkategorien nicht verändert werden.
            this.editCategory();
        } else { //neue Kategorie erstellen
            this.addCategory();
        }

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
            @Override
            public void onClick(View view) {
                if (checkInput()) {  //Wenn alle Eingaben korrekt sind
                    if (category == null && subCategory == null) {// neue Kategorie

                        if (selectedCategory == null || selectedCategory.equals(" ")) {
                            categories.add(new Category(nameView.getText().toString(), true));
                        } else {
                            Category c;
                            for (int i = 0; i < categories.size(); i++) { //Fügt die neue Kategorie unter der selektierten Kategorie ein
                                c = categories.get(i);
                                if (c.getName().equals(selectedCategory)) {
                                    categories.get(i).addSubCategory(new Category(nameView.getText().toString(), false));
                                }
                            }
                        }
                    } else if (category != null) {//Kategorie updaten
                        Category c;
                        for (int i = 0; i < categories.size(); i++) {
                            c = categories.get(i);
                            if (c.getName().equals(cat)) {
                                if (subCategory == null) {
                                    categories.get(i).setName(nameView.getText().toString());
                                } else {
                                    Category s;
                                    for (int j = 0; j < c.getSubCategories().size(); j++) {
                                        s = c.getSubCategories().get(j);
                                        if (s.getName().equals(subCat)) {
                                            categories.get(i).getSubCategories().get(j).setName(nameView.getText().toString());
                                        }
                                    }
                                }
                            }
                        }
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
        String input = this.nameView.getText().toString();

        if (input == null || input.equals("") || input.equals(" ")) {
            return false;
        }

        List<String> categoryNames = new ArrayList<>();
        for (Category c : this.categories) {
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

        return true;
    }

    private void editCategory() {
        this.nameView.setText(category.getName());
        //Todo Weitere Felder
    }

    private void addCategory() {
        List<String> categoryNames = new ArrayList<>();
        for (Category c : this.categories) {
            categoryNames.add(c.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, categoryNames);

        adapter.setDropDownViewResource(R.layout.spinner_item);
        categoriesView.setAdapter(adapter);
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
