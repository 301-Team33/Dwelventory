package com.example.dwelventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ItemList extends ArrayAdapter<Item> {
    private final Context context;
    private ArrayList<Item> items;

    public ItemList(Context context, ArrayList<Item> items){
        super(context,android.R.layout.simple_list_item_multiple_choice, items);
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_content,parent,false);
        }

        Item item = items.get(position);
        TextView description = view.findViewById(R.id.description); // Updated ID here
        TextView estValue = view.findViewById(R.id.estValue); // Updated ID here

        description.setText(item.getDescription());
        estValue.setText(String.valueOf(item.getEstValue()));

        ImageView image = view.findViewById(R.id.itemImage);
        image.setImageResource(R.drawable.goat);

        // Get the checkbox and set its state based on the item's selection status
        CheckBox checkBox = view.findViewById(R.id.checkbox);
        checkBox.setChecked(item.isSelected());

        return view;
    }
}
