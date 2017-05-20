package com.pr.se.cash_manager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * The TabFragment builds an tab object to work with. It builds the fragment and fills the
 * {@link ListView}.
 * <br><br>
 * <b>Warning:</b> The {@link #setAdapter(ArrayAdapter)} must be called and the adapter set,
 * otherwise the {@link ListView} won't be filled. No content will be displayed. The adapter should
 * be the custom {@link ListArrayAdapter} to match the layout.
 *
 * @author Team 1
 * @version 1.0
 */
public class TabFragment extends Fragment {
    private ArrayAdapter a;

    public void setAdapter(ArrayAdapter a) {
        this.a = a;
    }

    public ArrayAdapter getAdapter() {
        return this.a;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main_tab, container, false);
        ((ListView) view.findViewById(android.R.id.list)).setAdapter(getAdapter());

        return view;
    }
}
