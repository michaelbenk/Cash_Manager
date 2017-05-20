package com.pr.se.cash_manager;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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

        TextView sumView = (TextView) this.findViewById(R.id.activity_details_text_sum);
        TextView dateView = (TextView) this.findViewById(R.id.activity_details_text_date);
        TextView categoryView = (TextView) this.findViewById(R.id.activity_details_text_category);
        TextView descriptionView = (TextView) this.findViewById(R.id.activity_details_text_description);

        sumView.setText(sum);
        dateView.setText(date);
        categoryView.setText(category);
        descriptionView.setText(description);

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
