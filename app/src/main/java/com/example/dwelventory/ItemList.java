package com.example.dwelventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ItemList extends ArrayAdapter<Item> {
    private final Context context;
    private ArrayList<Item> items;

    public ItemList(Context context, ArrayList<Item> items){
        super(context,0, items);
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content,parent,false);
        }

        Item item = items.get(position);
        TextView description = view.findViewById(R.id.descriptionMain);
        TextView estValue = view.findViewById(R.id.estValueMain);

        description.setText(item.getDescription());
        estValue.setText(String.valueOf(item.getEstValue()));

        ImageView image = view.findViewById(R.id.itemImage);
        image.setImageResource(R.drawable.goat);

        return view;
    }
}
