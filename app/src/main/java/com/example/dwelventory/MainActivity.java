package com.example.dwelventory;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AddItemFragment.OnFragmentInteractionListener {

    private ArrayList<Item> dataList;
    private ListView itemList;
    private ArrayAdapter<Item> itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataList = new ArrayList<>();

        itemAdapter = new CustomList(this, dataList);
        itemList = findViewById(R.id.item_list);
        itemList.setAdapter(itemAdapter);

        final FloatingActionButton addButton = findViewById(R.id.add_item_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public void onOKPressed(Item item) {
        dataList.add(item);
        itemAdapter.notifyDataSetChanged();
    }
}
