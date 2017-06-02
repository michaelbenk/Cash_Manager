package com.pr.se.cash_manager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ShowDetails extends AppCompatActivity {
    private List<Expense> list;
    private List<Category> cat;
    private String sum;
    private String date;
    private String category;
    private String description;
    private String dateto;
    private String intervall;
    private Bitmap image = null;
    private Expense expense;
    private String id;
    private boolean update;
    private byte[] byteArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //Toolbar setzen
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_details_toolbar);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Werte von Intent (MainActivity) übernehmen
        id = getIntent().getStringExtra("id");
        update = getIntent().getBooleanExtra("update", false);

        list = RW.readExpenses(this, "expenses");

        for (Expense e:list) {
            if (e.getId().equals(id)){
                expense = e;
                break;
            }
        }

        //Byte Array in Bitmap konvertieren
        if (expense.getImages().size() != 0)
            image = BitmapFactory.decodeByteArray(expense.getImages().get(0), 0, expense.getImages().get(0).length);

        sum = String.valueOf(expense.getSum());
        date = expense.getDate();
        category = expense.getCategory();
        description = expense.getDescription();
        if (expense.getImages().size() != 0)
            byteArray = expense.getImages().get(0);
        if (expense instanceof Recurring_Expense) {
            dateto = ((Recurring_Expense) expense).getDate_to();
            intervall = ((Recurring_Expense) expense).getIntervall();
        }
        //Byte Array in Bitmap konvertieren
        if (byteArray != null)
            image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        //Viewelemente laden
        TextView sumView = (TextView) this.findViewById(R.id.activity_details_text_sum);
        TextView dateView = (TextView) this.findViewById(R.id.activity_details_text_date);
        TextView categoryView = (TextView) this.findViewById(R.id.activity_details_text_category);
        TextView descriptionView = (TextView) this.findViewById(R.id.activity_details_text_description);
        ImageView imageView = (ImageView) this.findViewById(R.id.activity_details_image);
        TextView dateToView = (TextView) this.findViewById(R.id.activity_details_text_todate);
        TextView intervallView = (TextView) this.findViewById(R.id.activity_details_text_intervall);

        //Viewelemente mit werden aus MainActivity setzen
        sumView.setText(sum);
        dateView.setText(date);
        categoryView.setText(category);
        descriptionView.setText(description);

        if (image != null)
            imageView.setImageBitmap(image);
        if (dateto != null && intervall != null){
            this.findViewById(R.id.activity_details_todate).setVisibility(View.VISIBLE);
            this.findViewById(R.id.activity_details_intervall).setVisibility(View.VISIBLE);
            dateToView.setText(dateto);
            intervallView.setText(intervall);
        }

        //Button mit Stift Icon - Bearbeiten von Expense - Click Event
        FloatingActionButton fab = (FloatingActionButton) this.findViewById(R.id.activity_details_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowDetails.this, AddEditActivity.class);
                intent.putExtra("update", update);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
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
