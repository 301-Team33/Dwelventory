package com.example.dwelventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class TagCustomList extends ArrayAdapter<Tag> {

    private ArrayList<Tag> tags;
    private Context context;

    public TagCustomList(Context context, ArrayList<Tag> tags){
        super(context,0,tags);
        this.tags = tags;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.tag_content,parent,false);
        }

        Tag tag = tags.get(position);
        TextView tagName = view.findViewById(R.id.tag_text);
        tagName.setText(tag.getTagName());

        return view;
    }
}
