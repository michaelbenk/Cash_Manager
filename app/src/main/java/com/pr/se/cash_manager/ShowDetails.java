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
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

public class ShowDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //Toolbar setzen
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_details_toolbar);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Werte von Intent (MainActivity) übernehmen
        final String id = getIntent().getStringExtra("id");
        final boolean update = getIntent().getBooleanExtra("update", false);
        final String sum = getIntent().getStringExtra("sum");
        final String date = getIntent().getStringExtra("date");
        final String category = getIntent().getStringExtra("category");
        final String description = getIntent().getStringExtra("description");
        final byte[] byteArray = getIntent().getByteArrayExtra("image");
        final String dateto = getIntent().getStringExtra("dateto");
        final String intervall = getIntent().getStringExtra("intervall");
        Bitmap image = null;
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
                intent.putExtra("category", category);
                intent.putExtra("description", description);
                intent.putExtra("date", date);
                intent.putExtra("sum", sum);
                intent.putExtra("update", update);
                intent.putExtra("id", id);
                if (byteArray != null)
                    intent.putExtra("image", byteArray);
                if (dateto != null && intervall != null){
                    intent.putExtra("dateto", dateto);
                    intent.putExtra("intervall", intervall);
                }
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
