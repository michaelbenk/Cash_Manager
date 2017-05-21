package com.pr.se.cash_manager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_details_toolbar);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String id = getIntent().getStringExtra("id");
        final boolean update = getIntent().getBooleanExtra("update", false);

        final String sum = getIntent().getStringExtra("sum");
        final String date = getIntent().getStringExtra("date");
        final String category = getIntent().getStringExtra("category");
        final String description = getIntent().getStringExtra("description");
        final byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap image = null;
        if (byteArray != null)
            image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        TextView sumView = (TextView) this.findViewById(R.id.activity_details_text_sum);
        TextView dateView = (TextView) this.findViewById(R.id.activity_details_text_date);
        TextView categoryView = (TextView) this.findViewById(R.id.activity_details_text_category);
        TextView descriptionView = (TextView) this.findViewById(R.id.activity_details_text_description);
        ImageView imageView = (ImageView) this.findViewById(R.id.activity_details_image);

        sumView.setText(sum);
        dateView.setText(date);
        categoryView.setText(category);
        descriptionView.setText(description);
        if (image != null)
            imageView.setImageBitmap(image);

        FloatingActionButton fab = (FloatingActionButton) this.findViewById(R.id.activity_details_fab);
        final Bitmap finalImage = image;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalImage != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    finalImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                }
                Intent intent = new Intent(ShowDetails.this, AddEditActivity.class);
                intent.putExtra("category", category);
                intent.putExtra("description", description);
                intent.putExtra("date", date);
                intent.putExtra("sum", sum);
                intent.putExtra("update", update);
                intent.putExtra("id", id);
                if (finalImage != null)
                    intent.putExtra("image", byteArray);
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
