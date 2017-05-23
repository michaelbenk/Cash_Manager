package com.pr.se.cash_manager;

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
    private String category_name;
    private List<Category> categories;
    private List<Category> allCategories = new ArrayList<>();
    private Category category;
    private Category selectedCategory;
    private  Category newCategory;

    private EditText bezeichnungView;
    private Spinner categorienView;
    private CardView cardView;
    private TextView saveView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_categories_det_toolbar);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bezeichnungView = (EditText)findViewById(R.id.activity_categories_det_input_bez);
        cardView = (CardView)findViewById(R.id.activity_categories_det_cat);
        categorienView = (Spinner) findViewById(R.id.activity_categories_det_input_cat);
        saveView = (TextView) findViewById(R.id.activity_categories_det_save);

        category_name = getIntent().getStringExtra("categories_name");

        categories = RW.readCategories(this, "categories");

        allCategories = categories.get(0).getAllCategories(allCategories);

        if (category_name == null){ //neue Kategorie erstellen
            dataInputForNewCategorie();
        }else{ //vorhandene Kategorie bearbeiten
            cardView.setVisibility(View.INVISIBLE); //bei Kategorien können die Subkategorien nicht verändert werden.
            dataInputForOldCategorie();
        }

        categorienView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selcat = adapterView.getItemAtPosition(i).toString(); //selektierter Kategorienname
                for (Category c:allCategories) {
                    if (c.getName().equals(selcat)){
                        selectedCategory = c;   //selektierte Kategorie
                    }
                }
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
                if (checkInput()){  //Wenn alle Eingaben korrekt sind
                    if (category_name == null) {// neue Kategorie
                        selectedCategory.addSubCategory(newCategory);   //neue Unterkategorie von selektierter Kategorie ist die neue Kategorie

                        categories = categories.get(0).addToList(selectedCategory, new ArrayList<Category>());  //Fügt die neue Kategorie unter der selektierten Kategorie ein
                        RW.writeCategories(CategoryDetailsActivity.this, (ArrayList<Category>) categories, "categories");
                    }else {//Kategorie updaten
                        //TODO update
                        RW.writeCategories(CategoryDetailsActivity.this, (ArrayList<Category>) categories, "categories");
                    }
                }
            }


        });
    }
    private boolean checkInput() {
        newCategory.setName(bezeichnungView.getText().toString());
        if (newCategory.getName() != null || !newCategory.getName().equals(""))
            return true;

        //TODO Inputchecks für weitere Felder
        return false;
    }
    private void dataInputForOldCategorie() {
        for (Category c : allCategories){

            if (c.getName().equals(category_name))
                category = c;
        }

        bezeichnungView.setText(category.getName());
        //Todo Weitere Felder
    }

    private void dataInputForNewCategorie() {

        List<String> categoryNames = new ArrayList<>();
        for (Category c: allCategories) {
            categoryNames.add(c.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorienView.setAdapter(adapter);

        //TODO Weitere Felder
        //TODO Prüfung ob Kategorienname einmalig
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
