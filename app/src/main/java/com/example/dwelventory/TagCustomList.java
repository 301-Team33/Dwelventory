package com.example.dwelventory;

import static androidx.core.content.res.ResourcesCompat.getColor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class TagCustomList extends ArrayAdapter<Tag> {

    private ArrayList<Tag> tags;
    private ArrayList<Tag> currentlyApplied; // used for items that already have specified tags// were created already.
    private Context context;

    public TagCustomList(Context context, ArrayList<Tag> tags,ArrayList<Tag> currentlyApplied){
        super(context,0,tags);
        this.tags = tags;
        this.context = context;
        this.currentlyApplied = currentlyApplied;
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
        if (currentlyApplied != null) {
            for (Tag appliedTagName : currentlyApplied) {
                if (appliedTagName.getTagName().equals(tag.getTagName())) {
                    // This is the case where the item already has tags specified to it that are
                    // in the database, we set it's background to the selected option.
                    view.setBackgroundColor(ContextCompat.getColor(context,R.color.selected));
                    return view;
                } else {
                    // else the item doesnt have this tag so it is not implied to be selected through the UI.
                    view.setBackgroundColor(ContextCompat.getColor(context,R.color.gray));
                }
            }
        }


        return view;
    }
}
