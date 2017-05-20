package com.pr.se.cash_manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The ListArrayAdapter acts as an bridge between the {@link ListView} and the
 * underlying data.
 * <br><br>
 * The ListArrayAdapter provides access to data items. Every component of the list displayed in a
 * content tab is filled in with this adapter, so it is necessary to pass an {@link ArrayList} with
 * all the {@link Expense} objects.
 *
 * @author Team 1
 * @version 1.0
 */
public class ListArrayAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList<Expense> list;
    private Boolean[] selectedItems;
    private boolean selected = false;
    private int selectedPos = 0;

    public ListArrayAdapter(Context context, ArrayList<Expense> list) {
        super(context, R.layout.content_main_list_element, new String[list.size()]);
        this.context = context;
        this.list = list;
        this.selectedItems = new Boolean[list.size()];
        Arrays.fill(this.selectedItems, false);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.content_main_list_element, parent, false);
        ImageView image = (ImageView) rowView.findViewById(R.id.content_main_list_image);
        TextView category = (TextView) rowView.findViewById(R.id.content_main_list_category);
        TextView date = (TextView) rowView.findViewById(R.id.content_main_list_date);
        TextView sum = (TextView) rowView.findViewById(R.id.content_main_list_sum);

        if (this.selected) {
            final CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.content_main_list_checkbox);
            checkBox.setVisibility(View.VISIBLE);
            if (position == this.selectedPos) {
                checkBox.setChecked(true);
                selectedItems[position] = checkBox.isChecked();
            }
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.setChecked(!checkBox.isChecked());
                    selectedItems[position] = checkBox.isChecked();
                }
            });
        }

        //image.setImageResource(list.get(position).getImageId());
        category.setText(list.get(position).getCategory().toString());
        date.setText(list.get(position).getDate());
        sum.setText(String.valueOf(list.get(position).getSum()));

        return rowView;
    }

    public void setSelected(boolean selected, int position) {
        this.selected = selected;
        this.selectedPos = position;
    }

    public Boolean[] getSelectedItems() {
        return this.selectedItems;
    }
}
